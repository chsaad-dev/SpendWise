package com.example.spendwise.ui.fragments

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spendwise.R
import com.example.spendwise.data.SplitGroup
import com.example.spendwise.data.SplitMember
import com.example.spendwise.ui.viewmodel.MainViewModel
import com.example.spendwise.utils.CurrencyHelper
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText

class SplitExpenseFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private val members = mutableListOf<MemberEntry>()
    private lateinit var memberAdapter: MemberAdapter
    private var isEqualSplit = true
    private var expenseId: Int = -1
    private var expenseAmount: Double = 0.0
    private var expenseCategory: String = ""

    data class MemberEntry(
        val name: String,
        var shareAmount: Double = 0.0,
        var isPaid: Boolean = false
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_split_expense, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        expenseId    = arguments?.getInt("expenseId", -1) ?: -1
        expenseAmount = (arguments?.getFloat("expenseAmount", 0f) ?: 0f).toDouble()
        expenseCategory = arguments?.getString("expenseCategory", "") ?: ""

        val btnBack         = view.findViewById<ImageButton>(R.id.btn_back)
        val tvInfo          = view.findViewById<TextView>(R.id.tv_split_expense_info)
        val tvTotal         = view.findViewById<TextView>(R.id.tv_split_total)
        val etDescription   = view.findViewById<TextInputEditText>(R.id.et_split_description)
        val etMemberName    = view.findViewById<TextInputEditText>(R.id.et_member_name)
        val btnAddMember    = view.findViewById<MaterialButton>(R.id.btn_add_member)
        val rvMembers       = view.findViewById<RecyclerView>(R.id.rv_members)
        val btnSave         = view.findViewById<MaterialButton>(R.id.btn_save_split)
        val cardSplitEqual  = view.findViewById<MaterialCardView>(R.id.card_split_equal)
        val tvSplitEqual    = view.findViewById<TextView>(R.id.tv_split_equal)
        val cardSplitCustom = view.findViewById<MaterialCardView>(R.id.card_split_custom)
        val tvSplitCustom   = view.findViewById<TextView>(R.id.tv_split_custom)

        val currencyFormat = CurrencyHelper.getCurrencyFormat(requireContext())

        tvInfo.text  = "$expenseCategory expense"
        tvTotal.text = currencyFormat.format(expenseAmount)

        memberAdapter = MemberAdapter(members, isEqualSplit) {
            recalculateEqualSplits()
            memberAdapter.notifyDataSetChanged()
        }
        rvMembers.layoutManager = LinearLayoutManager(context)
        rvMembers.adapter = memberAdapter

        btnBack.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            findNavController().popBackStack()
        }

        fun setSplitMode(equalSplit: Boolean) {
            isEqualSplit = equalSplit
            memberAdapter.isEqualSplit = equalSplit
            val density = resources.displayMetrics.density

            if (equalSplit) {
                cardSplitEqual.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.bg_surface))
                cardSplitEqual.cardElevation = 2f * density
                tvSplitEqual.setTextColor(ContextCompat.getColor(requireContext(), R.color.brand_primary))
                tvSplitEqual.typeface = Typeface.create("sans-serif-medium", Typeface.BOLD)

                cardSplitCustom.setCardBackgroundColor(Color.TRANSPARENT)
                cardSplitCustom.cardElevation = 0f
                tvSplitCustom.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
                tvSplitCustom.typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)

                recalculateEqualSplits()
            } else {
                cardSplitCustom.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.bg_surface))
                cardSplitCustom.cardElevation = 2f * density
                tvSplitCustom.setTextColor(ContextCompat.getColor(requireContext(), R.color.brand_primary))
                tvSplitCustom.typeface = Typeface.create("sans-serif-medium", Typeface.BOLD)

                cardSplitEqual.setCardBackgroundColor(Color.TRANSPARENT)
                cardSplitEqual.cardElevation = 0f
                tvSplitEqual.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
                tvSplitEqual.typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
            }
            memberAdapter.notifyDataSetChanged()
        }

        setSplitMode(true)

        cardSplitEqual.setOnClickListener {
            if (!isEqualSplit) {
                it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                setSplitMode(true)
            }
        }
        cardSplitCustom.setOnClickListener {
            if (isEqualSplit) {
                it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                setSplitMode(false)
            }
        }

        btnAddMember.setOnClickListener {
            val name = etMemberName.text.toString().trim()
            if (name.isBlank()) {
                Toast.makeText(context, "Enter a member name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            members.add(MemberEntry(name))
            recalculateEqualSplits()
            memberAdapter.notifyItemInserted(members.size - 1)
            etMemberName.text?.clear()
        }

        btnSave.setOnClickListener {
            if (members.isEmpty()) {
                Toast.makeText(context, "Add at least one member", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)

            val description = etDescription.text.toString().trim().ifBlank { "$expenseCategory split" }

            val group = SplitGroup(
                expenseId = expenseId,
                totalAmount = expenseAmount,
                description = description
            )

            viewModel.insertSplitGroup(group) { groupId ->
                for (member in members) {
                    val amount = if (isEqualSplit) expenseAmount / members.size else member.shareAmount
                    viewModel.insertSplitMember(
                        SplitMember(
                            groupId = groupId.toInt(),
                            name = member.name,
                            shareAmount = amount,
                            isPaid = member.isPaid
                        )
                    )
                }
                requireActivity().runOnUiThread {
                    Toast.makeText(context, "Split saved ✓", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun recalculateEqualSplits() {
        if (isEqualSplit && members.isNotEmpty()) {
            val share = expenseAmount / members.size
            members.forEach { it.shareAmount = share }
        }
    }

    class MemberAdapter(
        private val members: MutableList<MemberEntry>,
        var isEqualSplit: Boolean,
        private val onDelete: () -> Unit
    ) : RecyclerView.Adapter<MemberAdapter.VH>() {

        class VH(view: View) : RecyclerView.ViewHolder(view) {
            val tvName: TextView         = view.findViewById(R.id.tv_member_name)
            val tvShare: TextView        = view.findViewById(R.id.tv_member_share)
            val etAmount: TextInputEditText = view.findViewById(R.id.et_member_amount)
            val btnDelete: ImageButton   = view.findViewById(R.id.btn_delete_member)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_split_member, parent, false)
            return VH(v)
        }

        override fun getItemCount() = members.size

        override fun onBindViewHolder(holder: VH, position: Int) {
            val member = members[position]
            holder.tvName.text = member.name

            if (isEqualSplit) {
                val fmt = CurrencyHelper.getCurrencyFormat(holder.itemView.context)
                holder.tvShare.text = fmt.format(member.shareAmount)
                holder.tvShare.visibility  = View.VISIBLE
                holder.etAmount.visibility = View.GONE
            } else {
                holder.tvShare.visibility  = View.GONE
                holder.etAmount.visibility = View.VISIBLE
                if (member.shareAmount > 0) {
                    holder.etAmount.setText(String.format(java.util.Locale.US, "%.2f", member.shareAmount))
                } else {
                    holder.etAmount.text?.clear()
                }
                holder.etAmount.setOnFocusChangeListener { _, hasFocus ->
                    if (!hasFocus) {
                        member.shareAmount = holder.etAmount.text.toString().toDoubleOrNull() ?: 0.0
                    }
                }
            }

            holder.btnDelete.setOnClickListener {
                val pos = holder.adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    members.removeAt(pos)
                    notifyItemRemoved(pos)
                    onDelete()
                }
            }
        }
    }
}

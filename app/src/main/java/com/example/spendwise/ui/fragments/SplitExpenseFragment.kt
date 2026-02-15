package com.example.spendwise.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spendwise.R
import com.example.spendwise.data.SplitGroup
import com.example.spendwise.data.SplitMember
import com.example.spendwise.ui.viewmodel.MainViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import java.text.NumberFormat
import java.util.Locale

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

        expenseId = arguments?.getInt("expenseId", -1) ?: -1
        expenseAmount = (arguments?.getFloat("expenseAmount", 0f) ?: 0f).toDouble()
        expenseCategory = arguments?.getString("expenseCategory", "") ?: ""

        val tvInfo = view.findViewById<TextView>(R.id.tv_split_expense_info)
        val tvTotal = view.findViewById<TextView>(R.id.tv_split_total)
        val etDescription = view.findViewById<TextInputEditText>(R.id.et_split_description)
        val etMemberName = view.findViewById<TextInputEditText>(R.id.et_member_name)
        val btnAddMember = view.findViewById<MaterialButton>(R.id.btn_add_member)
        val rvMembers = view.findViewById<RecyclerView>(R.id.rv_members)
        val btnSave = view.findViewById<MaterialButton>(R.id.btn_save_split)
        val chipEqual = view.findViewById<Chip>(R.id.chip_equal)
        val chipCustom = view.findViewById<Chip>(R.id.chip_custom)

        val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())

        tvInfo.text = "$expenseCategory expense"
        tvTotal.text = currencyFormat.format(expenseAmount)

        memberAdapter = MemberAdapter(members, isEqualSplit) {
            recalculateEqualSplits()
            memberAdapter.notifyDataSetChanged()
        }
        rvMembers.layoutManager = LinearLayoutManager(context)
        rvMembers.adapter = memberAdapter

        chipEqual.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                isEqualSplit = true
                memberAdapter.isEqualSplit = true
                recalculateEqualSplits()
                memberAdapter.notifyDataSetChanged()
            }
        }

        chipCustom.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                isEqualSplit = false
                memberAdapter.isEqualSplit = false
                memberAdapter.notifyDataSetChanged()
            }
        }

        btnAddMember.setOnClickListener {
            val name = etMemberName.text.toString().trim()
            if (name.isBlank()) {
                Toast.makeText(context, "Enter a member name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
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

            val description = etDescription.text.toString().ifBlank { "$expenseCategory split" }

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
                    Toast.makeText(context, "Split saved \u2713", Toast.LENGTH_SHORT).show()
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

    // ── Inner Adapter ──
    class MemberAdapter(
        private val members: MutableList<MemberEntry>,
        var isEqualSplit: Boolean,
        private val onDelete: () -> Unit
    ) : RecyclerView.Adapter<MemberAdapter.VH>() {

        private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())

        class VH(view: View) : RecyclerView.ViewHolder(view) {
            val tvName: TextView = view.findViewById(R.id.tv_member_name)
            val etAmount: TextInputEditText = view.findViewById(R.id.et_member_amount)
            val cbPaid: MaterialCheckBox = view.findViewById(R.id.cb_paid)
            val btnDelete: ImageButton = view.findViewById(R.id.btn_delete_member)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_split_member, parent, false)
            return VH(view)
        }

        override fun getItemCount() = members.size

        override fun onBindViewHolder(holder: VH, position: Int) {
            val member = members[position]
            holder.tvName.text = member.name

            if (isEqualSplit) {
                holder.etAmount.setText(currencyFormat.format(member.shareAmount))
                holder.etAmount.isEnabled = false
            } else {
                holder.etAmount.isEnabled = true
                if (member.shareAmount > 0) {
                    holder.etAmount.setText(String.format("%.2f", member.shareAmount))
                } else {
                    holder.etAmount.text?.clear()
                }
            }

            holder.cbPaid.isChecked = member.isPaid
            holder.cbPaid.setOnCheckedChangeListener { _, isChecked ->
                member.isPaid = isChecked
            }

            holder.etAmount.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus && !isEqualSplit) {
                    member.shareAmount = holder.etAmount.text.toString().toDoubleOrNull() ?: 0.0
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

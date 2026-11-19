package com.example.spendwise.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spendwise.R
import com.example.spendwise.data.SavingsGoal
import com.example.spendwise.ui.dialogs.AddSavingsBottomSheet
import com.example.spendwise.ui.dialogs.DeleteGoalBottomSheet
import com.example.spendwise.ui.dialogs.NewGoalBottomSheet
import com.example.spendwise.ui.viewmodel.MainViewModel
import com.example.spendwise.utils.CurrencyHelper
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import java.text.SimpleDateFormat
import java.util.Locale

class SavingsGoalFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private var goalAdapter: GoalAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_savings_goals, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvGoals = view.findViewById<RecyclerView>(R.id.rv_goals)
        val layoutEmpty = view.findViewById<LinearLayout>(R.id.layout_empty)
        val btnAddGoal = view.findViewById<MaterialButton>(R.id.btn_add_goal)
        val btnEmptyAddGoal = view.findViewById<MaterialButton>(R.id.btn_empty_add_goal)

        val adapter = GoalAdapter(
            onAddSavings = { goal -> showAddSavingsDialog(goal) },
            onDelete = { goal -> showDeleteGoalDialog(goal) }
        )

        goalAdapter = adapter
        rvGoals.layoutManager = LinearLayoutManager(context)
        rvGoals.adapter = adapter

        fun updateEmptyState(goals: List<SavingsGoal>?) {
            val isLoaded = viewModel.isInitialDataLoaded.value == true
            val isEmpty = goals.isNullOrEmpty()
            layoutEmpty.visibility = if (isLoaded && isEmpty) View.VISIBLE else View.GONE
            rvGoals.visibility = if (isEmpty) View.GONE else View.VISIBLE
        }

        viewModel.isInitialDataLoaded.observe(viewLifecycleOwner) {
            updateEmptyState(viewModel.allSavingsGoals.value)
        }

        viewModel.allSavingsGoals.observe(viewLifecycleOwner) { goals ->
            if (goals != null) {
                viewModel.markInitialDataLoaded()
            }
            adapter.submitList(goals)
            updateEmptyState(goals)
        }

        btnAddGoal.setOnClickListener {
            showAddGoalDialog()
        }

        btnEmptyAddGoal?.setOnClickListener {
            showAddGoalDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        goalAdapter?.notifyDataSetChanged()
    }

    private fun showAddGoalDialog() {
        NewGoalBottomSheet.newInstance { name, target, deadline ->
            viewModel.insertGoal(
                SavingsGoal(
                    name = name,
                    targetAmount = target,
                    deadline = deadline
                )
            )
            Toast.makeText(context, "Goal created ✓", Toast.LENGTH_SHORT).show()
        }.show(childFragmentManager, NewGoalBottomSheet.TAG)
    }

    private fun showAddSavingsDialog(goal: SavingsGoal) {
        AddSavingsBottomSheet.newInstance(goal) { amount ->
            val updatedGoal = goal.copy(savedAmount = goal.savedAmount + amount)
            viewModel.updateGoal(updatedGoal)

            if (updatedGoal.savedAmount >= updatedGoal.targetAmount) {
                Toast.makeText(context, "\uD83C\uDF89 Goal reached!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Savings added ✓", Toast.LENGTH_SHORT).show()
            }
        }.show(childFragmentManager, AddSavingsBottomSheet.TAG)
    }

    private fun showDeleteGoalDialog(goal: SavingsGoal) {
        DeleteGoalBottomSheet.newInstance(goal) {
            viewModel.deleteGoal(goal)
            Toast.makeText(context, "Goal deleted", Toast.LENGTH_SHORT).show()
        }.show(childFragmentManager, DeleteGoalBottomSheet.TAG)
    }

    // ── Adapter ──
    class GoalAdapter(
        private val onAddSavings: (SavingsGoal) -> Unit,
        private val onDelete: (SavingsGoal) -> Unit
    ) : RecyclerView.Adapter<GoalAdapter.VH>() {

        private var goals = listOf<SavingsGoal>()
        private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

        fun submitList(list: List<SavingsGoal>) {
            goals = list
            notifyDataSetChanged()
        }

        class VH(view: View) : RecyclerView.ViewHolder(view) {
            val tvName: TextView = view.findViewById(R.id.tv_goal_name)
            val tvDeadline: TextView = view.findViewById(R.id.tv_goal_deadline)
            val tvPercent: TextView = view.findViewById(R.id.tv_goal_percent)
            val progressGoal: LinearProgressIndicator = view.findViewById(R.id.progress_goal)
            val tvSaved: TextView = view.findViewById(R.id.tv_goal_saved)
            val tvTarget: TextView = view.findViewById(R.id.tv_goal_target)
            val btnAddSavings: MaterialButton = view.findViewById(R.id.btn_add_savings)
            val btnDelete: MaterialButton = view.findViewById(R.id.btn_delete_goal)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_savings_goal, parent, false)
            return VH(view)
        }

        override fun getItemCount() = goals.size

        override fun onBindViewHolder(holder: VH, position: Int) {
            val goal = goals[position]
            val context = holder.itemView.context

            holder.tvName.text = goal.name
            holder.tvDeadline.text = "Target: ${dateFormat.format(goal.deadline)}"

            val progress = if (goal.targetAmount > 0)
                ((goal.savedAmount / goal.targetAmount) * 100).toInt().coerceAtMost(100)
            else 0

            holder.progressGoal.progress = progress
            holder.tvPercent.text = "$progress%"

            if (progress >= 100) {
                holder.tvPercent.setBackgroundResource(R.drawable.bg_pill_percent_completed)
                holder.tvPercent.setTextColor(ContextCompat.getColor(context, R.color.success_primary))
                holder.progressGoal.setIndicatorColor(ContextCompat.getColor(context, R.color.success_primary))
            } else {
                holder.tvPercent.setBackgroundResource(R.drawable.bg_pill_percent_active)
                holder.tvPercent.setTextColor(ContextCompat.getColor(context, R.color.brand_primary))
                holder.progressGoal.setIndicatorColor(ContextCompat.getColor(context, R.color.brand_primary))
            }

            val fmtSaved = CurrencyHelper.getCurrencyFormat(context).apply {
                if (goal.savedAmount % 1.0 == 0.0) maximumFractionDigits = 0
            }
            val fmtTarget = CurrencyHelper.getCurrencyFormat(context).apply {
                if (goal.targetAmount % 1.0 == 0.0) maximumFractionDigits = 0
            }

            holder.tvSaved.text = "${fmtSaved.format(goal.savedAmount)} saved"
            holder.tvTarget.text = "of ${fmtTarget.format(goal.targetAmount)}"

            holder.btnAddSavings.setOnClickListener { onAddSavings(goal) }
            holder.btnDelete.setOnClickListener { onDelete(goal) }
        }
    }
}

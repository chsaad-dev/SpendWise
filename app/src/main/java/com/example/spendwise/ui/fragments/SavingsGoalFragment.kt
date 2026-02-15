package com.example.spendwise.ui.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spendwise.R
import com.example.spendwise.data.SavingsGoal
import com.example.spendwise.ui.viewmodel.MainViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SavingsGoalFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()

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

        val adapter = GoalAdapter(
            onAddSavings = { goal -> showAddSavingsDialog(goal) },
            onDelete = { goal ->
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Delete Goal")
                    .setMessage("Delete \"${goal.name}\"?")
                    .setPositiveButton("Delete") { _, _ -> viewModel.deleteGoal(goal) }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        )

        rvGoals.layoutManager = LinearLayoutManager(context)
        rvGoals.adapter = adapter

        viewModel.allSavingsGoals.observe(viewLifecycleOwner) { goals ->
            adapter.submitList(goals)
            layoutEmpty.visibility = if (goals.isEmpty()) View.VISIBLE else View.GONE
            rvGoals.visibility = if (goals.isEmpty()) View.GONE else View.VISIBLE
        }

        btnAddGoal.setOnClickListener {
            showAddGoalDialog()
        }
    }

    private fun showAddGoalDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_goal, null)

        val etName = dialogView.findViewById<TextInputEditText>(R.id.et_goal_name)
        val etTarget = dialogView.findViewById<TextInputEditText>(R.id.et_goal_target)
        val etDeadline = dialogView.findViewById<TextInputEditText>(R.id.et_goal_deadline)

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, 3) // Default: 3 months from now

        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        etDeadline.setText(dateFormat.format(calendar.time))

        etDeadline.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    calendar.set(year, month, day)
                    etDeadline.setText(dateFormat.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("New Savings Goal")
            .setView(dialogView)
            .setPositiveButton("Create") { _, _ ->
                val name = etName.text.toString().trim()
                val target = etTarget.text.toString().toDoubleOrNull()

                if (name.isBlank() || target == null || target <= 0) {
                    Toast.makeText(context, "Please enter a valid name and target", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                viewModel.insertGoal(
                    SavingsGoal(
                        name = name,
                        targetAmount = target,
                        deadline = calendar.timeInMillis
                    )
                )
                Toast.makeText(context, "Goal created \u2713", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showAddSavingsDialog(goal: SavingsGoal) {
        val input = EditText(requireContext()).apply {
            hint = "Amount to add"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            setPadding(64, 32, 64, 32)
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add Savings to \"${goal.name}\"")
            .setView(input)
            .setPositiveButton("Add") { _, _ ->
                val amount = input.text.toString().toDoubleOrNull()
                if (amount == null || amount <= 0) {
                    Toast.makeText(context, "Enter a valid amount", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val updatedGoal = goal.copy(savedAmount = goal.savedAmount + amount)
                viewModel.updateGoal(updatedGoal)

                if (updatedGoal.savedAmount >= updatedGoal.targetAmount) {
                    Toast.makeText(context, "\uD83C\uDF89 Goal reached!", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Savings added \u2713", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // ── Adapter ──
    class GoalAdapter(
        private val onAddSavings: (SavingsGoal) -> Unit,
        private val onDelete: (SavingsGoal) -> Unit
    ) : RecyclerView.Adapter<GoalAdapter.VH>() {

        private var goals = listOf<SavingsGoal>()
        private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
        private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

        fun submitList(list: List<SavingsGoal>) {
            goals = list
            notifyDataSetChanged()
        }

        class VH(view: View) : RecyclerView.ViewHolder(view) {
            val tvName: TextView = view.findViewById(R.id.tv_goal_name)
            val tvDeadline: TextView = view.findViewById(R.id.tv_goal_deadline)
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
            holder.tvName.text = goal.name
            holder.tvDeadline.text = dateFormat.format(goal.deadline)

            val progress = if (goal.targetAmount > 0)
                ((goal.savedAmount / goal.targetAmount) * 100).toInt().coerceAtMost(100)
            else 0

            holder.progressGoal.progress = progress
            holder.tvSaved.text = "${currencyFormat.format(goal.savedAmount)} saved"
            holder.tvTarget.text = "of ${currencyFormat.format(goal.targetAmount)}"

            holder.btnAddSavings.setOnClickListener { onAddSavings(goal) }
            holder.btnDelete.setOnClickListener { onDelete(goal) }
        }
    }
}

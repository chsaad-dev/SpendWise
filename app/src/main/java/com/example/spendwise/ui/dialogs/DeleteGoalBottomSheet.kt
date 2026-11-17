package com.example.spendwise.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.spendwise.R
import com.example.spendwise.data.SavingsGoal
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton

class DeleteGoalBottomSheet : BottomSheetDialogFragment() {

    private var goal: SavingsGoal? = null
    private var onConfirmDeleteCallback: (() -> Unit)? = null

    override fun getTheme(): Int = R.style.Theme_SpendWise_BottomSheetDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_delete_goal, container, false)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)?.let { sheet ->
            sheet.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_bottom_sheet)
            BottomSheetBehavior.from(sheet).apply {
                state = BottomSheetBehavior.STATE_EXPANDED
                skipCollapsed = true
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val g = goal ?: return

        val tvTitle = view.findViewById<TextView>(R.id.tv_delete_goal_title)
        val btnDelete = view.findViewById<MaterialButton>(R.id.btn_confirm_delete_goal)
        val btnCancel = view.findViewById<MaterialButton>(R.id.btn_cancel_delete_goal)

        tvTitle.text = "Delete \"${g.name}\"?"

        btnDelete.setOnClickListener {
            onConfirmDeleteCallback?.invoke()
            dismiss()
        }

        btnCancel.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        const val TAG = "DeleteGoalBottomSheet"

        fun newInstance(
            goal: SavingsGoal,
            onConfirmDelete: () -> Unit
        ): DeleteGoalBottomSheet {
            return DeleteGoalBottomSheet().apply {
                this.goal = goal
                this.onConfirmDeleteCallback = onConfirmDelete
            }
        }
    }
}

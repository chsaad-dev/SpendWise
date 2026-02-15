package com.example.spendwise.ui.fragments

import android.os.Bundle
import androidx.navigation.NavDirections
import com.example.spendwise.R
import kotlin.Float
import kotlin.Int
import kotlin.String

public class AddExpenseFragmentDirections private constructor() {
  private data class ActionAddExpenseToSplit(
    public val expenseId: Int = -1,
    public val expenseAmount: Float = 0F,
    public val expenseCategory: String = "",
  ) : NavDirections {
    public override val actionId: Int = R.id.action_addExpense_to_split

    public override val arguments: Bundle
      get() {
        val result = Bundle()
        result.putInt("expenseId", this.expenseId)
        result.putFloat("expenseAmount", this.expenseAmount)
        result.putString("expenseCategory", this.expenseCategory)
        return result
      }
  }

  public companion object {
    public fun actionAddExpenseToSplit(
      expenseId: Int = -1,
      expenseAmount: Float = 0F,
      expenseCategory: String = "",
    ): NavDirections = ActionAddExpenseToSplit(expenseId, expenseAmount, expenseCategory)
  }
}

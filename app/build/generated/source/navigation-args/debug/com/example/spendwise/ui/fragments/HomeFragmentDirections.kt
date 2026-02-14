package com.example.spendwise.ui.fragments

import android.os.Bundle
import androidx.navigation.NavDirections
import com.example.spendwise.R
import kotlin.Int

public class HomeFragmentDirections private constructor() {
  private data class ActionHomeToAddExpense(
    public val expenseId: Int = -1,
  ) : NavDirections {
    public override val actionId: Int = R.id.action_home_to_addExpense

    public override val arguments: Bundle
      get() {
        val result = Bundle()
        result.putInt("expenseId", this.expenseId)
        return result
      }
  }

  public companion object {
    public fun actionHomeToAddExpense(expenseId: Int = -1): NavDirections =
        ActionHomeToAddExpense(expenseId)
  }
}

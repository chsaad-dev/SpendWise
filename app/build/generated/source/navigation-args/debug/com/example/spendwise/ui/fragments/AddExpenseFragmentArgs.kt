package com.example.spendwise.ui.fragments

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavArgs
import java.lang.IllegalArgumentException
import kotlin.Int
import kotlin.jvm.JvmStatic

public data class AddExpenseFragmentArgs(
  public val expenseId: Int = -1,
) : NavArgs {
  public fun toBundle(): Bundle {
    val result = Bundle()
    result.putInt("expenseId", this.expenseId)
    return result
  }

  public fun toSavedStateHandle(): SavedStateHandle {
    val result = SavedStateHandle()
    result.set("expenseId", this.expenseId)
    return result
  }

  public companion object {
    @JvmStatic
    public fun fromBundle(bundle: Bundle): AddExpenseFragmentArgs {
      bundle.setClassLoader(AddExpenseFragmentArgs::class.java.classLoader)
      val __expenseId : Int
      if (bundle.containsKey("expenseId")) {
        __expenseId = bundle.getInt("expenseId")
      } else {
        __expenseId = -1
      }
      return AddExpenseFragmentArgs(__expenseId)
    }

    @JvmStatic
    public fun fromSavedStateHandle(savedStateHandle: SavedStateHandle): AddExpenseFragmentArgs {
      val __expenseId : Int?
      if (savedStateHandle.contains("expenseId")) {
        __expenseId = savedStateHandle["expenseId"]
        if (__expenseId == null) {
          throw IllegalArgumentException("Argument \"expenseId\" of type integer does not support null values")
        }
      } else {
        __expenseId = -1
      }
      return AddExpenseFragmentArgs(__expenseId)
    }
  }
}

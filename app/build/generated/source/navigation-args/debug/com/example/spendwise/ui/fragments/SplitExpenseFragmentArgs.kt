package com.example.spendwise.ui.fragments

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavArgs
import java.lang.IllegalArgumentException
import kotlin.Float
import kotlin.Int
import kotlin.String
import kotlin.jvm.JvmStatic

public data class SplitExpenseFragmentArgs(
  public val expenseId: Int = -1,
  public val expenseAmount: Float = 0F,
  public val expenseCategory: String = "",
) : NavArgs {
  public fun toBundle(): Bundle {
    val result = Bundle()
    result.putInt("expenseId", this.expenseId)
    result.putFloat("expenseAmount", this.expenseAmount)
    result.putString("expenseCategory", this.expenseCategory)
    return result
  }

  public fun toSavedStateHandle(): SavedStateHandle {
    val result = SavedStateHandle()
    result.set("expenseId", this.expenseId)
    result.set("expenseAmount", this.expenseAmount)
    result.set("expenseCategory", this.expenseCategory)
    return result
  }

  public companion object {
    @JvmStatic
    public fun fromBundle(bundle: Bundle): SplitExpenseFragmentArgs {
      bundle.setClassLoader(SplitExpenseFragmentArgs::class.java.classLoader)
      val __expenseId : Int
      if (bundle.containsKey("expenseId")) {
        __expenseId = bundle.getInt("expenseId")
      } else {
        __expenseId = -1
      }
      val __expenseAmount : Float
      if (bundle.containsKey("expenseAmount")) {
        __expenseAmount = bundle.getFloat("expenseAmount")
      } else {
        __expenseAmount = 0F
      }
      val __expenseCategory : String?
      if (bundle.containsKey("expenseCategory")) {
        __expenseCategory = bundle.getString("expenseCategory")
        if (__expenseCategory == null) {
          throw IllegalArgumentException("Argument \"expenseCategory\" is marked as non-null but was passed a null value.")
        }
      } else {
        __expenseCategory = ""
      }
      return SplitExpenseFragmentArgs(__expenseId, __expenseAmount, __expenseCategory)
    }

    @JvmStatic
    public fun fromSavedStateHandle(savedStateHandle: SavedStateHandle): SplitExpenseFragmentArgs {
      val __expenseId : Int?
      if (savedStateHandle.contains("expenseId")) {
        __expenseId = savedStateHandle["expenseId"]
        if (__expenseId == null) {
          throw IllegalArgumentException("Argument \"expenseId\" of type integer does not support null values")
        }
      } else {
        __expenseId = -1
      }
      val __expenseAmount : Float?
      if (savedStateHandle.contains("expenseAmount")) {
        __expenseAmount = savedStateHandle["expenseAmount"]
        if (__expenseAmount == null) {
          throw IllegalArgumentException("Argument \"expenseAmount\" of type float does not support null values")
        }
      } else {
        __expenseAmount = 0F
      }
      val __expenseCategory : String?
      if (savedStateHandle.contains("expenseCategory")) {
        __expenseCategory = savedStateHandle["expenseCategory"]
        if (__expenseCategory == null) {
          throw IllegalArgumentException("Argument \"expenseCategory\" is marked as non-null but was passed a null value")
        }
      } else {
        __expenseCategory = ""
      }
      return SplitExpenseFragmentArgs(__expenseId, __expenseAmount, __expenseCategory)
    }
  }
}

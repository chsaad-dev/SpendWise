package com.example.spendwise.ui.fragments

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.example.spendwise.R

public class LoginFragmentDirections private constructor() {
  public companion object {
    public fun actionLoginToHome(): NavDirections =
        ActionOnlyNavDirections(R.id.action_login_to_home)
  }
}

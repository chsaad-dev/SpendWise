package com.example.spendwise.ui.fragments

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.example.spendwise.R

public class WelcomeFragmentDirections private constructor() {
  public companion object {
    public fun actionWelcomeToHome(): NavDirections =
        ActionOnlyNavDirections(R.id.action_welcome_to_home)
  }
}

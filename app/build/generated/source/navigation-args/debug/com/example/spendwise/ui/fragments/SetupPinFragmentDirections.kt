package com.example.spendwise.ui.fragments

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.example.spendwise.R

public class SetupPinFragmentDirections private constructor() {
  public companion object {
    public fun actionSetupToHome(): NavDirections =
        ActionOnlyNavDirections(R.id.action_setup_to_home)
  }
}

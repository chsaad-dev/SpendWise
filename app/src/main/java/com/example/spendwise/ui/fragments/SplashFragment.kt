package com.example.spendwise.ui.fragments

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.spendwise.R

class SplashFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val content = view.findViewById<View>(R.id.layout_splash_content)
        val bottom = view.findViewById<View>(R.id.layout_splash_bottom)

        content.alpha = 0f
        content.scaleX = 0.8f
        content.scaleY = 0.8f
        bottom.alpha = 0f
        bottom.translationY = 40f

        val contentFade = ObjectAnimator.ofFloat(content, "alpha", 0f, 1f).setDuration(800)
        val contentScaleX = ObjectAnimator.ofFloat(content, "scaleX", 0.8f, 1f).setDuration(800)
        val contentScaleY = ObjectAnimator.ofFloat(content, "scaleY", 0.8f, 1f).setDuration(800)
        contentScaleX.interpolator = OvershootInterpolator(1.5f)
        contentScaleY.interpolator = OvershootInterpolator(1.5f)

        val bottomFade = ObjectAnimator.ofFloat(bottom, "alpha", 0f, 1f).setDuration(600)
        val bottomSlide = ObjectAnimator.ofFloat(bottom, "translationY", 40f, 0f).setDuration(600)

        val set = AnimatorSet()
        set.playTogether(contentFade, contentScaleX, contentScaleY)
        set.play(bottomFade).with(bottomSlide).after(400)
        set.startDelay = 200
        set.start()

        Handler(Looper.getMainLooper()).postDelayed({
            val prefs = requireActivity().getSharedPreferences("SpendWisePrefs", Context.MODE_PRIVATE)
            val pinSetupDone = prefs.getBoolean("pin_setup_done", false)

            if (pinSetupDone) {
                findNavController().navigate(R.id.action_splash_to_login)
            } else {
                findNavController().navigate(R.id.action_splash_to_setup)
            }
        }, 2000)
    }
}

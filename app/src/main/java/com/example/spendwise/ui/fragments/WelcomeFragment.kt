package com.example.spendwise.ui.fragments

import android.content.Context
import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.spendwise.R
import com.example.spendwise.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment() {

    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val prefs = requireActivity().getSharedPreferences("SpendWisePrefs", Context.MODE_PRIVATE)
        val isFirstLaunch = prefs.getBoolean("is_first_launch", true)
        
        if (!isFirstLaunch) {
            findNavController().navigate(R.id.action_welcome_to_home)
            return View(requireContext())
        }
        
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        if (_binding == null) return // Fast bailout if skipped in onCreateView

        val pageCount = 3
        binding.viewPager.adapter = GuidePagerAdapter()

        setupIndicators(pageCount)
        setCurrentIndicator(0)

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
                
                if (position == pageCount - 1) {
                    binding.btnNext.text = "Get Started"
                    binding.btnSkip.animate().alpha(0f).setDuration(200).withEndAction {
                        binding.btnSkip.visibility = View.GONE
                    }.start()
                } else {
                    binding.btnNext.text = "Continue"
                    if (binding.btnSkip.visibility != View.VISIBLE) {
                        binding.btnSkip.visibility = View.VISIBLE
                        binding.btnSkip.alpha = 0f
                        binding.btnSkip.animate().alpha(1f).setDuration(200).start()
                    }
                }
            }
        })

        binding.btnNext.setOnClickListener {
            if (binding.viewPager.currentItem < pageCount - 1) {
                binding.viewPager.currentItem += 1
            } else {
                finishGuide()
            }
        }

        binding.btnSkip.setOnClickListener {
            finishGuide()
        }
    }

    private fun setupIndicators(count: Int) {
        binding.layoutIndicators.removeAllViews()
        val density = resources.displayMetrics.density
        val inactiveWidth = (7 * density).toInt()
        val height = (7 * density).toInt()
        val margin = (3 * density).toInt()

        for (i in 0 until count) {
            val indicator = View(requireContext())
            val params = LinearLayout.LayoutParams(inactiveWidth, height).apply {
                setMargins(margin, 0, margin, 0)
            }
            indicator.layoutParams = params
            indicator.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_indicator_inactive)
            binding.layoutIndicators.addView(indicator)
        }
    }

    private fun setCurrentIndicator(index: Int) {
        val density = resources.displayMetrics.density
        val activeWidth = (24 * density).toInt()
        val inactiveWidth = (7 * density).toInt()
        val height = (7 * density).toInt()
        val margin = (3 * density).toInt()

        TransitionManager.beginDelayedTransition(binding.layoutIndicators)

        for (i in 0 until binding.layoutIndicators.childCount) {
            val child = binding.layoutIndicators.getChildAt(i)
            val isActive = (i == index)
            val targetWidth = if (isActive) activeWidth else inactiveWidth
            val params = LinearLayout.LayoutParams(targetWidth, height).apply {
                setMargins(margin, 0, margin, 0)
            }
            child.layoutParams = params
            child.background = ContextCompat.getDrawable(
                requireContext(),
                if (isActive) R.drawable.bg_indicator_active else R.drawable.bg_indicator_inactive
            )
        }
    }

    private fun finishGuide() {
        val prefs = requireActivity().getSharedPreferences("SpendWisePrefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("is_first_launch", false).apply()
        findNavController().navigate(R.id.action_welcome_to_home)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

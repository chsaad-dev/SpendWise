package com.example.spendwise.ui.fragments

import android.content.Context
import android.os.Bundle
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
import com.google.android.material.button.MaterialButton

class WelcomeFragment : Fragment() {

    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val prefs = requireActivity().getSharedPreferences("SpendWisePrefs", Context.MODE_PRIVATE)
        val isFirstLaunch = prefs.getBoolean("is_first_launch", true)
        
        // Immediately bypass if not first launch
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

        val guideItems = listOf(
            GuideItem(
                "Track Expenses",
                "Log your daily spending easily and categorize it for better insights.",
                android.R.drawable.ic_menu_edit
            ),
            GuideItem(
                "Visualize Growth",
                "Understand where your money goes with intuitive charts and monthly summaries.",
                android.R.drawable.ic_menu_sort_by_size
            ),
            GuideItem(
                "Split & Save",
                "Split bills with friends and set visual savings goals to hit your targets.",
                android.R.drawable.ic_menu_myplaces
            )
        )

        val adapter = GuidePagerAdapter(guideItems)
        binding.viewPager.adapter = adapter

        setupIndicators(guideItems.size)
        setCurrentIndicator(0)

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
                
                if (position == guideItems.size - 1) {
                    binding.btnNext.text = "Get Started"
                } else {
                    binding.btnNext.text = "Next"
                }
            }
        })

        binding.btnNext.setOnClickListener {
            if (binding.viewPager.currentItem < guideItems.size - 1) {
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
        val indicators = arrayOfNulls<View>(count)
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(8, 0, 8, 0)
        }

        for (i in indicators.indices) {
            indicators[i] = View(requireContext())
            indicators[i]?.background = ContextCompat.getDrawable(
                requireContext(),
                android.R.drawable.presence_invisible // simple placeholder circle if needed
            )
            binding.layoutIndicators.addView(indicators[i], layoutParams)
        }
    }

    private fun setCurrentIndicator(index: Int) {
        for (i in 0 until binding.layoutIndicators.childCount) {
            val child = binding.layoutIndicators.getChildAt(i)
            if (i == index) {
                // simple hack to show active state without adding new drawables
                child.alpha = 1.0f 
                child.layoutParams = LinearLayout.LayoutParams(24, 24).apply { setMargins(8, 0, 8, 0) }
                child.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.background_dark))
            } else {
                child.alpha = 0.5f
                child.layoutParams = LinearLayout.LayoutParams(16, 16).apply { setMargins(8, 0, 8, 0) }
                child.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
            }
            // Add a small radius
            val shape = android.graphics.drawable.GradientDrawable()
            shape.shape = android.graphics.drawable.GradientDrawable.OVAL
            shape.setColor(child.solidColor)
            child.background = shape
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

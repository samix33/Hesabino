package com.example.hesabino.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.hesabino.R
import com.example.hesabino.model.adapter.OnboardingAdapter
import com.example.hesabino.model.data.OnboardingPage
import com.example.hesabino.databinding.ActivityOnboardingBinding
import com.example.hesabino.di.FragmentNavigator
import com.example.hesabino.ui.initialBalance.initial_balance_Fragment

class onboarding_Fragment : Fragment() {
    private lateinit var binding: ActivityOnboardingBinding
    private val pages = listOf(
        OnboardingPage(R.drawable.onboarding_1),
        OnboardingPage(R.drawable.onboarding_2),
        OnboardingPage(R.drawable.onboarding_3),
        OnboardingPage(R.drawable.onboarding_4)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        binding.viewPagerOnboarding.layoutDirection = View.LAYOUT_DIRECTION_LTR
        val adapter = OnboardingAdapter(pages)
        binding.viewPagerOnboarding.adapter = adapter
        setupDots()

        selectDot(0)
        binding.viewPagerOnboarding.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    selectDot(position)

                    binding.btnNext.text =
                        if (position == pages.lastIndex) "شروع" else "بعدی"
                }
            }
        )
        binding.btnNext.setOnClickListener {
            val current = binding.viewPagerOnboarding.currentItem

            if (current < pages.lastIndex) {
                binding.viewPagerOnboarding.setCurrentItem(current + 1, true)
            } else {
                finishOnboarding()
            }
        }

        binding.tvSkip.setOnClickListener {
            finishOnboarding()
        }

        binding.viewPagerOnboarding.setPageTransformer { page, position ->
            page.alpha = 1 - kotlin.math.abs(position) * 0.35f
            page.scaleY = 0.92f + (1 - kotlin.math.abs(position)) * 0.08f
            page.scaleX = 0.92f + (1 - kotlin.math.abs(position)) * 0.08f
        }
        return binding.root
    }




    private fun setupDots() {
        binding.dotsLayout.removeAllViews()

        repeat(pages.size) {
            val dot = View(  binding.root.context)
            val size = resources.getDimensionPixelSize(R.dimen.dot_size)

            val params = LinearLayout.LayoutParams(size, size)
            params.setMargins(6, 0, 6, 0)

            dot.layoutParams = params
            dot.background = ContextCompat.getDrawable(  binding.root.context, R.drawable.bg_dot_unselected)

            binding.dotsLayout.addView(dot)
        }
    }

    private fun selectDot(position: Int) {
        for (i in 0 until binding.dotsLayout.childCount) {
            val dot = binding.dotsLayout.getChildAt(i)

            dot.background = ContextCompat.getDrawable(
                binding.root.context,
                if (i == position) R.drawable.bg_dot_selected
                else R.drawable.bg_dot_unselected
            )
        }
    }
    private fun finishOnboarding() {
        FragmentNavigator.replace(
            parentFragmentManager,
            R.id.rootgragment,
            false,
            initial_balance_Fragment()
        )
    }
}
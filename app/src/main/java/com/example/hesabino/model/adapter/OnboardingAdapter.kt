package com.example.hesabino.model.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hesabino.model.data.OnboardingPage
import com.example.hesabino.databinding.ItemOnboardingSlideBinding


class OnboardingAdapter(
    private val pages: List<OnboardingPage>
) : RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    inner class OnboardingViewHolder(
        private val binding: ItemOnboardingSlideBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(page: OnboardingPage) {
            binding.imgOnboarding.setImageResource(page.imageRes)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OnboardingViewHolder {
        val binding = ItemOnboardingSlideBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OnboardingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        holder.bind(pages[position])
    }

    override fun getItemCount(): Int = pages.size
}
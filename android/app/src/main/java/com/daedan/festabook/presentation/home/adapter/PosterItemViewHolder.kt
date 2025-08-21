package com.daedan.festabook.presentation.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil3.request.CachePolicy
import coil3.request.transformations
import coil3.transform.RoundedCornersTransformation
import com.daedan.festabook.R
import com.daedan.festabook.databinding.ItemHomePosterBinding
import com.daedan.festabook.presentation.common.loadImage

class PosterItemViewHolder(
    val binding: ItemHomePosterBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(url: String) {
        binding.ivHomePoster.loadImage(url) {
            transformations(RoundedCornersTransformation(20f))
            memoryCachePolicy(CachePolicy.ENABLED)
        }
        binding.motionLayout.progress = 0f
        binding.motionLayout.transitionToState(R.id.collapsed)
    }

    fun transitionToExpanded() {
        binding.motionLayout.transitionToState(R.id.expanded)
    }

    fun transitionToCollapsed() {
        binding.motionLayout.transitionToState(R.id.collapsed)
    }

    companion object {
        fun from(parent: ViewGroup): PosterItemViewHolder {
            val binding =
                ItemHomePosterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return PosterItemViewHolder(binding)
        }
    }
}

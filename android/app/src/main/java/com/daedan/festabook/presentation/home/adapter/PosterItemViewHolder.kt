package com.daedan.festabook.presentation.home.adapter

import androidx.recyclerview.widget.RecyclerView
import com.daedan.festabook.R
import com.daedan.festabook.databinding.ItemHomePosterBinding

class PosterItemViewHolder(
    val binding: ItemHomePosterBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(poster: Int) {
        binding.ivHomePoster.setImageResource(poster)
        binding.motionLayout.progress = 0f
        binding.motionLayout.transitionToState(R.id.collapsed)
    }
}

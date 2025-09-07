package com.daedan.festabook.presentation.home.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class PosterAdapter : ListAdapter<String, PosterItemViewHolder>(posterDiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PosterItemViewHolder = PosterItemViewHolder.from(parent)

    override fun onBindViewHolder(
        holder: PosterItemViewHolder,
        position: Int,
    ) {
        val posterList = currentList
        if (posterList.isEmpty()) return

        val actualIndex = position % posterList.size
        holder.bind(posterList[actualIndex])
    }

    override fun getItemCount(): Int = Int.MAX_VALUE

    companion object {
        private val posterDiffCallback =
            object : DiffUtil.ItemCallback<String>() {
                override fun areItemsTheSame(
                    oldItem: String,
                    newItem: String,
                ): Boolean = oldItem == newItem

                override fun areContentsTheSame(
                    oldItem: String,
                    newItem: String,
                ): Boolean = oldItem == newItem
            }
    }
}

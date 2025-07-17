package com.daedan.festabook.presentation.placeDetail.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class PlaceImageViewPagerAdapter : ListAdapter<String, PlaceImageViewPagerViewHolder>(DIFF_UTIL_CALLBACK) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PlaceImageViewPagerViewHolder = PlaceImageViewPagerViewHolder.of(parent)

    override fun onBindViewHolder(
        holder: PlaceImageViewPagerViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_UTIL_CALLBACK =
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

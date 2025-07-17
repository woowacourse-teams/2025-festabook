package com.daedan.festabook.presentation.placeDetail.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.daedan.festabook.presentation.placeDetail.model.ImageUiModel

class PlaceImageViewPagerAdapter : ListAdapter<ImageUiModel, PlaceImageViewPagerViewHolder>(DIFF_UTIL_CALLBACK) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PlaceImageViewPagerViewHolder = PlaceImageViewPagerViewHolder.from(parent)

    override fun onBindViewHolder(
        holder: PlaceImageViewPagerViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_UTIL_CALLBACK =
            object : DiffUtil.ItemCallback<ImageUiModel>() {
                override fun areItemsTheSame(
                    oldItem: ImageUiModel,
                    newItem: ImageUiModel,
                ): Boolean = oldItem.id == newItem.id

                override fun areContentsTheSame(
                    oldItem: ImageUiModel,
                    newItem: ImageUiModel,
                ): Boolean = oldItem == newItem
            }
    }
}

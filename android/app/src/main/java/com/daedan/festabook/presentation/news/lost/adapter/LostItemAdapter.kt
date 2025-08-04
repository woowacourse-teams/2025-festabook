package com.daedan.festabook.presentation.news.lost.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.daedan.festabook.presentation.news.lost.model.LostItemUiModel

class LostItemAdapter :
    ListAdapter<LostItemUiModel, LostItemViewHolder>(
        object :
            DiffUtil.ItemCallback<LostItemUiModel>() {
            override fun areItemsTheSame(
                oldItem: LostItemUiModel,
                newItem: LostItemUiModel,
            ): Boolean = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: LostItemUiModel,
                newItem: LostItemUiModel,
            ): Boolean = oldItem.imageId == newItem.imageId
        },
    ) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): LostItemViewHolder = LostItemViewHolder.from(parent)

    override fun onBindViewHolder(
        holder: LostItemViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }
}

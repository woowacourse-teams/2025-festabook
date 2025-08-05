package com.daedan.festabook.presentation.news.lost.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.daedan.festabook.presentation.news.lost.model.LostItemUiModel
import com.daedan.festabook.presentation.news.notice.adapter.OnNewsClickListener

class LostItemAdapter(
    private val onNewsClickListener: OnNewsClickListener,
) : ListAdapter<LostItemUiModel, LostItemViewHolder>(lostItemDiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): LostItemViewHolder = LostItemViewHolder.from(parent, onNewsClickListener)

    override fun onBindViewHolder(
        holder: LostItemViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    companion object {
        private val lostItemDiffCallback =
            object :
                DiffUtil.ItemCallback<LostItemUiModel>() {
                override fun areItemsTheSame(
                    oldItem: LostItemUiModel,
                    newItem: LostItemUiModel,
                ): Boolean = oldItem.imageId == newItem.imageId

                override fun areContentsTheSame(
                    oldItem: LostItemUiModel,
                    newItem: LostItemUiModel,
                ): Boolean = oldItem == newItem
            }
    }
}

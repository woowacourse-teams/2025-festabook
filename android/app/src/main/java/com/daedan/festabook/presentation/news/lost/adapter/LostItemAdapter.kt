package com.daedan.festabook.presentation.news.lost.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.daedan.festabook.presentation.news.lost.model.LostGuideItemUiModel
import com.daedan.festabook.presentation.news.lost.model.LostItemUiModel
import com.daedan.festabook.presentation.news.notice.adapter.OnNewsClickListener

class LostItemAdapter(
    private val onNewsClickListener: OnNewsClickListener,
    private val lostGuideItem: LostGuideItemUiModel = LostGuideItemUiModel(),
) : ListAdapter<LostItemUiModel, RecyclerView.ViewHolder>(lostItemDiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder =
        if (viewType == VIEW_TYPE_GUIDE) {
            LostGuideItemViewHolder.from(parent, onNewsClickListener)
        } else {
            LostItemViewHolder.from(parent, onNewsClickListener)
        }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        if (position == 0) {
            (holder as LostGuideItemViewHolder).bind(lostGuideItem)
        } else {
            (holder as LostItemViewHolder).bind(getItem(position))
        }
    }

    override fun getItemViewType(position: Int): Int = if (position == 0) VIEW_TYPE_GUIDE else VIEW_TYPE_ITEM

    companion object {
        private const val VIEW_TYPE_GUIDE = 0
        private const val VIEW_TYPE_ITEM = 1
        private val lostItemDiffCallback =
            object :
                DiffUtil.ItemCallback<LostItemUiModel>() {
                override fun areItemsTheSame(
                    oldItem: LostItemUiModel,
                    newItem: LostItemUiModel,
                ): Boolean = oldItem.lostItemId == newItem.lostItemId

                override fun areContentsTheSame(
                    oldItem: LostItemUiModel,
                    newItem: LostItemUiModel,
                ): Boolean = oldItem == newItem
            }
    }
}

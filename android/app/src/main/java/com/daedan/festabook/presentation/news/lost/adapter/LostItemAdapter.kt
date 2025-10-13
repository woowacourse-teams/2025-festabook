package com.daedan.festabook.presentation.news.lost.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.daedan.festabook.presentation.news.lost.model.LostUiModel
import com.daedan.festabook.presentation.news.notice.adapter.OnNewsClickListener

class LostItemAdapter(
    private val onNewsClickListener: OnNewsClickListener,
) : ListAdapter<LostUiModel, RecyclerView.ViewHolder>(lostItemDiffCallback) {
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
        if (holder.itemViewType == VIEW_TYPE_GUIDE) {
            (holder as LostGuideItemViewHolder).bind(getItem(position) as LostUiModel.Guide)
        } else {
            (holder as LostItemViewHolder).bind(getItem(position) as LostUiModel.Item)
        }
    }

    override fun getItemViewType(position: Int): Int = if (position == 0) VIEW_TYPE_GUIDE else VIEW_TYPE_ITEM

    companion object {
        private const val VIEW_TYPE_GUIDE = 0
        private const val VIEW_TYPE_ITEM = 1
        private val lostItemDiffCallback =
            object :
                DiffUtil.ItemCallback<LostUiModel>() {
                override fun areItemsTheSame(
                    oldItem: LostUiModel,
                    newItem: LostUiModel,
                ): Boolean =
                    when {
                        oldItem is LostUiModel.Item && newItem is LostUiModel.Item ->
                            oldItem.lostItemId == newItem.lostItemId

                        oldItem is LostUiModel.Guide && newItem is LostUiModel.Guide ->
                            oldItem.guide == newItem.guide

                        else -> false
                    }

                override fun areContentsTheSame(
                    oldItem: LostUiModel,
                    newItem: LostUiModel,
                ): Boolean = oldItem == newItem
            }
    }
}

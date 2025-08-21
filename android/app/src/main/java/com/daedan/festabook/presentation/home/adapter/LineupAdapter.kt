package com.daedan.festabook.presentation.home.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.daedan.festabook.presentation.home.LineupItemUiModel

class LineupAdapter : ListAdapter<LineupItemUiModel, LineupItemViewHolder>(lineupItemDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): LineupItemViewHolder = LineupItemViewHolder.from(parent)

    override fun onBindViewHolder(
        holder: LineupItemViewHolder,
        position: Int,
    ) {
        holder.bind(currentList[position])
    }

    companion object {
        private val lineupItemDiffUtil =
            object : DiffUtil.ItemCallback<LineupItemUiModel>() {
                override fun areItemsTheSame(
                    oldItem: LineupItemUiModel,
                    newItem: LineupItemUiModel,
                ): Boolean = oldItem.id == newItem.id

                override fun areContentsTheSame(
                    oldItem: LineupItemUiModel,
                    newItem: LineupItemUiModel,
                ): Boolean = oldItem == newItem
            }
    }
}

package com.daedan.festabook.presentation.home.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.daedan.festabook.presentation.home.LineUpItemGroupUiModel
import com.daedan.festabook.presentation.home.LineUpItemOfDayUiModel
import com.daedan.festabook.presentation.home.LineupItemUiModel

class LineUpItemOfDayAdapter : ListAdapter<LineUpItemOfDayUiModel, LineUpItemOfDayViewHolder>(lineupItemDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): LineUpItemOfDayViewHolder = LineUpItemOfDayViewHolder.from(parent, LineupAdapter())

    override fun onBindViewHolder(
        holder: LineUpItemOfDayViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    companion object {
        private val lineupItemDiffUtil =
            object : DiffUtil.ItemCallback<LineUpItemOfDayUiModel>() {
                override fun areItemsTheSame(
                    oldItem: LineUpItemOfDayUiModel,
                    newItem: LineUpItemOfDayUiModel,
                ): Boolean = oldItem.id == newItem.id

                override fun areContentsTheSame(
                    oldItem: LineUpItemOfDayUiModel,
                    newItem: LineUpItemOfDayUiModel,
                ): Boolean = oldItem == newItem
            }
    }
}

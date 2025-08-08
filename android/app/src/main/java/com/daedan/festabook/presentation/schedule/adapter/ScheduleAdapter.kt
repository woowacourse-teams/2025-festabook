package com.daedan.festabook.presentation.schedule.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.daedan.festabook.presentation.schedule.OnBookmarkCheckedListener
import com.daedan.festabook.presentation.schedule.model.ScheduleEventUiModel

class ScheduleAdapter(
    private val onBookmarkCheckedListener: OnBookmarkCheckedListener,
) : ListAdapter<ScheduleEventUiModel, ScheduleItemViewHolder>(DIFF_UTIL) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ScheduleItemViewHolder = ScheduleItemViewHolder.from(parent, onBookmarkCheckedListener)

    override fun onBindViewHolder(
        holder: ScheduleItemViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position), itemCount)
    }

    companion object {
        private val DIFF_UTIL =
            object :
                DiffUtil.ItemCallback<ScheduleEventUiModel>() {
                override fun areItemsTheSame(
                    oldItem: ScheduleEventUiModel,
                    newItem: ScheduleEventUiModel,
                ): Boolean = oldItem.id == newItem.id

                override fun areContentsTheSame(
                    oldItem: ScheduleEventUiModel,
                    newItem: ScheduleEventUiModel,
                ): Boolean = oldItem == newItem
            }
    }
}

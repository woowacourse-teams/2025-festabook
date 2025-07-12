package com.daedan.festabook.presentation.schedule.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.daedan.festabook.domain.model.ScheduleEvent

class ScheduleAdapter :
    ListAdapter<ScheduleEvent, ScheduleItemViewHolder>(
        object :
            DiffUtil.ItemCallback<ScheduleEvent>() {
            override fun areItemsTheSame(
                oldItem: ScheduleEvent,
                newItem: ScheduleEvent,
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: ScheduleEvent,
                newItem: ScheduleEvent,
            ): Boolean = oldItem == newItem
        },
    ) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ScheduleItemViewHolder = ScheduleItemViewHolder.from(parent)

    override fun onBindViewHolder(
        holder: ScheduleItemViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }
}

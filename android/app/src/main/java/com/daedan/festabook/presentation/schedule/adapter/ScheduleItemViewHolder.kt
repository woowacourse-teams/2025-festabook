package com.daedan.festabook.presentation.schedule.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daedan.festabook.databinding.ItemScheduleTabPageBinding
import com.daedan.festabook.domain.model.ScheduleEvent

class ScheduleItemViewHolder(
    private val binding: ItemScheduleTabPageBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: ScheduleEvent) {
        binding.scheduleEvent = item
    }

    companion object {
        fun from(parent: ViewGroup): ScheduleItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemScheduleTabPageBinding.inflate(inflater, parent, false)
            return ScheduleItemViewHolder(binding)
        }
    }
}

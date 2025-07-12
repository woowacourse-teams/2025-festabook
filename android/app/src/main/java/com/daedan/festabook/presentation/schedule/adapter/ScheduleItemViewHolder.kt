package com.daedan.festabook.presentation.schedule.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daedan.festabook.databinding.ItemScheduleTabPageBinding
import com.daedan.festabook.domain.model.ScheduleEvent
import com.daedan.festabook.presentation.schedule.OnBookmarkCheckedListener

class ScheduleItemViewHolder(
    private val binding: ItemScheduleTabPageBinding,
    private val onBookmarkCheckedListener: OnBookmarkCheckedListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: ScheduleEvent) {
        binding.scheduleEvent = item
        binding.onBookmarkCheckedListener = onBookmarkCheckedListener
    }

    companion object {
        fun from(
            parent: ViewGroup,
            onBookmarkCheckedListener: OnBookmarkCheckedListener,
        ): ScheduleItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemScheduleTabPageBinding.inflate(inflater, parent, false)
            return ScheduleItemViewHolder(binding, onBookmarkCheckedListener)
        }
    }
}

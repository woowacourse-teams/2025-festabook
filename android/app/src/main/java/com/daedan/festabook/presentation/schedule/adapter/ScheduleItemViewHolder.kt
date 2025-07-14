package com.daedan.festabook.presentation.schedule.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.daedan.festabook.R
import com.daedan.festabook.databinding.ItemScheduleTabPageBinding
import com.daedan.festabook.domain.model.ScheduleEvent
import com.daedan.festabook.domain.model.ScheduleEventStatus
import com.daedan.festabook.presentation.schedule.OnBookmarkCheckedListener
import com.daedan.festabook.presentation.schedule.toKoreanString
import com.daedan.festabook.presentation.schedule.toPx

class ScheduleItemViewHolder(
    private val binding: ItemScheduleTabPageBinding,
    private val onBookmarkCheckedListener: OnBookmarkCheckedListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: ScheduleEvent) {
        binding.scheduleEvent = item
        binding.onBookmarkCheckedListener = onBookmarkCheckedListener
        setupEventViewByStatus(item.status)
    }

    private fun setupEventViewByStatus(status: ScheduleEventStatus) {
        val context = binding.root.context
        val gray050 = ContextCompat.getColor(context, R.color.gray050)
        val gray400 = ContextCompat.getColor(context, R.color.gray400)
        val gray500 = ContextCompat.getColor(context, R.color.gray500)
        val gray900 = ContextCompat.getColor(context, R.color.gray900)
        val black400 = ContextCompat.getColor(context, R.color.black400)

        when (status) {
            ScheduleEventStatus.COMPLETED -> {
                val borderColor = R.drawable.bg_stroke_gray400_radius_10dp
                binding.clScheduleEventCard.setBackgroundResource(borderColor)

                binding.tvScheduleEventStatus.text = status.toKoreanString(context)
                binding.tvScheduleEventStatus.setTextColor(gray400)
                binding.tvScheduleEventStatus.gravity = Gravity.END

                binding.ivScheduleEventTimeLineCircle.setImageResource(R.drawable.ic_circle_gray300)

                setupEventContentsColor(
                    titleColor = gray400,
                    timeColor = gray400,
                    locationColor = gray400,
                    bookmarkColor = gray400,
                )
            }

            ScheduleEventStatus.ONGOING -> {
                val borderColor = R.drawable.bg_stroke_blue400_radius_10dp
                binding.clScheduleEventCard.setBackgroundResource(borderColor)

                binding.tvScheduleEventStatus.text = status.toKoreanString(context)
                binding.tvScheduleEventStatus.setTextColor(gray050)
                binding.tvScheduleEventStatus.setBackgroundResource(R.drawable.bg_gray900_radius_6dp)

                binding.ivScheduleEventTimeLineCircle.setImageResource(R.drawable.ic_circle_blue400)

                setupEventContentsColor(
                    titleColor = gray900,
                    timeColor = gray500,
                    locationColor = gray500,
                    bookmarkColor = black400,
                )
            }

            ScheduleEventStatus.UPCOMING -> {
                val borderColor = R.drawable.bg_stroke_1dp_radius_10dp
                binding.clScheduleEventCard.setBackgroundResource(borderColor)

                binding.tvScheduleEventStatus.text = status.toKoreanString(context)
                binding.tvScheduleEventStatus.setTextColor(gray900)
                binding.tvScheduleEventStatus.setBackgroundResource(R.drawable.bg_stroke_gray900_radius_6dp)
                binding.tvScheduleEventStatus.layoutParams =
                    binding.tvScheduleEventStatus.layoutParams.apply {
                        width = 36.toPx(context)
                        height = 24.toPx(context)
                    }

                binding.ivScheduleEventTimeLineCircle.setImageResource(R.drawable.ic_circle_green400)

                setupEventContentsColor(
                    titleColor = gray900,
                    timeColor = gray500,
                    locationColor = gray500,
                    bookmarkColor = black400,
                )
            }
        }
    }

    private fun setupEventContentsColor(
        titleColor: Int,
        timeColor: Int,
        locationColor: Int,
        bookmarkColor: Int,
    ) {
        binding.tvScheduleEventTitle.setTextColor(titleColor)
        binding.ivScheduleEventLocation.setColorFilter(locationColor)
        binding.tvScheduleEventLocation.setTextColor(locationColor)
        binding.ivScheduleEventClock.setColorFilter(timeColor)
        binding.tvScheduleEventTime.setTextColor(timeColor)
        binding.ivScheduleEventBookMark.setColorFilter(bookmarkColor)
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

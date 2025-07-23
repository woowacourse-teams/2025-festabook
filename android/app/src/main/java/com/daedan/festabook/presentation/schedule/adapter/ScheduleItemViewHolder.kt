package com.daedan.festabook.presentation.schedule.adapter

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.daedan.festabook.R
import com.daedan.festabook.databinding.ItemScheduleTabPageBinding
import com.daedan.festabook.presentation.schedule.OnBookmarkCheckedListener
import com.daedan.festabook.presentation.schedule.model.ScheduleEventUiModel
import com.daedan.festabook.presentation.schedule.model.ScheduleEventUiStatus
import com.daedan.festabook.presentation.schedule.model.toKoreanString

class ScheduleItemViewHolder(
    private val binding: ItemScheduleTabPageBinding,
    private val onBookmarkCheckedListener: OnBookmarkCheckedListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: ScheduleEventUiModel) {
        binding.scheduleEvent = item
        binding.onBookmarkCheckedListener = onBookmarkCheckedListener
        setupEventViewByStatus(item.status)
    }

    private fun setupEventViewByStatus(status: ScheduleEventUiStatus) {
        val context = binding.root.context
        val gray050 = ContextCompat.getColor(context, R.color.gray050)
        val gray400 = ContextCompat.getColor(context, R.color.gray400)
        val gray500 = ContextCompat.getColor(context, R.color.gray500)
        val gray900 = ContextCompat.getColor(context, R.color.gray900)
        val black400 = ContextCompat.getColor(context, R.color.black400)

        when (status) {
            ScheduleEventUiStatus.COMPLETED -> {
                val borderColor = R.drawable.bg_stroke_gray400_radius_10dp
                binding.clScheduleEventCard.setBackgroundResource(borderColor)
                setupScheduleEventStatusText(
                    context = context,
                    status = status,
                    textColor = gray400,
                    backgroundResId = null,
                )
                setupScheduleEventTimeLineCircleIcon(R.drawable.ic_circle_gray300)
                setupScheduleEventContentsColor(
                    titleColor = gray400,
                    timeColor = gray400,
                    locationColor = gray400,
                    bookmarkColor = gray400,
                )
            }

            ScheduleEventUiStatus.ONGOING -> {
                val borderColor = R.drawable.bg_stroke_blue400_radius_10dp
                binding.clScheduleEventCard.setBackgroundResource(borderColor)
                setupScheduleEventStatusText(
                    context = context,
                    status = status,
                    textColor = gray050,
                    backgroundResId = R.drawable.bg_gray900_radius_6dp,
                )
                setupScheduleEventTimeLineCircleIcon(R.drawable.ic_circle_blue400)
                setupScheduleEventContentsColor(
                    titleColor = gray900,
                    timeColor = gray500,
                    locationColor = gray500,
                    bookmarkColor = black400,
                )
            }

            ScheduleEventUiStatus.UPCOMING -> {
                val borderColor = R.drawable.bg_stroke_green400_radius_10dp
                binding.clScheduleEventCard.setBackgroundResource(borderColor)
                setupScheduleEventStatusText(
                    context = context,
                    status = status,
                    textColor = gray900,
                    backgroundResId = R.drawable.bg_stroke_gray900_radius_6dp,
                )
                setupScheduleEventTimeLineCircleIcon(R.drawable.ic_circle_green400)
                setupScheduleEventContentsColor(
                    titleColor = gray900,
                    timeColor = gray500,
                    locationColor = gray500,
                    bookmarkColor = black400,
                )
                binding.tvScheduleEventStatus.layoutParams =
                    binding.tvScheduleEventStatus.layoutParams.apply {
                        width = UPCOMING_TEXT_WIDTH.toPx(context)
                        height = UPCOMING_TEXT_HEIGHT.toPx(context)
                    }
            }
        }
    }

    private fun setupScheduleEventContentsColor(
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

    private fun setupScheduleEventStatusText(
        context: Context,
        status: ScheduleEventUiStatus,
        textColor: Int,
        backgroundResId: Int?,
    ) = with(binding.tvScheduleEventStatus) {
        val gray050 = ContextCompat.getColor(context, R.color.gray050)
        text = status.toKoreanString(context)
        setTextColor(textColor)
        gravity = if (status == ScheduleEventUiStatus.COMPLETED) Gravity.END else Gravity.CENTER
        backgroundResId?.let { setBackgroundResource(it) } ?: setBackgroundColor(gray050)
    }

    private fun setupScheduleEventTimeLineCircleIcon(iconRes: Int) {
        binding.ivScheduleEventTimeLineCircle.setImageResource(iconRes)
    }

    companion object {
        private const val UPCOMING_TEXT_WIDTH = 36
        private const val UPCOMING_TEXT_HEIGHT = 24

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

fun Int.toPx(context: Context): Int = (this * context.resources.displayMetrics.density).toInt()

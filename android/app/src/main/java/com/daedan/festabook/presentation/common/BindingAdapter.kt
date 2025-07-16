package com.daedan.festabook.presentation.common

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.daedan.festabook.R
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@BindingAdapter("formattedNoticeDateTime")
fun TextView.setFormattedNoticeDateTime(isoDateTime: String) {
    val formattedDateTime =
        runCatching {
            val instant = Instant.parse(isoDateTime)
            val localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime()
            val outputFormatter = DateTimeFormatter.ofPattern("MM/dd HH:mm")
            localDateTime.format(outputFormatter)
        }.getOrDefault("")

    text = formattedDateTime
}

@BindingAdapter("startTime", "endTime", requireAll = true)
fun setFormatDate(
    textView: TextView,
    startTime: String?,
    endTime: String?,
) {
    val text = listOf(startTime, endTime).joinToString(" ~ ")
    textView.text = text
}

@BindingAdapter("isBookmarked")
fun setBookmarkColor(
    imageView: ImageView,
    isBookmarked: Boolean,
) {
    val iconRes =
        if (isBookmarked) R.drawable.ic_bookmark_selected else R.drawable.ic_bookmark_normal
    imageView.setImageResource(iconRes)
}

package com.daedan.festabook.presentation.common

import android.widget.TextView
import androidx.databinding.BindingAdapter
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

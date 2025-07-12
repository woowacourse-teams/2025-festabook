package com.daedan.festabook.presentation

import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("startTime", "endTime")
fun setFormatDate(
    view: TextView,
    startTime: String?,
    endTime: String?,
) {
    val text = listOf(startTime, endTime).joinToString(" ~ ")
    view.text = text
}

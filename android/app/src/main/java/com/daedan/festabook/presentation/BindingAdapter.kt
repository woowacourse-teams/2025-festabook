package com.daedan.festabook.presentation

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.daedan.festabook.R

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
    val iconRes = if (isBookmarked) R.drawable.ic_bookmark_filled else R.drawable.ic_bookmark
    imageView.setImageResource(iconRes)
}

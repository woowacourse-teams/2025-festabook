package com.daedan.festabook.presentation.common

import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import coil3.load
import coil3.request.crossfade
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.model.KeyPath
import com.daedan.festabook.R
import com.daedan.festabook.presentation.schedule.model.ScheduleEventUiStatus

@BindingAdapter("startTime", "endTime", requireAll = true)
fun setFormatDate(
    textView: TextView,
    startTime: String?,
    endTime: String?,
) {
    if (startTime == null && endTime == null) {
        textView.text = textView.context.getString(R.string.place_detail_default_time)
        return
    }
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

@BindingAdapter("imageUrl")
fun setImage(
    view: ImageView,
    imageUrl: String?,
) {
    view.load(imageUrl) {
        crossfade(true)
    }
}

@BindingAdapter("timeLineCircleStatus")
fun setTimeLineCircle(
    view: LottieAnimationView,
    status: ScheduleEventUiStatus?,
) {
    status ?: return
    when (status) {
        ScheduleEventUiStatus.UPCOMING -> {
            val color = ContextCompat.getColor(view.context, R.color.green400)
            view.addValueCallback(
                KeyPath("centerCircle", "**", "Fill 1"),
                LottieProperty.COLOR,
            ) { color }
            view.addValueCallback(
                KeyPath("outerWave", "**", "Fill 1"),
                LottieProperty.OPACITY,
            ) { 0 }
            view.addValueCallback(
                KeyPath("innerWave", "**", "Fill 1"),
                LottieProperty.OPACITY,
            ) { 100 }
        }

        ScheduleEventUiStatus.ONGOING -> {
            val color = ContextCompat.getColor(view.context, R.color.blue400)
            view.addValueCallback(
                KeyPath("centerCircle", "**", "Fill 1"),
                LottieProperty.COLOR,
            ) { color }
            view.addValueCallback(
                KeyPath("outerWave", "**", "Fill 1"),
                LottieProperty.OPACITY,
            ) { 100 }
            view.addValueCallback(
                KeyPath("innerWave", "**", "Fill 1"),
                LottieProperty.OPACITY,
            ) { 100 }
            view.addValueCallback(
                KeyPath("outerWave", "**", "Fill 1"),
                LottieProperty.COLOR,
            ) { color }
            view.addValueCallback(
                KeyPath("innerWave", "**", "Fill 1"),
                LottieProperty.COLOR,
            ) { color }
        }

        ScheduleEventUiStatus.COMPLETED -> {
            val color = ContextCompat.getColor(view.context, R.color.gray300)
            view.addValueCallback(
                KeyPath("centerCircle", "**", "Fill 1"),
                LottieProperty.COLOR,
            ) { color }
            view.addValueCallback(
                KeyPath("outerWave", "**", "Fill 1"),
                LottieProperty.OPACITY,
            ) { 0 }
            view.addValueCallback(
                KeyPath("innerWave", "**", "Fill 1"),
                LottieProperty.OPACITY,
            ) { 0 }
        }
    }
}

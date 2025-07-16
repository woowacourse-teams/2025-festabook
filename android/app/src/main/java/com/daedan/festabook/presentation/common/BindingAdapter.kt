package com.daedan.festabook.presentation.common

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import coil3.load
import coil3.request.crossfade
import com.daedan.festabook.R
import com.daedan.festabook.presentation.placeList.model.PlaceCategory
import com.google.android.material.card.MaterialCardView
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

@BindingAdapter("category")
fun setCategory(
    view: MaterialCardView,
    category: PlaceCategory,
) {
    val density = view.context.resources.displayMetrics.density
    val layoutParams = view.layoutParams
    when (category) {
        PlaceCategory.FOOD_TRUCK -> layoutParams.width = (50 * density).toInt()
        PlaceCategory.BAR -> layoutParams.width = (34 * density).toInt()
        PlaceCategory.BOOTH -> layoutParams.width = (34 * density).toInt()
    }
    view.layoutParams = layoutParams
}

@BindingAdapter("category")
fun setCategory(
    view: TextView,
    category: PlaceCategory,
) {
    when (category) {
        PlaceCategory.FOOD_TRUCK ->
            view.text =
                view.context.getString(R.string.place_list_title_food_truck)

        PlaceCategory.BAR -> view.text = view.context.getString(R.string.place_list_title_bar)
        PlaceCategory.BOOTH -> view.text = view.context.getString(R.string.place_list_title_booth)
    }
}

@BindingAdapter("imageUrl")
fun setImage(
    view: ImageView,
    imageUrl: String,
) {
    view.load(imageUrl) {
        crossfade(true)
    }
}

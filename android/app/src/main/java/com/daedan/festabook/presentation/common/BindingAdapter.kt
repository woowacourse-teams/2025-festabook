package com.daedan.festabook.presentation.common

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import coil3.load
import coil3.request.crossfade
import com.daedan.festabook.R
import com.daedan.festabook.presentation.placeList.model.PlaceCategoryUiModel
import com.google.android.material.card.MaterialCardView

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

@BindingAdapter("category")
fun setCategory(
    view: MaterialCardView,
    category: PlaceCategoryUiModel?,
) {
    category ?: return
    val density = view.context.resources.displayMetrics.density
    val layoutParams = view.layoutParams
    when (category) {
        PlaceCategoryUiModel.FOOD_TRUCK -> layoutParams.width = (50 * density).toInt()
        PlaceCategoryUiModel.BAR -> layoutParams.width = (34 * density).toInt()
        PlaceCategoryUiModel.BOOTH -> layoutParams.width = (34 * density).toInt()
        PlaceCategoryUiModel.SMOKING_AREA, PlaceCategoryUiModel.TOILET, PlaceCategoryUiModel.TRASH_CAN -> Unit
    }
    view.layoutParams = layoutParams
}

@BindingAdapter("category")
fun setCategory(
    view: TextView,
    category: PlaceCategoryUiModel?,
) {
    category ?: return
    when (category) {
        PlaceCategoryUiModel.FOOD_TRUCK ->
            view.text =
                view.context.getString(R.string.place_list_title_food_truck)

        PlaceCategoryUiModel.BAR -> view.text = view.context.getString(R.string.place_list_title_bar)
        PlaceCategoryUiModel.BOOTH -> view.text = view.context.getString(R.string.place_list_title_booth)
        PlaceCategoryUiModel.SMOKING_AREA, PlaceCategoryUiModel.TOILET, PlaceCategoryUiModel.TRASH_CAN -> Unit
    }
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

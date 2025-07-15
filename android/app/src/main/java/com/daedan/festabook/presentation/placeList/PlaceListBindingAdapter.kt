package com.daedan.festabook.presentation.placeList

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import coil3.load
import coil3.request.crossfade
import com.daedan.festabook.R
import com.daedan.festabook.presentation.placeList.uimodel.PlaceCategory
import com.google.android.material.card.MaterialCardView

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

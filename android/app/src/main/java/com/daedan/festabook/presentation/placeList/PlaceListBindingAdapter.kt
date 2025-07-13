package com.daedan.festabook.presentation.placeList

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil3.load
import coil3.request.crossfade
import com.google.android.material.card.MaterialCardView

@BindingAdapter("category")
fun setCategory(view: MaterialCardView, category: String) {
    val density = view.context.resources.displayMetrics.density
    val layoutParams = view.layoutParams
    when (category) {
        "푸드트럭" -> layoutParams.width = (50 * density).toInt()
        "주점" -> layoutParams.width = (34 * density).toInt()
        "부스" -> layoutParams.width = (34 * density).toInt()
    }
    view.layoutParams = layoutParams
}

@BindingAdapter("imageUrl")
fun setImage(view: ImageView, imageUrl: String) {
    view.load(imageUrl) {
        crossfade(true)
    }
}
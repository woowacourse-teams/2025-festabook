package com.daedan.festabook.presentation.common

import android.graphics.Color
import android.widget.ImageView
import androidx.core.graphics.drawable.toDrawable
import coil3.load
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.fallback
import coil3.request.placeholder
import com.daedan.festabook.BuildConfig
import com.daedan.festabook.R

fun ImageView.loadImage(
    url: String?,
    block: ImageRequest.Builder.() -> Unit = {},
) {
    val finalUrl =
        if (url != null && url.startsWith("/images/")) {
            BuildConfig.FESTABOOK_URL.removeSuffix("/api/") + url
        } else {
            url
        }

    this.load(finalUrl) {
        crossfade(true)
        placeholder(Color.LTGRAY.toDrawable())
        fallback(R.drawable.img_fallback)

        block()
    }
}

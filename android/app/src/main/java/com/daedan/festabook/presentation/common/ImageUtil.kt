package com.daedan.festabook.presentation.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toDrawable
import coil3.load
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
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
        block()
        crossfade(true)
        placeholder(Color.LTGRAY.toDrawable())
        fallback(R.drawable.img_fallback)
        error(R.drawable.img_fallback)
    }
}

fun vectorToBitmap(
    context: Context,
    vectorResId: Int,
): Bitmap {
    val drawable = AppCompatResources.getDrawable(context, vectorResId)!!
    val bitmap =
        createBitmap(
            drawable.intrinsicWidth.coerceAtLeast(1),
            drawable.intrinsicHeight.coerceAtLeast(1),
        )
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}

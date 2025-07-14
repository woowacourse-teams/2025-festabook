package com.daedan.festabook.presentation.common

import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator

fun ViewGroup.scrollAnimation(limitedTranslationY: Float) {
    animate()
        .translationY(limitedTranslationY)
        .setDuration(100)
        .setInterpolator(AccelerateInterpolator(0.4f))
        .start()
}


package com.daedan.festabook.presentation.common

import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

fun ViewGroup.scrollAnimation(limitedTranslationY: Float) {
    animate()
        .translationY(limitedTranslationY)
        .setDuration(100)
        .setInterpolator(AccelerateInterpolator(0.4f))
        .start()
}

fun RecyclerView.canScrollUp(): Boolean = this.canScrollVertically(-1)

fun BottomNavigationView.animateHideBottomNavigationView() {
    clearAnimation()
    animate()
        .translationY(height.toFloat())
        .setDuration(200)
        .withEndAction {
            visibility = View.GONE
        }.start()
}

fun BottomNavigationView.animateShowBottomNavigationView() {
    clearAnimation()
    visibility = View.VISIBLE
    animate()
        .translationY(0f)
        .setDuration(100)
        .start()
}

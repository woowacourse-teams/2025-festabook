package com.daedan.festabook.presentation.common

import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.recyclerview.widget.RecyclerView

fun ViewGroup.scrollAnimation(limitedTranslationY: Float) {
    animate()
        .translationY(limitedTranslationY)
        .setDuration(100)
        .setInterpolator(AccelerateInterpolator(0.4f))
        .start()
}

fun RecyclerView.canScrollUp(): Boolean = this.canScrollVertically(-1)

fun ViewGroup.showBottomAnimation() {
    alpha = 0.3f // 시작 시 투명하게 설정
    translationY = 120f // 시작 시 아래로 이동
    visibility = View.VISIBLE // 애니메이션 전에 뷰를 보이게 함'
    animate()
        .alpha(1f) // 투명도를 1로
        .translationY(0f) // 원래 위치로 이동
        .setDuration(300) // 0.5초 동안
        .setInterpolator(DecelerateInterpolator()) // 점점 느려지게
        .start()
}
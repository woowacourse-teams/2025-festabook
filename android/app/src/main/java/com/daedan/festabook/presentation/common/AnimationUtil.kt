package com.daedan.festabook.presentation.common

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.daedan.festabook.R
import com.google.android.material.card.MaterialCardView

fun ViewGroup.scrollAnimation(limitedTranslationY: Float) {
    animate()
        .translationY(limitedTranslationY)
        .setDuration(100)
        .setInterpolator(AccelerateInterpolator(0.4f))
        .start()
}

fun RecyclerView.canScrollUp(): Boolean = this.canScrollVertically(-1)

fun MaterialCardView.animateHideBottomNavigationView() {
    clearAnimation()
    animate()
        .translationY(height.toFloat())
        .setDuration(200)
        .withEndAction {
            visibility = View.GONE
        }.start()
}

fun MaterialCardView.animateShowBottomNavigationView() {
    clearAnimation()
    visibility = View.VISIBLE
    animate()
        .translationY(0f)
        .setDuration(100)
        .start()
}

fun Fragment.getBottomNavigationViewAnimationCallback() =
    object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentStopped(
            fm: FragmentManager,
            f: Fragment,
        ) {
            requireActivity()
                .findViewById<MaterialCardView>(R.id.cv_bnv_wrapper)
                ?.animateShowBottomNavigationView()
            super.onFragmentStopped(fm, f)
        }

        override fun onFragmentAttached(
            fm: FragmentManager,
            f: Fragment,
            context: Context,
        ) {
            requireActivity()
                .findViewById<MaterialCardView>(R.id.cv_bnv_wrapper)
                ?.animateHideBottomNavigationView()
            super.onFragmentAttached(fm, f, context)
        }
    }

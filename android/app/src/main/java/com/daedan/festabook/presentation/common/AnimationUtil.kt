package com.daedan.festabook.presentation.common

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.daedan.festabook.R
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton

fun ViewGroup.scrollAnimation(limitedTranslationY: Float) {
    animate()
        .translationY(limitedTranslationY)
        .setDuration(100)
        .setInterpolator(AccelerateInterpolator(0.4f))
        .start()
}

fun RecyclerView.canScrollUp(): Boolean = this.canScrollVertically(-1)

fun View.animateHideBottomNavigationView() {
    clearAnimation()
    animate()
        .translationY(height.toFloat())
        .setDuration(200)
        .withEndAction {
            visibility = View.GONE
        }.start()
}

fun View.animateShowBottomNavigationView() {
    clearAnimation()
    animate()
        .withStartAction {
            visibility = View.VISIBLE
        }.translationY(0f)
        .setDuration(200)
        .start()
}

val bottomNavigationViewAnimationCallback =
    object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentStopped(
            fm: FragmentManager,
            f: Fragment,
        ) {
            f
                .requireActivity()
                .findViewById<FloatingActionButton>(R.id.fab_map)
                ?.animateShowBottomNavigationView()

            f
                .requireActivity()
                .findViewById<BottomAppBar>(R.id.bab_menu)
                ?.animateShowBottomNavigationView()

            super.onFragmentStopped(fm, f)
        }

        override fun onFragmentAttached(
            fm: FragmentManager,
            f: Fragment,
            context: Context,
        ) {
            f
                .requireActivity()
                .findViewById<BottomAppBar>(R.id.bab_menu)
                ?.animateHideBottomNavigationView()

            f
                .requireActivity()
                .findViewById<FloatingActionButton>(R.id.fab_map)
                ?.animateHideBottomNavigationView()
            super.onFragmentAttached(fm, f, context)
        }
    }

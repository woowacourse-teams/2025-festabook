package com.daedan.festabook.presentation.placeList.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior

class PlaceListBottomSheetFollowBehavior(
    context: Context,
    attrs: AttributeSet,
) : CoordinatorLayout.Behavior<View>(
        context,
        attrs,
    ) {
    private var currentBehavior: BottomSheetBehavior<*>? = null
    private lateinit var callback: BottomSheetFollowCallback

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View,
    ): Boolean {
        val behavior = (dependency.layoutParams as? CoordinatorLayout.LayoutParams)?.behavior
        return behavior is BottomSheetBehavior<*>
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View,
    ): Boolean {
        val bottomSheetTopY = dependency.y - dependency.height
        child.translationY = bottomSheetTopY

        val behavior = (dependency.layoutParams as? CoordinatorLayout.LayoutParams)?.behavior as? BottomSheetBehavior<*>
        if (behavior != null && behavior !== currentBehavior) {
            currentBehavior?.removeBottomSheetCallback(callback)
            if (!::callback.isInitialized) {
                callback = BottomSheetFollowCallback(child)
            }
            behavior.addBottomSheetCallback(callback)
            currentBehavior = behavior
        }

        return true
    }

    override fun onDetachedFromLayoutParams() {
        super.onDetachedFromLayoutParams()
        currentBehavior?.removeBottomSheetCallback(callback)
        currentBehavior = null
    }

    private class BottomSheetFollowCallback(
        private val child: View,
    ) : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(
            bottomSheet: View,
            newState: Int,
        ) {
            if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                child.visibility = View.GONE
            } else {
                child.visibility = View.VISIBLE
            }
        }

        override fun onSlide(
            bottomSheet: View,
            slideOffset: Float,
        ) = Unit
    }
}

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
    private lateinit var callback: BottomSheetBehavior.BottomSheetCallback

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View,
    ): Boolean {
        val behavior = (dependency.layoutParams as? CoordinatorLayout.LayoutParams)?.behavior
        if (behavior is BottomSheetBehavior<*>) {
            currentBehavior = behavior
        }
        return behavior is BottomSheetBehavior<*>
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View,
    ): Boolean {
        val bottomSheetTopY = dependency.y - dependency.height
        child.translationY = bottomSheetTopY
        return true
    }

    override fun onDetachedFromLayoutParams() {
        super.onDetachedFromLayoutParams()
        currentBehavior?.removeBottomSheetCallback(callback)
        currentBehavior = null
    }

    fun setCallback(callback: BottomSheetBehavior.BottomSheetCallback) {
        if (::callback.isInitialized) {
            currentBehavior?.removeBottomSheetCallback(this.callback)
        }
        this.callback = callback
        currentBehavior?.addBottomSheetCallback(callback)
    }
}

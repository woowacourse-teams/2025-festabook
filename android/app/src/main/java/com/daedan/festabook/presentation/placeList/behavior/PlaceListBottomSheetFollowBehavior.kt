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
    private var callback: BottomSheetBehavior.BottomSheetCallback? = null

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
        callback?.let {
            currentBehavior?.removeBottomSheetCallback(it)
        }
        currentBehavior = null
    }

    fun setCallback(callback: BottomSheetBehavior.BottomSheetCallback) {
        this.callback?.let {
            currentBehavior?.removeBottomSheetCallback(it)
        }
        this.callback = callback
        currentBehavior?.addBottomSheetCallback(callback)
    }
}

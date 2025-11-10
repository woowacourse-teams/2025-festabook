package com.daedan.festabook.presentation.placeMap.placeList.behavior

import android.view.View
import androidx.annotation.IdRes
import com.google.android.material.bottomsheet.BottomSheetBehavior

class MoveToInitialPositionCallback(
    @IdRes private val viewId: Int,
) : BottomSheetBehavior.BottomSheetCallback() {
    private lateinit var child: View
    private var isExceededMaxLength = true

    override fun onStateChanged(
        bottomSheet: View,
        newState: Int,
    ) {
        if (!::child.isInitialized) {
            child = bottomSheet.rootView.findViewById(viewId) ?: return
        }
        if (newState == BottomSheetBehavior.STATE_EXPANDED || !isExceededMaxLength) {
            child.visibility = View.GONE
        } else {
            child.visibility = View.VISIBLE
        }
    }

    override fun onSlide(
        bottomSheet: View,
        slideOffset: Float,
    ) = Unit

    fun setIsExceededMaxLength(isExceededMaxLength: Boolean) {
        this.isExceededMaxLength = isExceededMaxLength
    }
}

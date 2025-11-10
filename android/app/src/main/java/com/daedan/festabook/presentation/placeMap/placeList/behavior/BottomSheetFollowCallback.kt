package com.daedan.festabook.presentation.placeMap.placeList.behavior

import android.view.View
import androidx.annotation.IdRes
import com.google.android.material.bottomsheet.BottomSheetBehavior

open class BottomSheetFollowCallback(
    @IdRes private val viewId: Int,
) : BottomSheetBehavior.BottomSheetCallback() {
    private lateinit var child: View

    override fun onStateChanged(
        bottomSheet: View,
        newState: Int,
    ) {
        if (!::child.isInitialized) {
            child = bottomSheet.rootView.findViewById(viewId) ?: return
        }

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

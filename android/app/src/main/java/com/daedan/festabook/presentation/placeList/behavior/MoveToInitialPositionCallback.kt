package com.daedan.festabook.presentation.placeList.behavior

import android.view.View
import androidx.annotation.IdRes
import com.daedan.festabook.presentation.placeList.placeMap.MapManager
import com.google.android.material.bottomsheet.BottomSheetBehavior

class MoveToInitialPositionCallback(
    @IdRes val viewId: Int,
    private val mapManager: MapManager,
) : BottomSheetFollowCallback(viewId) {
    private lateinit var child: View

    override fun onStateChanged(
        bottomSheet: View,
        newState: Int,
    ) {
        if (!::child.isInitialized) {
            child = bottomSheet.rootView.findViewById(viewId)
        }
        if (newState == BottomSheetBehavior.STATE_EXPANDED || !mapManager.isExceededMaxLength()) {
            child.visibility = View.GONE
        } else {
            child.visibility = View.VISIBLE
        }
    }
}

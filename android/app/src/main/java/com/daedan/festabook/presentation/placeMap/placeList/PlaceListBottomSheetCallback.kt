package com.daedan.festabook.presentation.placeMap.placeList

import android.view.View
import com.daedan.festabook.presentation.placeMap.PlaceMapViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import timber.log.Timber

class PlaceListBottomSheetCallback(
    private val viewModel: PlaceMapViewModel,
) : BottomSheetBehavior.BottomSheetCallback() {
    override fun onStateChanged(
        bottomSheet: View,
        newState: Int,
    ) {
        when (newState) {
            BottomSheetBehavior.STATE_DRAGGING -> {
                Timber.d("STATE_DRAGGING")
                viewModel.onExpandedStateReached()
            }
        }
    }

    override fun onSlide(
        bottomSheet: View,
        slideOffset: Float,
    ) {
    }
}

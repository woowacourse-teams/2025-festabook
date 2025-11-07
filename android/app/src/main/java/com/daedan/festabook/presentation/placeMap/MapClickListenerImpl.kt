package com.daedan.festabook.presentation.placeMap

import com.daedan.festabook.presentation.placeMap.model.PlaceCategoryUiModel
import timber.log.Timber

class MapClickListenerImpl(
    private val viewModel: PlaceMapViewModel,
) : MapClickListener {
    override fun onMarkerListener(
        placeId: Long,
        category: PlaceCategoryUiModel,
    ): Boolean {
        Timber.d("Marker CLick : placeID: $placeId categoty: $category")
        viewModel.selectPlace(placeId)
        return true
    }

    override fun onMapClickListener() {
        Timber.d("Map CLick")
        viewModel.unselectPlace()
    }
}

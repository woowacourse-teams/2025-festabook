package com.daedan.festabook.presentation.placeList.placeMap

import com.daedan.festabook.presentation.placeList.PlaceListViewModel
import com.daedan.festabook.presentation.placeList.model.PlaceCategoryUiModel
import timber.log.Timber

class MapClickListenerImpl(
    private val viewModel: PlaceListViewModel,
) : MapClickListener {
    override fun onMarkerListener(
        placeId: Long,
        category: PlaceCategoryUiModel,
    ): Boolean {
        Timber.d("Marker CLick : placeID: $placeId categoty: $category")
        viewModel.selectPlace(placeId, category)
        return true
    }

    override fun onMapClickListener() {
        Timber.d("Map CLick")
        viewModel.unselectPlace()
    }
}

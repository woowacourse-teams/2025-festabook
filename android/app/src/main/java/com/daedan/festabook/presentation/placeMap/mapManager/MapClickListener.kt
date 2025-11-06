package com.daedan.festabook.presentation.placeMap.mapManager

import com.daedan.festabook.presentation.placeMap.model.PlaceCategoryUiModel

interface MapClickListener {
    fun onMarkerListener(
        placeId: Long,
        category: PlaceCategoryUiModel,
    ): Boolean

    fun onMapClickListener()
}

package com.daedan.festabook.presentation.placeList.placeMap

import com.daedan.festabook.presentation.placeList.model.PlaceCategoryUiModel

interface MapClickListener {
    fun onMarkerListener(
        placeId: Long,
        category: PlaceCategoryUiModel,
    ): Boolean

    fun onMapClickListener()
}

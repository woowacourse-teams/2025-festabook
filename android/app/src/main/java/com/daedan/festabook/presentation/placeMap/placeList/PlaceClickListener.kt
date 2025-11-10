package com.daedan.festabook.presentation.placeMap.placeList

import com.daedan.festabook.presentation.placeMap.model.PlaceUiModel

interface PlaceClickListener {
    fun onPlaceClicked(place: PlaceUiModel)
}

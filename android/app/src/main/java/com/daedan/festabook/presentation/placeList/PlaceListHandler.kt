package com.daedan.festabook.presentation.placeList

import com.daedan.festabook.presentation.placeList.model.PlaceUiModel

fun interface PlaceListHandler {
    fun onPlaceClicked(place: PlaceUiModel)
}

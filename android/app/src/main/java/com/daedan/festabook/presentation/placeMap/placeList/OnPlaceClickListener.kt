package com.daedan.festabook.presentation.placeMap.placeList

import com.daedan.festabook.presentation.placeMap.model.PlaceUiModel

fun interface OnPlaceClickListener {
    fun onPlaceClicked(place: PlaceUiModel)
}

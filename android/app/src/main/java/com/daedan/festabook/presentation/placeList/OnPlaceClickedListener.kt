package com.daedan.festabook.presentation.placeList

import com.daedan.festabook.presentation.placeList.model.PlaceUiModel

fun interface OnPlaceClickedListener {
    fun onPlaceClicked(place: PlaceUiModel)
}

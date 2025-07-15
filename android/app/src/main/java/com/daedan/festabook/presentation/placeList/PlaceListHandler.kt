package com.daedan.festabook.presentation.placeList

import com.daedan.festabook.presentation.placeList.uimodel.Place

fun interface PlaceListHandler {
    fun onPlaceClicked(place: Place)
}

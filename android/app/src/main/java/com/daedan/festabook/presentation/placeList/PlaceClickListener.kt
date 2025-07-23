package com.daedan.festabook.presentation.placeList

import com.daedan.festabook.presentation.placeList.model.PlaceUiModel

interface PlaceClickListener {
    fun onPlaceClicked(place: PlaceUiModel)

    fun onBookmarkClicked(place: PlaceUiModel)
}

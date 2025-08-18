package com.daedan.festabook.presentation.placeList.placeMap

import com.daedan.festabook.presentation.placeList.model.CoordinateUiModel
import com.daedan.festabook.presentation.placeList.model.PlaceCategoryUiModel
import com.daedan.festabook.presentation.placeList.model.PlaceCoordinateUiModel

object DummyPlaceGeography {
    val VALUE =
        listOf(
            PlaceCoordinateUiModel(
                1234,
                CoordinateUiModel(
                    37.5843607642893,
                    127.05848795945516,
                ),
                PlaceCategoryUiModel.STAGE,
            ),
            PlaceCoordinateUiModel(
                1235,
                CoordinateUiModel(
                    37.584639001106105,
                    127.06061100566939,
                ),
                PlaceCategoryUiModel.PRIMARY,
            ),
            PlaceCoordinateUiModel(
                1236,
                CoordinateUiModel(
                    37.58476022551359,
                    127.06140362788611,
                ),
                PlaceCategoryUiModel.PARKING,
            ),
            PlaceCoordinateUiModel(
                1237,
                CoordinateUiModel(
                    37.58434624816219,
                    127.06047490781971,
                ),
                PlaceCategoryUiModel.TOILET,
            ),
        )
}

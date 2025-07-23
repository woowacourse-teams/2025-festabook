package com.daedan.festabook.presentation.placeList.dummy

import com.daedan.festabook.presentation.placeList.model.CoordinateUiModel
import com.daedan.festabook.presentation.placeList.model.InitialMapSettingUiModel
import com.daedan.festabook.presentation.placeList.model.PlaceCategoryUiModel
import com.daedan.festabook.presentation.placeList.model.PlaceCoordinateUiModel

object DummyMapData {
    val initialMapSettingUiModel =
        InitialMapSettingUiModel(
            zoom = 15,
            initialCenter =
                CoordinateUiModel(
                    latitude = 37.583585,
                    longitude = 127.0588862,
                ),
            border =
                listOf(
                    CoordinateUiModel(latitude = 37.5850814, longitude = 127.0593583),
                    CoordinateUiModel(latitude = 37.5844352, longitude = 127.0555388),
                    CoordinateUiModel(latitude = 37.5805582, longitude = 127.0573842),
                    CoordinateUiModel(latitude = 37.5811704, longitude = 127.0600879),
                    CoordinateUiModel(latitude = 37.5826668, longitude = 127.0630061),
                    CoordinateUiModel(latitude = 37.5846053, longitude = 127.0639073),
                    CoordinateUiModel(latitude = 37.5853875, longitude = 127.0619761),
                ),
        )

    val placeCoordinates =
        listOf(
            PlaceCoordinateUiModel(
                coordinate =
                    CoordinateUiModel(
                        37.58363767265077,
                        127.0585779665425,
                    ),
                category = PlaceCategoryUiModel.SMOKING_AREA,
            ),
            PlaceCoordinateUiModel(
                coordinate =
                    CoordinateUiModel(
                        37.583651080599815,
                        127.05879308749738,
                    ),
                category = PlaceCategoryUiModel.TOILET,
            ),
            PlaceCoordinateUiModel(
                coordinate =
                    CoordinateUiModel(
                        37.583961996341635,
                        127.05864615056845,
                    ),
                category = PlaceCategoryUiModel.BOOTH,
            ),
            PlaceCoordinateUiModel(
                coordinate =
                    CoordinateUiModel(
                        37.59,
                        127.058646150568,
                    ),
                category = PlaceCategoryUiModel.BOOTH,
            ),
            PlaceCoordinateUiModel(
                coordinate =
                    CoordinateUiModel(
                        37.583961996341635,
                        127.059,
                    ),
                category = PlaceCategoryUiModel.BOOTH,
            ),
            PlaceCoordinateUiModel(
                coordinate =
                    CoordinateUiModel(
                        37.583201906206114,
                        127.05807556594641,
                    ),
                category = PlaceCategoryUiModel.FOOD_TRUCK,
            ),
            PlaceCoordinateUiModel(
                coordinate =
                    CoordinateUiModel(
                        37.58302357371701,
                        127.06137955847021,
                    ),
                category = PlaceCategoryUiModel.TRASH_CAN,
            ),
            PlaceCoordinateUiModel(
                coordinate =
                    CoordinateUiModel(
                        37.585,
                        127.06137955847021,
                    ),
                category = PlaceCategoryUiModel.TRASH_CAN,
            ),
            PlaceCoordinateUiModel(
                coordinate =
                    CoordinateUiModel(
                        37.583,
                        127.06137955847021,
                    ),
                category = PlaceCategoryUiModel.TRASH_CAN,
            ),
            PlaceCoordinateUiModel(
                coordinate =
                    CoordinateUiModel(
                        37.58383522055214,
                        127.05991407372454,
                    ),
                category = PlaceCategoryUiModel.BAR,
            ),
        )
}

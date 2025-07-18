package com.daedan.festabook.presentation.placeList.dummy

import com.daedan.festabook.presentation.placeList.model.CoordinateUiModel
import com.daedan.festabook.presentation.placeList.model.InitialMapSettingUiModel

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
}

package com.daedan.festabook.placeList

import com.daedan.festabook.domain.model.Coordinate
import com.daedan.festabook.domain.model.OrganizationGeography
import com.daedan.festabook.domain.model.Place
import com.daedan.festabook.domain.model.PlaceCategory
import com.daedan.festabook.domain.model.PlaceGeography

val FAKE_PLACES =
    listOf(
        Place(
            id = 1,
            imageUrl = null,
            category = PlaceCategory.FOOD_TRUCK,
            title = "테스트 1",
            description = "설명 1",
            location = "위치 1",
        ),
    )

val FAKE_PLACE_GEOGRAPHIES =
    listOf(
        PlaceGeography(
            id = 1,
            category = PlaceCategory.FOOD_TRUCK,
            Coordinate(
                latitude = 1.0,
                longitude = 1.0,
            ),
            "푸드트럭",
        ),
        PlaceGeography(
            id = 1,
            category = PlaceCategory.BOOTH,
            Coordinate(
                latitude = 1.0,
                longitude = 1.0,
            ),
            "부스",
        ),
        PlaceGeography(
            id = 1,
            category = PlaceCategory.BAR,
            Coordinate(
                latitude = 1.0,
                longitude = 1.0,
            ),
            "주점",
        ),
    )

val FAKE_ORGANIZATION_GEOGRAPHY =
    OrganizationGeography(
        zoom = 15,
        initialCenter =
            Coordinate(
                latitude = 1.0,
                longitude = 1.0,
            ),
        polygonHoleBoundary =
            listOf(
                Coordinate(
                    latitude = 1.0,
                    longitude = 1.0,
                ),
            ),
    )

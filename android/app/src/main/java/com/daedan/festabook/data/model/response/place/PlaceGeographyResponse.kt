package com.daedan.festabook.data.model.response.place

import com.daedan.festabook.domain.model.Coordinate
import com.daedan.festabook.domain.model.PlaceCategory
import com.daedan.festabook.domain.model.PlaceGeography
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlaceGeographyResponse(
    @SerialName("category")
    val category: PlaceCategory,
    @SerialName("placeId")
    val id: Long,
    @SerialName("markerCoordinate")
    val markerCoordinate: MarkerCoordinate,
) {
    @Serializable
    data class MarkerCoordinate(
        @SerialName("latitude")
        val latitude: Double,
        @SerialName("longitude")
        val longitude: Double,
    )

    enum class PlaceCategory {
        FOOD_TRUCK,
        BOOTH,
        BAR,
        TRASH_CAN,
        TOILET,
        SMOKING,
    }
}

fun PlaceGeographyResponse.toDomain() =
    PlaceGeography(
        id = id,
        category = category.toDomain(),
        markerCoordinate =
            Coordinate(
                latitude = markerCoordinate.latitude,
                longitude = markerCoordinate.longitude,
            ),
    )

fun PlaceGeographyResponse.PlaceCategory.toDomain() =
    when (this) {
        PlaceGeographyResponse.PlaceCategory.BOOTH -> PlaceCategory.BOOTH
        PlaceGeographyResponse.PlaceCategory.BAR -> PlaceCategory.BAR
        PlaceGeographyResponse.PlaceCategory.FOOD_TRUCK -> PlaceCategory.FOOD_TRUCK
        PlaceGeographyResponse.PlaceCategory.SMOKING -> PlaceCategory.SMOKING_AREA
        PlaceGeographyResponse.PlaceCategory.TOILET -> PlaceCategory.TOILET
        PlaceGeographyResponse.PlaceCategory.TRASH_CAN -> PlaceCategory.TRASH_CAN
    }

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
    @SerialName("title")
    val title: String,
    @SerialName("timeTags")
    val timeTags: List<TimeTagResponse>,
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
        PARKING,
        PRIMARY,
        STAGE,
        PHOTO_BOOTH,
        EXTRA,
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
        title = title,
        timeTags = timeTags.map { it.toDomain() },
    )

fun PlaceGeographyResponse.PlaceCategory.toDomain() =
    when (this) {
        PlaceGeographyResponse.PlaceCategory.BOOTH -> PlaceCategory.BOOTH
        PlaceGeographyResponse.PlaceCategory.BAR -> PlaceCategory.BAR
        PlaceGeographyResponse.PlaceCategory.FOOD_TRUCK -> PlaceCategory.FOOD_TRUCK
        PlaceGeographyResponse.PlaceCategory.SMOKING -> PlaceCategory.SMOKING_AREA
        PlaceGeographyResponse.PlaceCategory.TOILET -> PlaceCategory.TOILET
        PlaceGeographyResponse.PlaceCategory.TRASH_CAN -> PlaceCategory.TRASH_CAN
        PlaceGeographyResponse.PlaceCategory.PARKING -> PlaceCategory.PARKING
        PlaceGeographyResponse.PlaceCategory.PRIMARY -> PlaceCategory.PRIMARY
        PlaceGeographyResponse.PlaceCategory.STAGE -> PlaceCategory.STAGE
        PlaceGeographyResponse.PlaceCategory.PHOTO_BOOTH -> PlaceCategory.PHOTO_BOOTH
        PlaceGeographyResponse.PlaceCategory.EXTRA -> PlaceCategory.EXTRA
    }

package com.daedan.festabook.data.model.response

import com.daedan.festabook.domain.model.Coordinate
import com.daedan.festabook.domain.model.PlaceCategory
import com.daedan.festabook.domain.model.PlaceGeography
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlaceGeographyResponse(
    @SerialName("category")
    val category: PlaceCategory,
    @SerialName("id")
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
}

fun PlaceGeographyResponse.toDomain() =
    PlaceGeography(
        id = id,
        category = category,
        markerCoordinate =
            Coordinate(
                latitude = markerCoordinate.latitude,
                longitude = markerCoordinate.longitude,
            ),
    )

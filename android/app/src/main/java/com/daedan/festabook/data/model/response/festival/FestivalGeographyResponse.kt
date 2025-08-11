package com.daedan.festabook.data.model.response.festival

import com.daedan.festabook.domain.model.OrganizationGeography
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FestivalGeographyResponse(
    @SerialName("zoom")
    val zoom: Int,
    @SerialName("centerCoordinate")
    val centerCoordinate: CenterCoordinate,
    @SerialName("polygonHoleBoundary")
    val polygonHoleBoundary: List<PolygonHoleBoundary>,
) {
    @Serializable
    data class CenterCoordinate(
        @SerialName("latitude")
        val latitude: Double,
        @SerialName("longitude")
        val longitude: Double,
    )

    @Serializable
    data class PolygonHoleBoundary(
        @SerialName("latitude")
        val latitude: Double,
        @SerialName("longitude")
        val longitude: Double,
    )
}

fun FestivalGeographyResponse.toDomain() =
    OrganizationGeography(
        zoom = zoom,
        initialCenter =
            com.daedan.festabook.domain.model.Coordinate(
                latitude = centerCoordinate.latitude,
                longitude = centerCoordinate.longitude,
            ),
        polygonHoleBoundary =
            polygonHoleBoundary.map {
                com.daedan.festabook.domain.model.Coordinate(
                    latitude = it.latitude,
                    longitude = it.longitude,
                )
            },
    )

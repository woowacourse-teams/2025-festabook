package com.daedan.festabook.data.model.response.place

import com.daedan.festabook.data.model.response.place.PlaceGeographyResponse.PlaceCategory
import com.daedan.festabook.domain.model.Place
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlaceResponse(
    @SerialName("placeId")
    val id: Long,
    @SerialName("title")
    val title: String?,
    @SerialName("category")
    val category: PlaceCategory,
    @SerialName("description")
    val description: String?,
    @SerialName("imageUrl")
    val imageUrl: String?,
    @SerialName("location")
    val location: String?,
)

fun PlaceResponse.toDomain() =
    Place(
        id = id,
        title = title,
        category = category.toDomain(),
        description = description,
        imageUrl = imageUrl,
        location = location,
    )

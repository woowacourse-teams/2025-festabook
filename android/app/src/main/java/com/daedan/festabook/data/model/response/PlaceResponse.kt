package com.daedan.festabook.data.model.response

import com.daedan.festabook.domain.model.Place
import com.daedan.festabook.domain.model.PlaceCategory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlaceResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("title")
    val title: String,
    @SerialName("category")
    val category: String,
    @SerialName("description")
    val description: String,
    @SerialName("imageUrl")
    val imageUrl: String,
    @SerialName("location")
    val location: String,
)

fun PlaceResponse.toDomain() =
    Place(
        id = id,
        title = title,
        category = category.toPlaceCategory(),
        description = description,
        imageUrl = imageUrl,
        location = location,
    )

fun String.toPlaceCategory(): PlaceCategory =
    when (this) {
        "FOOD_TRUCK" -> PlaceCategory.FOOD_TRUCK
        "BOOTH" -> PlaceCategory.BOOTH
        "BAR" -> PlaceCategory.BAR
        "TRASH_CAN" -> PlaceCategory.TRASH_CAN
        "TOILET" -> PlaceCategory.TOILET
        "SMOKING_AREA" -> PlaceCategory.SMOKING_AREA
        else -> throw IllegalArgumentException("잘못된 카테고리 값입니다")
    }

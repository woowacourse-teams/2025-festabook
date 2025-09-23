package com.daedan.festabook.presentation.placeList.model

import android.os.Parcelable
import com.daedan.festabook.domain.model.Place
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaceUiModel(
    val id: Long,
    val imageUrl: String?,
    val category: PlaceCategoryUiModel,
    val title: String?,
    val description: String?,
    val location: String?,
    val isBookmarked: Boolean = false,
    val timeTagId: List<Long>,
) : Parcelable

fun Place.toUiModel(): PlaceUiModel =
    PlaceUiModel(
        id = id,
        imageUrl = imageUrl,
        category = category.toUiModel(),
        title = title,
        description = description,
        location = location,
        timeTagId =
            timeTags.map {
                it.timeTagId
            },
    )

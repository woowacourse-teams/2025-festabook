package com.daedan.festabook.presentation.placeDetail.model

import com.daedan.festabook.domain.model.PlaceDetailImage

data class ImageUiModel(
    val url: String,
    val id: Long,
)

fun PlaceDetailImage.toUiModel() =
    ImageUiModel(
        url = imageUrl,
        id = id,
    )

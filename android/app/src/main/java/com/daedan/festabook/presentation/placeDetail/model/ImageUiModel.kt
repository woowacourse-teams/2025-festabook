package com.daedan.festabook.presentation.placeDetail.model

import com.daedan.festabook.domain.model.PlaceDetailImage

data class ImageUiModel(
    val url: String?,
    val id: Long,
) {
    companion object {}
}

fun PlaceDetailImage.toUiModel() =
    ImageUiModel(
        url = imageUrl,
        id = id,
    )

fun ImageUiModel.Companion.emptyUiModel() =
    ImageUiModel(
        null,
        0,
    )

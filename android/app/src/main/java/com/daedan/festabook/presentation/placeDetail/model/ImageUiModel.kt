package com.daedan.festabook.presentation.placeDetail.model

import android.os.Parcelable
import com.daedan.festabook.domain.model.PlaceDetailImage
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageUiModel(
    val url: String? = null,
    val id: Long = -1,
) : Parcelable {
    companion object {}
}

fun PlaceDetailImage.toUiModel() =
    ImageUiModel(
        url = imageUrl,
        id = id,
    )

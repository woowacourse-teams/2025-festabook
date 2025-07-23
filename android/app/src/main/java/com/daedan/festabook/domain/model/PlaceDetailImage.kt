package com.daedan.festabook.domain.model

data class PlaceDetailImage(
    val id: Long,
    val imageUrl: String,
    val sequence: Int,
) {
    companion object {
        const val DEFAULT_IMAGE_URL = ""
    }
}

package com.daedan.festabook.data.model.response.lostitem

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LostGuideItemResponse(
    @SerialName("lostItemGuide")
    val lostItemGuide: String,
)

package com.daedan.festabook.data.model.response.lostitem

import com.daedan.festabook.domain.model.Lost
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LostGuideItemResponse(
    @SerialName("lostItemGuide")
    val lostItemGuide: String,
)

fun LostGuideItemResponse.toDomain(): Lost.Guide =
    Lost.Guide(
        guide = lostItemGuide,
    )

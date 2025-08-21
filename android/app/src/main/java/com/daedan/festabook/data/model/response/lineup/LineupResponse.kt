package com.daedan.festabook.data.model.response.lineup

import com.daedan.festabook.domain.model.LineupItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LineupResponse(
    @SerialName("lineupId")
    val lineupId: Long,
    @SerialName("imageUrl")
    val imageUrl: String,
    @SerialName("name")
    val name: String,
    @SerialName("performanceAt")
    val performanceAt: String,
)

fun LineupResponse.toDomain(): LineupItem =
    LineupItem(
        id = lineupId,
        imageUrl = imageUrl,
        name = name,
    )

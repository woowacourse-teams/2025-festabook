package com.daedan.festabook.data.model.response.place

import com.daedan.festabook.domain.model.TimeTag
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TimeTagResponse(
    @SerialName("name")
    val name: String,
    @SerialName("timeTagId")
    val timeTagId: Long,
)

fun TimeTagResponse.toDomain() =
    TimeTag(
        name = name,
        timeTagId = timeTagId,
    )

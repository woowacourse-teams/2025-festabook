package com.daedan.festabook.presentation.placeDetail.model

import com.daedan.festabook.domain.model.PlaceDetail
import com.daedan.festabook.presentation.news.notice.model.NoticeUiModel
import com.daedan.festabook.presentation.news.notice.model.toUiModel
import com.daedan.festabook.presentation.placeList.model.PlaceUiModel
import com.daedan.festabook.presentation.placeList.model.toUiModel

data class PlaceDetailUiModel(
    val place: PlaceUiModel,
    val notices: List<NoticeUiModel>,
    val host: String,
    val startTime: String,
    val endTime: String,
    val images: List<ImageUiModel>,
)

fun PlaceDetail.toUiModel() =
    PlaceDetailUiModel(
        place = place.toUiModel(),
        notices = notices.map { it.toUiModel() },
        host = host,
        startTime = startTime,
        endTime = endTime,
        images = images.map { it.toUiModel() },
    )

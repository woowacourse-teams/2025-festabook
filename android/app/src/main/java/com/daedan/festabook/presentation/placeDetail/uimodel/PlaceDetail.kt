package com.daedan.festabook.presentation.placeDetail.uimodel

import com.daedan.festabook.presentation.news.notice.model.NoticeUiModel
import com.daedan.festabook.presentation.placeList.model.PlaceUiModel

data class PlaceDetail(
    val place: PlaceUiModel,
    val notices: List<NoticeUiModel>,
    val host: String,
    val startTime: String,
    val endTime: String,
    val images: List<String>,
)

package com.daedan.festabook.presentation.placeDetail.uimodel

import com.daedan.festabook.presentation.news.notice.NoticeUiModel
import com.daedan.festabook.presentation.placeList.uimodel.Place

data class PlaceDetail(
    val place: Place,
    val notices: List<NoticeUiModel>,
    val host: String,
    val startTime: String,
    val endTime: String,
)

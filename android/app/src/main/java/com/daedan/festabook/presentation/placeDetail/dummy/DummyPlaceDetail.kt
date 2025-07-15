package com.daedan.festabook.presentation.placeDetail.dummy

import com.daedan.festabook.presentation.news.notice.NoticeUiModel
import com.daedan.festabook.presentation.placeDetail.uimodel.PlaceDetail
import com.daedan.festabook.presentation.placeList.uimodel.Place

object DummyPlaceDetail {
    private val noticeList =
        listOf(
            NoticeUiModel(
                id = 1,
                title = "매장 공지사항",
                description = "공지사항 설명",
                createdAt = "2025-07-14T05:22:39.963Z",
                isExpanded = false,
            ),
            NoticeUiModel(
                id = 2,
                title = "매장 공지사항",
                description = "공지사항 설명",
                createdAt = "2025-07-14T05:22:39.963Z",
                isExpanded = true,
            ),
            NoticeUiModel(
                id = 3,
                title = "매장 공지사항",
                description = "공지사항 설명",
                createdAt = "2025-07-14T05:22:39.963Z",
                isExpanded = true,
            ),
        )

    fun create(place: Place): PlaceDetail =
        PlaceDetail(
            place = place,
            notices = noticeList,
            host = "C블C블",
            startTime = "09:00",
            endTime = "18:00",
        )
}

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
            images =
                listOf(
                    "https://img1.bizhows.com/bhfile01/__CM_FILE_DATA/201911/20/18/1481577_1574242984817.jpg",
                    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR0aVVMwlpW32jjvP9Nlw8WAaLLDP-_nhS2Ng&s",
                    "https://dumbchicken.co.kr/wp-content/uploads/2024/05/%EB%8D%A4%ED%94%84%EC%B9%98%ED%82%A80457-copy.jpg",
                ),
        )
}

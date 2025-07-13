package com.daedan.festabook.presentation.placeDetail.dummy

import com.daedan.festabook.presentation.news.notice.NoticeUiModel

object DummyNotice {
    val noticeList =
        listOf(
            NoticeUiModel(
                id = 1,
                title = "매장 공지사항",
                description = "공지사항 설명",
                createdAt = "2025-07-14T05:22:39.963Z",
                isExpanded = false,
            ),
            NoticeUiModel(
                id = 1,
                title = "매장 공지사항",
                description = "공지사항 설명",
                createdAt = "2025-07-14T05:22:39.963Z",
                isExpanded = true,
            ),
        )
}

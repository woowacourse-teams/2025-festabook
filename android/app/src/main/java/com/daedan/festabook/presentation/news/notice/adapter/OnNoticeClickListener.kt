package com.daedan.festabook.presentation.news.notice.adapter

import com.daedan.festabook.presentation.news.notice.NoticeUiModel

fun interface OnNoticeClickListener {
    fun onNoticeClick(notice: NoticeUiModel)
}

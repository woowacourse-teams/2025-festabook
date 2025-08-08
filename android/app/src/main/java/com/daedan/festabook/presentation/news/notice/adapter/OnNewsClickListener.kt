package com.daedan.festabook.presentation.news.notice.adapter

import com.daedan.festabook.presentation.news.faq.model.FAQItemUiModel
import com.daedan.festabook.presentation.news.lost.model.LostItemUiModel
import com.daedan.festabook.presentation.news.notice.model.NoticeUiModel

interface OnNewsClickListener {
    fun onNoticeClick(notice: NoticeUiModel)

    fun onFAQClick(faqItem: FAQItemUiModel)

    fun onLostItemClick(lostItem: LostItemUiModel)
}

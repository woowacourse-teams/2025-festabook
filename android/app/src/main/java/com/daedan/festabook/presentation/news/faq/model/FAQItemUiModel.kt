package com.daedan.festabook.presentation.news.faq.model

data class FAQItemUiModel(
    val questionId: Long,
    val question: String,
    val answer: String,
    val sequence: Int,
)

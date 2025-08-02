package com.daedan.festabook.domain.model

data class FAQItem(
    val questionId: Long,
    val question: String,
    val answer: String,
    val sequence: Int,
)

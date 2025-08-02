package com.daedan.festabook.presentation.news.faq.model

import com.daedan.festabook.domain.model.FAQItem

data class FAQItemUiModel(
    val questionId: Long,
    val question: String,
    val answer: String,
    val sequence: Int,
    val isExpanded: Boolean = false,
)

fun FAQItem.toUiModel(): FAQItemUiModel =
    FAQItemUiModel(
        questionId = questionId,
        question = question,
        answer = answer,
        sequence = sequence,
    )

package com.daedan.festabook.data.model.response.faq

import com.daedan.festabook.domain.model.FAQItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FAQResponse(
    @SerialName("questionId")
    val questionId: Long,
    @SerialName("question")
    val question: String,
    @SerialName("answer")
    val answer: String,
    @SerialName("sequence")
    val sequence: Int,
)

fun FAQResponse.toDomain(): FAQItem =
    FAQItem(
        questionId = questionId,
        question = question,
        answer = answer,
        sequence = sequence,
    )

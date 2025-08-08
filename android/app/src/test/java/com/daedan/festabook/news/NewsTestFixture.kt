package com.daedan.festabook.news

import com.daedan.festabook.domain.model.FAQItem
import com.daedan.festabook.domain.model.LostItem
import com.daedan.festabook.domain.model.Notice
import java.time.LocalDateTime

val FAKE_NOTICES =
    listOf(
        Notice(
            id = 1,
            title = "테스트 1",
            content = "테스트 1",
            isPinned = false,
            createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
        ),
        Notice(
            id = 2,
            title = "테스트 2",
            content = "테스트 2",
            isPinned = true,
            createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
        ),
    )

val FAKE_FAQS =
    listOf(
        FAQItem(
            questionId = 1,
            question = "테스트 질문 1",
            answer = "테스트 답변 1",
            sequence = 1,
        ),
    )

val FAKE_LOST_ITEM =
    listOf(
        LostItem(
            imageId = 1,
            imageUrl = "테스트 이미지 주소",
            storageLocation = "테스트 장소",
        ),
    )

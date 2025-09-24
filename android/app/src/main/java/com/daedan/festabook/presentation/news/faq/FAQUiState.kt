package com.daedan.festabook.presentation.news.faq

import com.daedan.festabook.presentation.news.faq.model.FAQItemUiModel

sealed interface FAQUiState {
    data object InitialLoading : FAQUiState

    data class Success(
        val faqs: List<FAQItemUiModel>,
    ) : FAQUiState

    data class Error(
        val throwable: Throwable,
    ) : FAQUiState
}

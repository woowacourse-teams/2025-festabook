package com.daedan.festabook.presentation.news.notice

import com.daedan.festabook.presentation.news.notice.model.NoticeUiModel

sealed interface NoticeUiState {
    data object Loading : NoticeUiState

    data class Success(
        val notices: List<NoticeUiModel>,
    ) : NoticeUiState

    data class Error(
        val message: String,
    ) : NoticeUiState
}

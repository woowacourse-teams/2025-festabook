package com.daedan.festabook.presentation.news.notice

import com.daedan.festabook.presentation.news.notice.model.NoticeUiModel

sealed interface NoticeUiState {
    data class Refreshing(
        val oldNotices: List<NoticeUiModel>,
    ) : NoticeUiState

    data object InitialLoading : NoticeUiState

    data class Success(
        val notices: List<NoticeUiModel>,
        val expandPosition: Int,
    ) : NoticeUiState

    data class Error(
        val throwable: Throwable,
    ) : NoticeUiState

    companion object {
        const val DEFAULT_POSITION: Int = 0
    }
}

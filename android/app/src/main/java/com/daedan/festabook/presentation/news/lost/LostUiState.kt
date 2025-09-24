package com.daedan.festabook.presentation.news.lost

import com.daedan.festabook.presentation.news.lost.model.LostUiModel

sealed interface LostUiState {
    data object InitialLoading : LostUiState

    data object Refreshing : LostUiState

    data class Success(
        val lostItems: List<LostUiModel>,
    ) : LostUiState

    data class Error(
        val throwable: Throwable,
    ) : LostUiState
}

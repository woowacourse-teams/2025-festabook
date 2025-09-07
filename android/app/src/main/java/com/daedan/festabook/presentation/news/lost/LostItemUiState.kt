package com.daedan.festabook.presentation.news.lost

import com.daedan.festabook.presentation.news.lost.model.LostItemUiModel

sealed interface LostItemUiState {
    data object InitialLoading : LostItemUiState

    data object Refreshing : LostItemUiState

    data class Success(
        val lostItems: List<LostItemUiModel>,
    ) : LostItemUiState

    data class Error(
        val throwable: Throwable,
    ) : LostItemUiState
}

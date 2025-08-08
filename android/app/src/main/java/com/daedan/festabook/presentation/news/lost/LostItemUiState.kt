package com.daedan.festabook.presentation.news.lost

import com.daedan.festabook.presentation.news.lost.model.LostItemUiModel

interface LostItemUiState {
    data object InitialLoading : LostItemUiState

    data object Loading : LostItemUiState

    data class Success(
        val lostItems: List<LostItemUiModel>,
    ) : LostItemUiState

    data class Error(
        val throwable: Throwable,
    ) : LostItemUiState
}

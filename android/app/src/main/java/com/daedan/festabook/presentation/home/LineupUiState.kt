package com.daedan.festabook.presentation.home

sealed interface LineupUiState {
    data object Loading : LineupUiState

    data class Success(
        val lineups: LineUpItemGroupUiModel,
    ) : LineupUiState

    data class Error(
        val throwable: Throwable,
    ) : LineupUiState
}

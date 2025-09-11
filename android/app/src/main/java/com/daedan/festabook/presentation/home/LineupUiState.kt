package com.daedan.festabook.presentation.home

import java.time.LocalDateTime

sealed interface LineupUiState {
    data object Loading : LineupUiState

    data class Success(
        val lineups: LineUpItemGroupUiModel,
    ) : LineupUiState

    data class Error(
        val throwable: Throwable,
    ) : LineupUiState
}

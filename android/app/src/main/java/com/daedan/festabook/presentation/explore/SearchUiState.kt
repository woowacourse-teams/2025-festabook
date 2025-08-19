package com.daedan.festabook.presentation.explore

import com.daedan.festabook.domain.model.University

sealed interface SearchUiState {
    data object Idle : SearchUiState

    data object Loading : SearchUiState

    data class Success(
        val value: List<University>,
    ) : SearchUiState

    data class Error(
        val throwable: Throwable,
    ) : SearchUiState
}

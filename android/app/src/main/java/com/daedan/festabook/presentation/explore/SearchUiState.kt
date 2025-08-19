package com.daedan.festabook.presentation.explore

import com.daedan.festabook.domain.model.University

sealed interface SearchUiState {
    data object Idle : SearchUiState

    data object Loading : SearchUiState

    data class Success(
        val universitiesFound: List<University> = emptyList(),
        val selectedUniversity: University? = null,
    ) : SearchUiState

    data class Error(
        val throwable: Throwable,
    ) : SearchUiState
}

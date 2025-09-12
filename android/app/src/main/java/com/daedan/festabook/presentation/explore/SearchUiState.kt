package com.daedan.festabook.presentation.explore

import com.daedan.festabook.presentation.explore.model.SearchResultUiModel

sealed interface SearchUiState {
    data object Idle : SearchUiState

    data object Loading : SearchUiState

    data class Success(
        val universitiesFound: List<SearchResultUiModel> = emptyList(),
//        val selectedUniversity: University? = null,
    ) : SearchUiState

    data class Error(
        val throwable: Throwable,
    ) : SearchUiState
}

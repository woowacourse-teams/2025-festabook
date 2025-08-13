package com.daedan.festabook.presentation.explore

sealed interface SearchUiState<T> {
    class Idle<T> : SearchUiState<T>

    class Loading<T> : SearchUiState<T>

    data class Success<T>(
        val value: T,
    ) : SearchUiState<T>

    data class Error<T>(
        val throwable: Throwable,
    ) : SearchUiState<T>
}

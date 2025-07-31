package com.daedan.festabook.presentation.placeList.model

sealed interface PlaceListUiState<T> {
    class Loading<T> : PlaceListUiState<T>

    data class Success<T>(
        val value: T,
    ) : PlaceListUiState<T>

    data class Error<T>(
        val throwable: Throwable,
    ) : PlaceListUiState<T>
}

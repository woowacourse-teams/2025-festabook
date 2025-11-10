package com.daedan.festabook.presentation.placeMap.model

sealed interface PlaceListUiState<T> {
    class Loading<T> : PlaceListUiState<T>

    data class Success<T>(
        val value: T,
    ) : PlaceListUiState<T>

    data class PlaceLoaded(
        val value: List<PlaceUiModel>,
    ) : PlaceListUiState<List<PlaceUiModel>>

    data class Error<T>(
        val throwable: Throwable,
    ) : PlaceListUiState<T>

    class Complete<T> : PlaceListUiState<T>
}

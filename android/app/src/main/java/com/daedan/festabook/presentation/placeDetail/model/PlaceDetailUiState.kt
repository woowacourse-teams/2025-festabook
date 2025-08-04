package com.daedan.festabook.presentation.placeDetail.model

sealed interface PlaceDetailUiState {
    data object Loading : PlaceDetailUiState

    data class Success(
        val placeDetail: PlaceDetailUiModel,
    ) : PlaceDetailUiState

    data class Error(
        val throwable: Throwable,
    ) : PlaceDetailUiState
}

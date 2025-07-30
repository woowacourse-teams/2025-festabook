package com.daedan.festabook.presentation.placeDetail.model

sealed interface PlaceDetailUiState {
    data object Loading : PlaceDetailUiState

    data class Success(
        val placeDetail: PlaceDetailUiModel,
    ) : PlaceDetailUiState

    data class Error(
        val message: String,
        val throwable: Throwable? = null,
    ) : PlaceDetailUiState
}

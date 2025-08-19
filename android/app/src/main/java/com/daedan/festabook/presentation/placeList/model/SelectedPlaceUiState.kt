package com.daedan.festabook.presentation.placeList.model

import com.daedan.festabook.presentation.placeDetail.model.PlaceDetailUiModel

sealed interface SelectedPlaceUiState {
    data object Loading : SelectedPlaceUiState

    data object Empty : SelectedPlaceUiState

    data class Success(
        val value: PlaceDetailUiModel,
    ) : SelectedPlaceUiState {
        val isSecondary = value.place.category in PlaceCategoryUiModel.SECONDARY_CATEGORIES
    }

    data class Error(
        val throwable: Throwable,
    ) : SelectedPlaceUiState
}

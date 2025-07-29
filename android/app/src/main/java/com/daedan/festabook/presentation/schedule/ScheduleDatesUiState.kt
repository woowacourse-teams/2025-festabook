package com.daedan.festabook.presentation.schedule

import com.daedan.festabook.presentation.schedule.model.ScheduleDateUiModel

sealed interface ScheduleDatesUiState {
    data object Loading : ScheduleDatesUiState

    data class Success(
        val dates: List<ScheduleDateUiModel>,
        val initialCurrentDateIndex: Int,
    ) : ScheduleDatesUiState

    data class Error(
        val message: String,
    ) : ScheduleDatesUiState
}

package com.daedan.festabook.presentation.schedule

import com.daedan.festabook.presentation.schedule.model.ScheduleEventUiModel

sealed class ScheduleUiState {
    data object Loading : ScheduleUiState()

    data class Success(
        val events: List<ScheduleEventUiModel>,
    ) : ScheduleUiState()

    data class Error(
        val message: String,
    ) : ScheduleUiState()
}

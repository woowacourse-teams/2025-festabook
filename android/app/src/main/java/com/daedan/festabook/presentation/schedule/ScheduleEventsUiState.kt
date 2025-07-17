package com.daedan.festabook.presentation.schedule

import com.daedan.festabook.presentation.schedule.model.ScheduleEventUiModel

sealed class ScheduleEventsUiState {
    data object Loading : ScheduleEventsUiState()

    data class Success(
        val events: List<ScheduleEventUiModel>,
    ) : ScheduleEventsUiState()

    data class Error(
        val message: String,
    ) : ScheduleEventsUiState()
}

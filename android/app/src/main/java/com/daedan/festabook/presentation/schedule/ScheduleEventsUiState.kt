package com.daedan.festabook.presentation.schedule

import com.daedan.festabook.presentation.schedule.model.ScheduleEventUiModel

sealed interface ScheduleEventsUiState {
    data object Loading : ScheduleEventsUiState

    data class Success(
        val events: List<ScheduleEventUiModel>,
        val currentEventPosition: Int,
    ) : ScheduleEventsUiState

    data class Error(
        val throwable: Throwable,
    ) : ScheduleEventsUiState
}

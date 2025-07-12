package com.daedan.festabook.presentation.schedule

import com.daedan.festabook.domain.model.ScheduleEvent

sealed class ScheduleUiState {
    data object Loading : ScheduleUiState()

    data class Success(
        val events: List<ScheduleEvent>,
    ) : ScheduleUiState()

    data class Error(
        val message: String,
    ) : ScheduleUiState()
}

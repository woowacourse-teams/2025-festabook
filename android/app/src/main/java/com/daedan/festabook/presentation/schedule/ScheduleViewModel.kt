package com.daedan.festabook.presentation.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.daedan.festabook.FestaBookApp
import com.daedan.festabook.domain.model.ScheduleEvent
import com.daedan.festabook.domain.repository.ScheduleRepository

class ScheduleViewModel(
    private val scheduleRepository: ScheduleRepository,
) : ViewModel() {
    private val _scheduleUiState: MutableLiveData<ScheduleUiState> =
        MutableLiveData<ScheduleUiState>()
    val scheduleUiState: LiveData<ScheduleUiState> get() = _scheduleUiState

    init {
        loadSchedule()
    }

    fun updateBookmark(scheduleEventId: Long) {
        updateUiState { scheduleEvents ->
            scheduleEvents.map { scheduleEvent ->
                if (scheduleEvent.id == scheduleEventId) {
                    scheduleEvent.copy(isBookmarked = !scheduleEvent.isBookmarked)
                } else {
                    scheduleEvent
                }
            }
        }
    }

    private fun loadSchedule() {
        val result = scheduleRepository.dummyScheduleEvents
        _scheduleUiState.value = ScheduleUiState.Success(result)
    }

    private fun updateUiState(onUpdate: (List<ScheduleEvent>) -> List<ScheduleEvent>) {
        val currentState = _scheduleUiState.value ?: return
        _scheduleUiState.value =
            when (currentState) {
                is ScheduleUiState.Success -> currentState.copy(events = onUpdate(currentState.events))
                is ScheduleUiState.Loading,
                is ScheduleUiState.Error,
                -> currentState
            }
    }

    companion object {
        val Factory: ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras,
                ): T {
                    val application = checkNotNull(extras[APPLICATION_KEY]) as FestaBookApp
                    return ScheduleViewModel(
                        scheduleRepository = application.scheduleRepository,
                    ) as T
                }
            }
    }
}

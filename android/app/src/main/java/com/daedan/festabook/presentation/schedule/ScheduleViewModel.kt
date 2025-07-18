package com.daedan.festabook.presentation.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.daedan.festabook.FestaBookApp
import com.daedan.festabook.domain.repository.ScheduleRepository
import com.daedan.festabook.presentation.schedule.model.ScheduleEventUiModel
import com.daedan.festabook.presentation.schedule.model.toUiModel
import kotlinx.coroutines.launch

class ScheduleViewModel(
    private val scheduleRepository: ScheduleRepository,
) : ViewModel() {
    private val _scheduleEventsUiState: MutableLiveData<ScheduleEventsUiState> =
        MutableLiveData<ScheduleEventsUiState>()
    val scheduleEventsUiState: LiveData<ScheduleEventsUiState> get() = _scheduleEventsUiState

    private val _scheduleDatesUiState: MutableLiveData<ScheduleDatesUiState> =
        MutableLiveData<ScheduleDatesUiState>()
    val scheduleDatesUiState: LiveData<ScheduleDatesUiState> get() = _scheduleDatesUiState

    init {
        loadAllScheduleDates()
    }

    fun updateBookmark(scheduleEventId: Long) {
        updateScheduleEventsUiState { scheduleEvents ->
            scheduleEvents.map { scheduleEvent ->
                if (scheduleEvent.id == scheduleEventId) {
                    scheduleEvent.copy(isBookmarked = !scheduleEvent.isBookmarked)
                } else {
                    scheduleEvent
                }
            }
        }
    }

    fun loadScheduleByDate(dateId: Long) {
        viewModelScope.launch {
            _scheduleEventsUiState.value = ScheduleEventsUiState.Loading

            val result = scheduleRepository.fetchScheduleEventsById(dateId)
            result
                .onSuccess { scheduleEvents ->
                    val scheduleEventUiModels = scheduleEvents.map { it.toUiModel() }
                    _scheduleEventsUiState.value =
                        ScheduleEventsUiState.Success(scheduleEventUiModels)
                }.onFailure {
                    _scheduleEventsUiState.value =
                        ScheduleEventsUiState.Error(it.message.toString())
                }
        }
    }

    private fun loadAllScheduleDates() {
        viewModelScope.launch {
            _scheduleDatesUiState.value = ScheduleDatesUiState.Loading

            val result = scheduleRepository.fetchAllScheduleDates()
            result
                .onSuccess { scheduleDates ->
                    val scheduleDateUiModels = scheduleDates.map { it.toUiModel() }
                    _scheduleDatesUiState.value = ScheduleDatesUiState.Success(scheduleDateUiModels)
                }.onFailure {
                    _scheduleDatesUiState.value = ScheduleDatesUiState.Error(it.message.toString())
                }
        }
    }

    private fun updateScheduleEventsUiState(onUpdate: (List<ScheduleEventUiModel>) -> List<ScheduleEventUiModel>) {
        val currentState = _scheduleEventsUiState.value ?: return
        _scheduleEventsUiState.value =
            when (currentState) {
                is ScheduleEventsUiState.Success -> currentState.copy(events = onUpdate(currentState.events))
                is ScheduleEventsUiState.Loading,
                is ScheduleEventsUiState.Error,
                -> currentState
            }
    }

    companion object {
        val Factory: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val scheduleRepository =
                        (this[APPLICATION_KEY] as FestaBookApp).appContainer.scheduleRepository
                    ScheduleViewModel(scheduleRepository)
                }
            }
    }
}

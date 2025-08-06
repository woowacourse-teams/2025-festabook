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
import com.daedan.festabook.presentation.schedule.model.ScheduleEventUiStatus
import com.daedan.festabook.presentation.schedule.model.toUiModel
import kotlinx.coroutines.launch
import java.time.LocalDate

class ScheduleViewModel(
    private val scheduleRepository: ScheduleRepository,
    private val dateId: Long,
) : ViewModel() {
    private val _scheduleEventsUiState: MutableLiveData<ScheduleEventsUiState> =
        MutableLiveData<ScheduleEventsUiState>()
    val scheduleEventsUiState: LiveData<ScheduleEventsUiState> get() = _scheduleEventsUiState

    private val _scheduleDatesUiState: MutableLiveData<ScheduleDatesUiState> =
        MutableLiveData<ScheduleDatesUiState>()
    val scheduleDatesUiState: LiveData<ScheduleDatesUiState> get() = _scheduleDatesUiState

    init {
        loadAllDates()
        if (dateId != INVALID_ID) loadScheduleByDate()
    }

    fun updateBookmark(scheduleEventId: Long) {
        updateScheduleEvents { scheduleEvents ->
            scheduleEvents.map { scheduleEvent ->
                if (scheduleEvent.id == scheduleEventId) {
                    scheduleEvent.copy(isBookmarked = !scheduleEvent.isBookmarked)
                } else {
                    scheduleEvent
                }
            }
        }
    }

    fun loadScheduleByDate() {
        if (dateId == INVALID_ID) return
        viewModelScope.launch {
            _scheduleEventsUiState.value = ScheduleEventsUiState.Loading

            val result = scheduleRepository.fetchScheduleEventsById(dateId)
            result
                .onSuccess { scheduleEvents ->
                    val scheduleEventUiModels = scheduleEvents.map { it.toUiModel() }
                    val currentEventPosition =
                        scheduleEventUiModels
                            .indexOfFirst { scheduleEvent -> scheduleEvent.status != ScheduleEventUiStatus.COMPLETED }
                            .let { currentIndex -> if (currentIndex == INVALID_INDEX) FIRST_INDEX else currentIndex }

                    _scheduleEventsUiState.value =
                        ScheduleEventsUiState.Success(scheduleEventUiModels, currentEventPosition)
                }.onFailure {
                    _scheduleEventsUiState.value =
                        ScheduleEventsUiState.Error(it)
                }
        }
    }

    fun loadAllDates() {
        viewModelScope.launch {
            _scheduleDatesUiState.value = ScheduleDatesUiState.Loading

            val result = scheduleRepository.fetchAllScheduleDates()
            result
                .onSuccess { scheduleDates ->
                    val scheduleDateUiModels = scheduleDates.map { it.toUiModel() }
                    val today = LocalDate.now()

                    val currentDatePosition =
                        scheduleDates
                            .indexOfFirst { !it.date.isBefore(today) }
                            .let { currentIndex -> if (currentIndex == INVALID_INDEX) FIRST_INDEX else currentIndex }

                    _scheduleDatesUiState.value =
                        ScheduleDatesUiState.Success(scheduleDateUiModels, currentDatePosition)
                }.onFailure {
                    _scheduleDatesUiState.value = ScheduleDatesUiState.Error(it)
                }
        }
    }

    private fun updateScheduleEvents(onUpdate: (List<ScheduleEventUiModel>) -> List<ScheduleEventUiModel>) {
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
        const val INVALID_ID: Long = -1L
        private const val FIRST_INDEX: Int = 0
        private const val INVALID_INDEX: Int = -1

        fun factory(dateId: Long = INVALID_ID): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val scheduleRepository =
                        (this[APPLICATION_KEY] as FestaBookApp).appContainer.scheduleRepository

                    ScheduleViewModel(scheduleRepository, dateId)
                }
            }
    }
}

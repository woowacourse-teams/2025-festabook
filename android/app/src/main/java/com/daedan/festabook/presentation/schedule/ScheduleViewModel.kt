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
import com.daedan.festabook.presentation.schedule.model.ScheduleEventUiStatus
import com.daedan.festabook.presentation.schedule.model.toUiModel
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.launch
import java.time.LocalDate

class ScheduleViewModel @AssistedInject constructor(
    private val scheduleRepository: ScheduleRepository,
    @Assisted private val dateId: Long,
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun create(dateId: Long): ScheduleViewModel
    }

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

    fun loadScheduleByDate() {
        if (dateId == INVALID_ID) return
        if (_scheduleEventsUiState.value == ScheduleEventsUiState.Loading) return
        viewModelScope.launch {
            _scheduleEventsUiState.value = ScheduleEventsUiState.Loading

            val result = scheduleRepository.fetchScheduleEventsById(dateId)
            result
                .onSuccess { scheduleEvents ->
                    val scheduleEventUiModels = scheduleEvents.map { it.toUiModel() }
                    val currentEventPosition =
                        scheduleEventUiModels
                            .indexOfFirst { scheduleEvent -> scheduleEvent.status == ScheduleEventUiStatus.ONGOING }
                            .coerceAtLeast(FIRST_INDEX)

                    _scheduleEventsUiState.value =
                        ScheduleEventsUiState.Success(scheduleEventUiModels, currentEventPosition)
                }.onFailure {
                    _scheduleEventsUiState.value =
                        ScheduleEventsUiState.Error(it)
                }
        }
    }

    fun loadAllDates() {
        if (_scheduleDatesUiState.value == ScheduleDatesUiState.Loading) return
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

    companion object {
        const val INVALID_ID: Long = -1L
        private const val FIRST_INDEX: Int = 0
        private const val INVALID_INDEX: Int = -1

        fun factory(
            factory: Factory,
            dateId: Long = INVALID_ID
        ): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    factory.create(dateId)
                }
            }
    }
}

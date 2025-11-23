package com.daedan.festabook.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daedan.festabook.di.viewmodel.ViewModelKey
import com.daedan.festabook.di.viewmodel.ViewModelScope
import com.daedan.festabook.domain.repository.FestivalRepository
import com.daedan.festabook.presentation.common.SingleLiveData
import com.daedan.festabook.presentation.home.adapter.FestivalUiState
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.launch

@ContributesIntoMap(AppScope::class)
@ViewModelKey(HomeViewModel::class)
class HomeViewModel @Inject constructor(
    private val festivalRepository: FestivalRepository,
) : ViewModel() {
    private val _festivalUiState = MutableLiveData<FestivalUiState>()
    val festivalUiState: LiveData<FestivalUiState> get() = _festivalUiState

    private val _lineupUiState = MutableLiveData<LineupUiState>()
    val lineupUiState: LiveData<LineupUiState> get() = _lineupUiState

    private val _navigateToScheduleEvent: SingleLiveData<Unit> = SingleLiveData()
    val navigateToScheduleEvent: LiveData<Unit> get() = _navigateToScheduleEvent

    init {
        loadFestival()
        loadLineup()
    }

    fun loadFestival() {
        viewModelScope.launch {
            _festivalUiState.value = FestivalUiState.Loading

            val result = festivalRepository.getFestivalInfo()
            result
                .onSuccess { festival ->
                    _festivalUiState.value = FestivalUiState.Success(festival)
                }.onFailure {
                    _festivalUiState.value = FestivalUiState.Error(it)
                }
        }
    }

    fun navigateToScheduleClick() {
        _navigateToScheduleEvent.setValue(Unit)
    }

    private fun loadLineup() {
        viewModelScope.launch {
            _lineupUiState.value = LineupUiState.Loading

            val result = festivalRepository.getLineUpGroupByDate()
            result
                .onSuccess { lineups ->
                    _lineupUiState.value =
                        LineupUiState.Success(
                            lineups.toUiModel(),
                        )
                }.onFailure {
                    _lineupUiState.value = LineupUiState.Error(it)
                }
        }
    }
}

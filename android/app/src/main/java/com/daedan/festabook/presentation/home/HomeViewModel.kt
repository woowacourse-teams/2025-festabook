package com.daedan.festabook.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.daedan.festabook.FestaBookApp
import com.daedan.festabook.domain.repository.FestivalRepository
import com.daedan.festabook.presentation.home.adapter.FestivalUiState
import kotlinx.coroutines.launch

class HomeViewModel(
    private val festivalRepository: FestivalRepository,
) : ViewModel() {
    private val _festivalUiState = MutableLiveData<FestivalUiState>()
    val festivalUiState: LiveData<FestivalUiState> get() = _festivalUiState

    init {
        loadFestival()
    }

    private fun loadFestival() {
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

    companion object {
        val Factory: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val festivalRepository =
                        (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FestaBookApp).appContainer.festivalRepository
                    HomeViewModel(festivalRepository)
                }
            }
    }
}

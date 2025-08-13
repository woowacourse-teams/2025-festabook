package com.daedan.festabook.presentation.explore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.daedan.festabook.FestaBookApp
import com.daedan.festabook.domain.model.University
import com.daedan.festabook.domain.repository.ExploreRepository
import com.daedan.festabook.presentation.common.SingleLiveData
import kotlinx.coroutines.launch
import timber.log.Timber

class ExploreViewModel(
    private val exploreRepository: ExploreRepository,
) : ViewModel() {
    private val _searchState = MutableLiveData<SearchUiState<University?>>()
    val searchState: LiveData<SearchUiState<University?>> = _searchState

    private val _navigateToMain = SingleLiveData<University?>()
    val navigateToMain: LiveData<University?> = _navigateToMain

    fun search(query: String) {
        if (query.length < 2) {
            _searchState.value = SearchUiState.Idle()
            return
        }

        _searchState.value = SearchUiState.Loading()

        viewModelScope.launch {
            val result = exploreRepository.search(query)
            result
                .onSuccess { university ->
                    Timber.d("검색 성공 - received: $university")
                    _searchState.value = SearchUiState.Success(university)
                }.onFailure {
                    _searchState.value = SearchUiState.Error(it)
                }
        }
    }

    fun onNavigateIconClicked() {
        val currentState = searchState.value
        if (currentState is SearchUiState.Success && currentState.value != null) {
            Timber.d("festivalId 로 화면 이동 - ${currentState.value.festivalId}")
            _navigateToMain.setValue(currentState.value)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val exploreRepository =
                        (this[APPLICATION_KEY] as FestaBookApp).appContainer.exploreRepository
                    ExploreViewModel(exploreRepository)
                }
            }
    }
}

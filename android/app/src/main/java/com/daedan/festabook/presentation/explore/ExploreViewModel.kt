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
    private val _searchState = MutableLiveData<SearchUiState<List<University>>>()
    val searchState: LiveData<SearchUiState<List<University>>> = _searchState

    private val _navigateToMain = SingleLiveData<University?>()
    val navigateToMain: LiveData<University?> = _navigateToMain

    private val _selectedUniversity = MutableLiveData<University>()
    val selectedUniversity: LiveData<University> = _selectedUniversity

    fun onUniversitySelected(university: University) {
        _selectedUniversity.value = university
        _searchState.value = SearchUiState.Success(listOf(university))
    }

    fun onTextInputChanged() {
        when (searchState.value) {
            is SearchUiState.Success, is SearchUiState.Error -> {
                _searchState.value = SearchUiState.Idle()
            }

            else -> {}
        }
    }

    fun search(query: String) {
        if (query.isEmpty()) {
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
                    Timber.d(it, "검색 실패")
                    _searchState.value = SearchUiState.Error(it)
                }
        }
    }

    fun onNavigateIconClicked() {
        val selectedUniversity = _selectedUniversity.value

        if (selectedUniversity != null) {
            Timber.d("festivalId 로 화면 이동 - ${selectedUniversity.festivalId}")
            _navigateToMain.setValue(selectedUniversity)
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

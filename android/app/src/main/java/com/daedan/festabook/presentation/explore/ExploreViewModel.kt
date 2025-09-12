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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import timber.log.Timber

class ExploreViewModel(
    private val exploreRepository: ExploreRepository,
) : ViewModel() {
    private val searchQuery = MutableStateFlow("")

    private val _searchState = MutableLiveData<SearchUiState>()
    val searchState: LiveData<SearchUiState> = _searchState

    private val _navigateToMain = SingleLiveData<University?>()
    val navigateToMain: LiveData<University?> = _navigateToMain

    private var selectedUniversity: University? = null

    init {
        viewModelScope.launch {
            searchQuery
                .debounce(300L)
                .distinctUntilChanged()
                .collect { query ->
                    search(query)
                }
        }
    }

    fun onUniversitySelected(university: University) {
        selectedUniversity = university
        _searchState.value =
            SearchUiState.Success(
                universitiesFound = listOf(university),
//                selectedUniversity = university,
            )
    }

    fun onTextInputChanged(query: String) {
        searchQuery.value = query
    }

    fun search(query: String) {
        if (query.isEmpty()) {
            _searchState.value = SearchUiState.Idle
            return
        }

        _searchState.value = SearchUiState.Loading

        viewModelScope.launch {
            val result = exploreRepository.search(query)
            result
                .onSuccess { universitiesFound ->
                    Timber.d("검색 성공 - received: $universitiesFound")
                    _searchState.value = SearchUiState.Success(universitiesFound = universitiesFound)
                }.onFailure {
                    Timber.d(it, "검색 실패")
                    _searchState.value = SearchUiState.Error(it)
                }
        }
    }

    fun navigateToMainScreen() {
        val selectedUniversity = selectedUniversity

        if (selectedUniversity != null) {
            Timber.d("festivalId 로 화면 이동 - ${selectedUniversity.festivalId}")
            _navigateToMain.setValue(selectedUniversity)
            exploreRepository.saveFestivalId(selectedUniversity.festivalId)
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

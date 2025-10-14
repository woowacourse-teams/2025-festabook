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
import com.daedan.festabook.domain.repository.ExploreRepository
import com.daedan.festabook.presentation.common.SingleLiveData
import com.daedan.festabook.presentation.explore.model.SearchResultUiModel
import com.daedan.festabook.presentation.explore.model.toUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
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

    private val _navigateToMain = SingleLiveData<SearchResultUiModel?>()
    val navigateToMain: LiveData<SearchResultUiModel?> = _navigateToMain

    private val _hasFestivalId = MutableLiveData<Boolean>(false)
    val hasFestivalId: LiveData<Boolean> = _hasFestivalId

    private var selectedUniversity: SearchResultUiModel? = null

    init {
        checkFestivalId()

        viewModelScope.launch {
            searchQuery
                .debounce(300L)
                .distinctUntilChanged()
                .collectLatest { query ->
// 현재는 검색어가 없을 시, 전체 리스트를 보여주기 위해 아래의 코드를 주석처리해두었음.
//                    if (query.isEmpty()) {
//                        _searchState.value = SearchUiState.Idle
//                        return@collectLatest
//                    }

                    _searchState.value = SearchUiState.Loading

                    val result = exploreRepository.search(query)
                    result
                        .onSuccess { universitiesFound ->
                            Timber.d("검색 성공 - received: $universitiesFound")
                            _searchState.value =
                                SearchUiState.Success(universitiesFound = universitiesFound.map { it.toUiModel() })
                        }.onFailure {
                            Timber.d(it, "검색 실패")
                            _searchState.value = SearchUiState.Error(it)
                        }
                }
        }
    }

    fun checkFestivalId() {
        val festivalId = exploreRepository.getFestivalId()
        Timber.d("festival ID : $festivalId")
        if (festivalId != null) {
            _hasFestivalId.value = true
        }
    }

    fun onUniversitySelected(university: SearchResultUiModel) {
        selectedUniversity = university
        _searchState.value =
            SearchUiState.Success(
                universitiesFound = listOf(university),
//                selectedUniversity = university,
            )
        navigateToMainScreen()
    }

    fun onTextInputChanged(query: String) {
        searchQuery.value = query
    }

    private fun navigateToMainScreen() {
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

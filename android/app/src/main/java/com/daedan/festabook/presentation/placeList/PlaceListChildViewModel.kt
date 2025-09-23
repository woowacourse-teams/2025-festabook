package com.daedan.festabook.presentation.placeList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.daedan.festabook.FestaBookApp
import com.daedan.festabook.domain.model.PlaceCategory
import com.daedan.festabook.domain.repository.PlaceListRepository
import com.daedan.festabook.presentation.placeList.model.PlaceCategoryUiModel
import com.daedan.festabook.presentation.placeList.model.PlaceListUiState
import com.daedan.festabook.presentation.placeList.model.PlaceUiModel
import com.daedan.festabook.presentation.placeList.model.toUiModel
import kotlinx.coroutines.launch

class PlaceListChildViewModel(
    private val placeListRepository: PlaceListRepository,
) : ViewModel() {
    private var cachedPlaces = listOf<PlaceUiModel>()
    private var cachedPlaceByTimeTag: List<PlaceUiModel> = emptyList()

    private val _places: MutableLiveData<PlaceListUiState<List<PlaceUiModel>>> =
        MutableLiveData(PlaceListUiState.Loading())
    val places: LiveData<PlaceListUiState<List<PlaceUiModel>>> = _places

    init {
        loadAllPlaces()
    }

    fun updatePlacesByCategories(category: List<PlaceCategoryUiModel>) {
        val secondaryCategories =
            PlaceCategory.SECONDARY_CATEGORIES.map {
                it.toUiModel()
            }
        val primaryCategoriesSelected = category.any { it !in secondaryCategories }

        if (!primaryCategoriesSelected) {
            clearPlacesFilter()
            return
        }
        val filteredPlaces =
            cachedPlaceByTimeTag
                .filter { place ->
                    place.category in category
                }
        _places.value = PlaceListUiState.Success(filteredPlaces)
    }

    private fun filterPlacesByTimeTag(timeTagId: Long): List<PlaceUiModel> {
        val filteredPlaces =
            cachedPlaces.filter { place ->
                place.timeTagId.contains(timeTagId)
            }
        return filteredPlaces
    }

    fun updatePlacesByTimeTag(timeTagId: Long) {
        val filteredPlaces = filterPlacesByTimeTag(timeTagId)

        _places.value = PlaceListUiState.Success(filteredPlaces)
        cachedPlaceByTimeTag = filteredPlaces
    }

    fun clearPlacesFilter() {
        _places.value = PlaceListUiState.Success(cachedPlaceByTimeTag)
    }

    fun setPlacesStateComplete() {
        _places.value = PlaceListUiState.Complete()
    }

    private fun loadAllPlaces() {
        viewModelScope.launch {
            val result = placeListRepository.getPlaces()
            result
                .onSuccess { places ->
                    val placeUiModels = places.map { it.toUiModel() }
                    cachedPlaces = placeUiModels
                }.onFailure {
                    _places.value = PlaceListUiState.Error(it)
                }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val placeListRepository =
                        (this[APPLICATION_KEY] as FestaBookApp).appContainer.placeListRepository
                    PlaceListChildViewModel(placeListRepository)
                }
            }
    }
}

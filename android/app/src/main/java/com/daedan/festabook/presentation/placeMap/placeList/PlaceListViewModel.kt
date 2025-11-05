package com.daedan.festabook.presentation.placeMap.placeList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daedan.festabook.di.viewmodel.ViewModelKey
import com.daedan.festabook.domain.model.PlaceCategory
import com.daedan.festabook.domain.model.TimeTag
import com.daedan.festabook.domain.repository.PlaceListRepository
import com.daedan.festabook.presentation.placeMap.model.PlaceCategoryUiModel
import com.daedan.festabook.presentation.placeMap.model.PlaceListUiState
import com.daedan.festabook.presentation.placeMap.model.PlaceUiModel
import com.daedan.festabook.presentation.placeMap.model.toUiModel
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.launch

@ContributesIntoMap(AppScope::class)
@ViewModelKey(PlaceListViewModel::class)
class PlaceListViewModel @Inject constructor(
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
        val filteredPlaces =
            if (timeTagId == TimeTag.EMTPY_TIME_TAG_ID) {
                cachedPlaces
            } else {
                filterPlacesByTimeTag(timeTagId)
            }

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
                    _places.value = PlaceListUiState.PlaceLoaded(placeUiModels)
                }.onFailure {
                    _places.value = PlaceListUiState.Error(it)
                }
        }
    }
}

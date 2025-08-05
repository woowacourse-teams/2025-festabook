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
import com.daedan.festabook.presentation.placeList.model.InitialMapSettingUiModel
import com.daedan.festabook.presentation.placeList.model.PlaceCategoryUiModel
import com.daedan.festabook.presentation.placeList.model.PlaceCoordinateUiModel
import com.daedan.festabook.presentation.placeList.model.PlaceListUiState
import com.daedan.festabook.presentation.placeList.model.PlaceUiModel
import com.daedan.festabook.presentation.placeList.model.toUiModel
import kotlinx.coroutines.launch

class PlaceListViewModel(
    private val placeListRepository: PlaceListRepository,
) : ViewModel() {
    private var _cachedPlaces = listOf<PlaceUiModel>()

    private val _places: MutableLiveData<PlaceListUiState<List<PlaceUiModel>>> =
        MutableLiveData(PlaceListUiState.Loading())
    val places: LiveData<PlaceListUiState<List<PlaceUiModel>>> = _places

    private val _initialMapSetting: MutableLiveData<PlaceListUiState<InitialMapSettingUiModel>> =
        MutableLiveData()
    val initialMapSetting: LiveData<PlaceListUiState<InitialMapSettingUiModel>> = _initialMapSetting

    private val _placeGeographies: MutableLiveData<PlaceListUiState<List<PlaceCoordinateUiModel>>> =
        MutableLiveData()
    val placeGeographies: LiveData<PlaceListUiState<List<PlaceCoordinateUiModel>>> =
        _placeGeographies

    init {
        loadAllPlaces()
        loadOrganizationGeography()
    }

    fun filterPlaces(category: List<PlaceCategoryUiModel>) {
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
            _cachedPlaces.filter { place ->
                place.category in category
            }

        _places.value = PlaceListUiState.Success(filteredPlaces)
    }

    fun clearPlacesFilter() {
        _places.value = PlaceListUiState.Success(_cachedPlaces)
    }

    private fun loadAllPlaces() {
        viewModelScope.launch {
            val result = placeListRepository.getPlaces()
            result
                .onSuccess { places ->
                    val placeUiModels = places.map { it.toUiModel() }
                    _places.value =
                        PlaceListUiState.Success(
                            placeUiModels,
                        )
                    _cachedPlaces = placeUiModels
                }.onFailure {
                    _places.value = PlaceListUiState.Error(it)
                }
        }
    }

    private fun loadOrganizationGeography() {
        viewModelScope.launch {
            launch {
                placeListRepository.getOrganizationGeography().onSuccess {
                    _initialMapSetting.value = PlaceListUiState.Success(it.toUiModel())
                }
            }

            launch {
                placeListRepository
                    .getPlaceGeographies()
                    .onSuccess {
                        _placeGeographies.value =
                            PlaceListUiState.Success(it.map { it.toUiModel() })
                    }.onFailure {
                        _placeGeographies.value = PlaceListUiState.Error(it)
                    }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val placeListRepository =
                        (this[APPLICATION_KEY] as FestaBookApp).appContainer.placeListRepository
                    PlaceListViewModel(placeListRepository)
                }
            }
    }
}

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
import com.daedan.festabook.domain.repository.PlaceDetailRepository
import com.daedan.festabook.domain.repository.PlaceListRepository
import com.daedan.festabook.presentation.common.SingleLiveData
import com.daedan.festabook.presentation.placeDetail.model.PlaceDetailUiModel
import com.daedan.festabook.presentation.placeDetail.model.toUiModel
import com.daedan.festabook.presentation.placeList.model.InitialMapSettingUiModel
import com.daedan.festabook.presentation.placeList.model.PlaceCategoryUiModel
import com.daedan.festabook.presentation.placeList.model.PlaceCoordinateUiModel
import com.daedan.festabook.presentation.placeList.model.PlaceListUiState
import com.daedan.festabook.presentation.placeList.model.PlaceUiModel
import com.daedan.festabook.presentation.placeList.model.toUiModel
import kotlinx.coroutines.launch

class PlaceListViewModel(
    private val placeListRepository: PlaceListRepository,
    private val placeDetailRepository: PlaceDetailRepository,
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

    private val _selectedPlace: MutableLiveData<PlaceDetailUiModel?> = MutableLiveData()
    val selectedPlace: LiveData<PlaceDetailUiModel?> = _selectedPlace

    private val _navigateToDetail = SingleLiveData<PlaceDetailUiModel>()
    val navigateToDetail: LiveData<PlaceDetailUiModel> = _navigateToDetail

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

    fun selectPlace(
        placeId: Long,
        category: PlaceCategoryUiModel,
    ) {
        if (category in PlaceCategoryUiModel.SECONDARY_CATEGORIES) {
            return
        }

        viewModelScope.launch {
            placeDetailRepository
                .getPlaceDetail(placeId = placeId)
                .onSuccess {
                    _selectedPlace.value = it.toUiModel()
                }
        }
    }

    fun unselectPlace() {
        _selectedPlace.value = null
    }

    fun onExpandedStateReached() {
        val currentPlace = _selectedPlace.value
        if (currentPlace != null) {
            _navigateToDetail.setValue(currentPlace)
        }
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
            placeListRepository.getOrganizationGeography().onSuccess {
                _initialMapSetting.value = PlaceListUiState.Success(it.toUiModel())
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
                    val placeDetailRepository =
                        (this[APPLICATION_KEY] as FestaBookApp).appContainer.placeDetailRepository
                    val placeListRepository =
                        (this[APPLICATION_KEY] as FestaBookApp).appContainer.placeListRepository
                    PlaceListViewModel(placeListRepository, placeDetailRepository)
                }
            }
    }
}

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
import com.daedan.festabook.domain.repository.PlaceListRepository
import com.daedan.festabook.presentation.placeList.model.InitialMapSettingUiModel
import com.daedan.festabook.presentation.placeList.model.PlaceListUiState
import com.daedan.festabook.presentation.placeList.model.PlaceUiModel
import com.daedan.festabook.presentation.placeList.model.toUiModel
import kotlinx.coroutines.launch

class PlaceListViewModel(
    private val placeListRepository: PlaceListRepository,
) : ViewModel() {
    private val _selectedPlace: MutableLiveData<PlaceUiModel> = MutableLiveData()
    val selectedPlace: LiveData<PlaceUiModel> = _selectedPlace

    private val _places: MutableLiveData<PlaceListUiState<List<PlaceUiModel>>> =
        MutableLiveData(PlaceListUiState.Loading())
    val places: LiveData<PlaceListUiState<List<PlaceUiModel>>> = _places

    private val _initialMapSetting: MutableLiveData<PlaceListUiState<InitialMapSettingUiModel>> =
        MutableLiveData()
    val initialMapSetting: LiveData<PlaceListUiState<InitialMapSettingUiModel>> = _initialMapSetting

    init {
        loadAllPlaces()
        loadOrganizationGeography()
    }

    fun setPlace(place: PlaceUiModel) {
        _selectedPlace.value = place
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
                }.onFailure {}
        }
    }

    fun loadOrganizationGeography() {
        viewModelScope.launch {
            val organizationGeography = placeListRepository.getOrganizationGeography().getOrNull()
            val placeGeographies = placeListRepository.getPlaceGeographies().getOrNull()

            if (organizationGeography == null || placeGeographies == null) {
                return@launch
            }
            val initialMapSetting = organizationGeography.toUiModel()
            val placeCoordinates = placeGeographies.map { it.toUiModel() }
            _initialMapSetting.value =
                PlaceListUiState.Success(
                    initialMapSetting.copy(placeCoordinates = placeCoordinates),
                )
        }
    }

    fun updateBookmark(place: PlaceUiModel) {
        val currentUiState = _places.value
        if (currentUiState is PlaceListUiState.Success<List<PlaceUiModel>>) {
            val currentPlaces = currentUiState.value
            val updatedPlaces =
                currentPlaces.map {
                    if (it.id == place.id) {
                        it.copy(isBookmarked = !it.isBookmarked)
                    } else {
                        it
                    }
                }
            _places.value = PlaceListUiState.Success(updatedPlaces)
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

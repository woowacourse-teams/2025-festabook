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
import com.daedan.festabook.presentation.placeList.model.PlaceUiModel
import com.daedan.festabook.presentation.placeList.model.toUiModel
import kotlinx.coroutines.launch

class PlaceListViewModel(
    private val placeListRepository: PlaceListRepository,
) : ViewModel() {
    private val _selectedPlace: MutableLiveData<PlaceUiModel> = MutableLiveData()
    val selectedPlace: LiveData<PlaceUiModel> = _selectedPlace

    private val _places: MutableLiveData<List<PlaceUiModel>> = MutableLiveData(emptyList())
    val places: LiveData<List<PlaceUiModel>> = _places

    private val _initialMapSetting: MutableLiveData<InitialMapSettingUiModel> = MutableLiveData()
    val initialMapSetting: LiveData<InitialMapSettingUiModel> = _initialMapSetting

    init {
        loadAllPlaces()
        loadPlaceGeography()
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
                    _places.value = placeUiModels
                }.onFailure {}
        }
    }

    fun loadPlaceGeography() {
        viewModelScope.launch {
            val result = placeListRepository.fetchPlaceGeography()
            result
                .onSuccess { placeGeography ->
                    _initialMapSetting.value = placeGeography.toUiModel()
                }.onFailure {}
        }
    }

    fun updateBookmark(place: PlaceUiModel) {
        _places.value =
            _places.value?.map {
                if (it.id == place.id) {
                    it.copy(isBookmarked = !it.isBookmarked)
                } else {
                    it
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

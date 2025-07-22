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
import com.daedan.festabook.presentation.placeList.model.PlaceUiModel
import com.daedan.festabook.presentation.placeList.model.toUiModel
import kotlinx.coroutines.launch

class PlaceListViewModel(
    private val placeListRepository: PlaceListRepository,
) : ViewModel() {
    init {
        loadAllPlaces()
    }

    private val _selectedPlace: MutableLiveData<PlaceUiModel> = MutableLiveData()
    val selectedPlace: LiveData<PlaceUiModel> = _selectedPlace

    private val _places: MutableLiveData<List<PlaceUiModel>> = MutableLiveData()
    val places: LiveData<List<PlaceUiModel>> = _places

    fun setPlace(place: PlaceUiModel) {
        _selectedPlace.value = place
    }

    fun loadAllPlaces() {
        viewModelScope.launch {
            val result = placeListRepository.fetchPlaces()
            result
                .onSuccess { places ->
                    val placeUiModels = places.map { it.toUiModel() }
                    _places.value = placeUiModels
                }.onFailure {}
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

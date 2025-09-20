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
import com.daedan.festabook.domain.repository.PlaceDetailRepository
import com.daedan.festabook.domain.repository.PlaceListRepository
import com.daedan.festabook.presentation.common.Event
import com.daedan.festabook.presentation.common.SingleLiveData
import com.daedan.festabook.presentation.placeDetail.model.PlaceDetailUiModel
import com.daedan.festabook.presentation.placeDetail.model.toUiModel
import com.daedan.festabook.presentation.placeList.model.InitialMapSettingUiModel
import com.daedan.festabook.presentation.placeList.model.PlaceCategoryUiModel
import com.daedan.festabook.presentation.placeList.model.PlaceCoordinateUiModel
import com.daedan.festabook.presentation.placeList.model.PlaceListUiState
import com.daedan.festabook.presentation.placeList.model.SelectedPlaceUiState
import com.daedan.festabook.presentation.placeList.model.toUiModel
import kotlinx.coroutines.launch

class PlaceListViewModel(
    private val placeListRepository: PlaceListRepository,
    private val placeDetailRepository: PlaceDetailRepository,
) : ViewModel() {
    private val _initialMapSetting: MutableLiveData<PlaceListUiState<InitialMapSettingUiModel>> =
        MutableLiveData()
    val initialMapSetting: LiveData<PlaceListUiState<InitialMapSettingUiModel>> = _initialMapSetting

    private val _placeGeographies: MutableLiveData<PlaceListUiState<List<PlaceCoordinateUiModel>>> =
        MutableLiveData()
    val placeGeographies: LiveData<PlaceListUiState<List<PlaceCoordinateUiModel>>> =
        _placeGeographies

    private val _selectedPlace: MutableLiveData<SelectedPlaceUiState> = MutableLiveData()
    val selectedPlace: LiveData<SelectedPlaceUiState> = _selectedPlace

    private val _navigateToDetail = SingleLiveData<PlaceDetailUiModel>()
    val navigateToDetail: LiveData<PlaceDetailUiModel> = _navigateToDetail

    private val _isExceededMaxLength: MutableLiveData<Boolean> = MutableLiveData()
    val isExceededMaxLength: LiveData<Boolean> = _isExceededMaxLength

    private val _backToInitialPositionClicked: MutableLiveData<Event<Unit>> = MutableLiveData()
    val backToInitialPositionClicked: LiveData<Event<Unit>> = _backToInitialPositionClicked

    private val _selectedCategories: MutableLiveData<List<PlaceCategoryUiModel>> = MutableLiveData()
    val selectedCategories: LiveData<List<PlaceCategoryUiModel>> = _selectedCategories

    private val _onMapViewClick: MutableLiveData<Event<Unit>> = MutableLiveData()
    val onMapViewClick: LiveData<Event<Unit>> = _onMapViewClick

    init {
        loadOrganizationGeography()
    }

    fun selectPlace(placeId: Long) {
        viewModelScope.launch {
            _selectedPlace.value = SelectedPlaceUiState.Loading
            placeDetailRepository
                .getPlaceDetail(placeId = placeId)
                .onSuccess {
                    _selectedPlace.value = SelectedPlaceUiState.Success(it.toUiModel())
                }.onFailure {
                    _selectedPlace.value = SelectedPlaceUiState.Error(it)
                }
        }
    }

    fun unselectPlace() {
        _selectedPlace.value = SelectedPlaceUiState.Empty
    }

    fun onExpandedStateReached() {
        val currentPlace = _selectedPlace.value.let { it as? SelectedPlaceUiState.Success }?.value
        if (currentPlace != null) {
            _navigateToDetail.setValue(currentPlace)
        }
    }

    fun onBackToInitialPositionClicked() {
        _backToInitialPositionClicked.value = Event(Unit)
    }

    fun setIsExceededMaxLength(isExceededMaxLength: Boolean) {
        _isExceededMaxLength.value = isExceededMaxLength
    }

    fun setSelectedCategories(categories: List<PlaceCategoryUiModel>) {
        _selectedCategories.value = categories
    }

    fun onMapViewClick() {
        _onMapViewClick.value = Event(Unit)
    }

    private fun loadOrganizationGeography() {
        viewModelScope.launch {
            placeListRepository.getOrganizationGeography().onSuccess { organizationGeography ->
                _initialMapSetting.value = PlaceListUiState.Success(organizationGeography.toUiModel())
            }

            launch {
                placeListRepository
                    .getPlaceGeographies()
                    .onSuccess { placeGeographies ->
                        _placeGeographies.value =
                            PlaceListUiState.Success(placeGeographies.map { it.toUiModel() })
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

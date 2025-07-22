package com.daedan.festabook.presentation.placeDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.daedan.festabook.FestaBookApp
import com.daedan.festabook.domain.repository.PlaceDetailRepository
import com.daedan.festabook.presentation.placeDetail.model.PlaceDetailUiModel
import com.daedan.festabook.presentation.placeDetail.model.toUiModel
import com.daedan.festabook.presentation.placeList.model.PlaceUiModel
import kotlinx.coroutines.launch

class PlaceDetailViewModel(
    private val placeDetailRepository: PlaceDetailRepository,
    private val place: PlaceUiModel,
) : ViewModel() {
    private val _placeDetail = MutableLiveData<PlaceDetailUiModel>()
    val placeDetail: LiveData<PlaceDetailUiModel> = _placeDetail

    init {
        loadPlaceDetail()
    }

    fun loadPlaceDetail() {
        viewModelScope.launch {
            val result = placeDetailRepository.fetchPlaceDetail(place.id)
            result
                .onSuccess { placeDetail ->
                    _placeDetail.value = placeDetail.toUiModel()
                }.onFailure { }
        }
    }

    companion object {
        fun factory(place: PlaceUiModel) =
            viewModelFactory {
                initializer {
                    val placeListRepository =
                        (this[APPLICATION_KEY] as FestaBookApp).appContainer.placeDetailRepository
                    PlaceDetailViewModel(placeListRepository, place)
                }
            }
    }
}

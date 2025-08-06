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
import com.daedan.festabook.presentation.placeDetail.model.PlaceDetailUiState
import com.daedan.festabook.presentation.placeDetail.model.toUiModel
import com.daedan.festabook.presentation.placeList.model.PlaceUiModel
import kotlinx.coroutines.launch

class PlaceDetailViewModel(
    private val placeDetailRepository: PlaceDetailRepository,
    private val place: PlaceUiModel? = null,
    private val receivedPlaceDetail: PlaceDetailUiModel? = null,
) : ViewModel() {
    private val _placeDetail =
        MutableLiveData<PlaceDetailUiState>(
            PlaceDetailUiState.Loading,
        )
    val placeDetail: LiveData<PlaceDetailUiState> = _placeDetail

    init {
        if (receivedPlaceDetail != null) {
            _placeDetail.value = PlaceDetailUiState.Success(receivedPlaceDetail)
        } else if (place != null) {
            loadPlaceDetail(place.id)
        }
    }

    private fun loadPlaceDetail(placeId: Long) {
        viewModelScope.launch {
            val result = placeDetailRepository.getPlaceDetail(placeId)
            result
                .onSuccess { placeDetail ->
                    _placeDetail.value =
                        PlaceDetailUiState.Success(
                            placeDetail.toUiModel(),
                        )
                }.onFailure {
                    _placeDetail.value = PlaceDetailUiState.Error(it)
                }
        }
    }

    companion object {
        fun factory(place: PlaceUiModel) =
            viewModelFactory {
                initializer {
                    val placeDetailRepository =
                        (this[APPLICATION_KEY] as FestaBookApp).appContainer.placeDetailRepository
                    PlaceDetailViewModel(placeDetailRepository, place = place)
                }
            }

        fun factory(placeDetail: PlaceDetailUiModel) =
            viewModelFactory {
                initializer {
                    val placeDetailRepository =
                        (this[APPLICATION_KEY] as FestaBookApp).appContainer.placeDetailRepository
                    PlaceDetailViewModel(placeDetailRepository, receivedPlaceDetail = placeDetail)
                }
            }
    }
}

package com.daedan.festabook.presentation.placeDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.daedan.festabook.domain.repository.PlaceDetailRepository
import com.daedan.festabook.presentation.news.notice.model.NoticeUiModel
import com.daedan.festabook.presentation.placeDetail.model.PlaceDetailUiModel
import com.daedan.festabook.presentation.placeDetail.model.PlaceDetailUiState
import com.daedan.festabook.presentation.placeDetail.model.toUiModel
import com.daedan.festabook.presentation.placeMap.model.PlaceUiModel
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.launch

class PlaceDetailViewModel @AssistedInject constructor(
    private val placeDetailRepository: PlaceDetailRepository,
    @Assisted private val place: PlaceUiModel?,
    @Assisted private val receivedPlaceDetail: PlaceDetailUiModel?,
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun create(
            place: PlaceUiModel?,
            receivedPlaceDetail: PlaceDetailUiModel?,
        ): PlaceDetailViewModel
    }

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

    fun loadPlaceDetail(placeId: Long) {
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

    fun toggleNoticeExpanded(notice: NoticeUiModel) {
        val currentState = _placeDetail.value ?: return
        if (currentState !is PlaceDetailUiState.Success) return
        _placeDetail.value =
            currentState.copy(
                placeDetail =
                    currentState.placeDetail.copy(
                        notices =
                            currentState.placeDetail.notices.map {
                                if (notice.id == it.id) {
                                    it.copy(isExpanded = !it.isExpanded)
                                } else {
                                    it
                                }
                            },
                    ),
            )
    }

    companion object {
        fun factory(
            factory: Factory,
            place: PlaceUiModel?,
            receivedPlaceDetail: PlaceDetailUiModel?,
        ) =
            viewModelFactory {
                initializer {
                    factory.create(place, receivedPlaceDetail)
                }
            }
    }
}

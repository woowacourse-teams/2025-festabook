package com.daedan.festabook.presentation.placeList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.daedan.festabook.presentation.common.SingleLiveData
import com.daedan.festabook.presentation.placeList.model.PlaceUiModel

class PlaceListViewModel : ViewModel() {
    private val _event: SingleLiveData<PlaceListEvent> = SingleLiveData(PlaceListEvent.RUNNING)
    val event: SingleLiveData<PlaceListEvent> = _event

    private val _place: MutableLiveData<PlaceUiModel> = MutableLiveData()
    val place: LiveData<PlaceUiModel> = _place

    fun publishClickEvent() {
        _event.setValue(PlaceListEvent.PLACE_CLICKED)
    }

    fun setPlace(place: PlaceUiModel) {
        _place.value = place
    }
}

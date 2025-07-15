package com.daedan.festabook.presentation.placeList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.daedan.festabook.presentation.common.SingleLiveData
import com.daedan.festabook.presentation.placeList.uimodel.Place
import com.daedan.festabook.presentation.placeList.uimodel.PlaceListEvent

class PlaceListViewModel : ViewModel() {
    private val _event: SingleLiveData<PlaceListEvent> = SingleLiveData(PlaceListEvent.RUNNING)
    val event: SingleLiveData<PlaceListEvent> = _event

    private val _place: MutableLiveData<Place> = MutableLiveData()
    val place: LiveData<Place> = _place

    fun publishClickEvent() {
        _event.postValue(PlaceListEvent.PLACE_CLICKED)
    }

    fun setPlace(place: Place) {
        _place.value = place
    }
}

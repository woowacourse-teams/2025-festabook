package com.daedan.festabook.presentation.placeList

import androidx.lifecycle.ViewModel
import com.daedan.festabook.presentation.common.SingleLiveData
import com.daedan.festabook.presentation.placeList.uimodel.PlaceListEvent

class PlaceListViewModel : ViewModel() {
    private val _event: SingleLiveData<PlaceListEvent> = SingleLiveData(PlaceListEvent.RUNNING)
    val event: SingleLiveData<PlaceListEvent> = _event

    fun publishClickEvent() {
        _event.postValue(PlaceListEvent.PLACE_CLICKED)
    }
}

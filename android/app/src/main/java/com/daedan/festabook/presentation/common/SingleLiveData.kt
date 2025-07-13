package com.daedan.festabook.presentation.common

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData

class SingleLiveData<T>(
    value: T,
) {
    private val liveData = MutableLiveData<Event<T>>()

    init {
        liveData.value = Event(value)
    }

    fun setValue(value: T) {
        liveData.value = Event(value)
    }

    fun postValue(value: T) {
        liveData.postValue(Event(value))
    }

    fun getValue() = liveData.value?.peekContent()

    fun observe(
        owner: LifecycleOwner,
        onResult: (T) -> Unit,
    ) {
        liveData.observe(owner) { it.getContentIfNotHandled()?.let(onResult) }
    }
}

package com.daedan.festabook.presentation.common

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

class SingleLiveData<T> : MutableLiveData<T>() {
    private val liveData = MutableLiveData<Event<T>>()

    override fun setValue(value: T) {
        liveData.value = Event(value)
    }

    override fun postValue(value: T) {
        liveData.postValue(Event(value))
    }

    override fun getValue() = liveData.value?.peekContent()

    override fun observe(
        owner: LifecycleOwner,
        observer: Observer<in T>,
    ) {
        liveData.observe(owner) { it.getContentIfNotHandled()?.let(observer::onChanged) }
    }
}

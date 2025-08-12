package com.daedan.festabook.presentation.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.daedan.festabook.FestaBookApp
import com.daedan.festabook.domain.repository.FestivalNotificationRepository
import com.daedan.festabook.presentation.common.Event
import kotlinx.coroutines.launch

class SettingViewModel(
    private val festivalNotificationRepository: FestivalNotificationRepository,
) : ViewModel() {
    private val _allowClickEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val allowClickEvent: LiveData<Event<Unit>> get() = _allowClickEvent

    var isAllowed = festivalNotificationRepository.getFestivalNotificationIsAllow()
        private set

    fun notificationAllowClick() {
        updateNotificationIsAllowed(!isAllowed)
        _allowClickEvent.value = Event(Unit)
        saveNotificationIsAllowed(isAllowed)
        if (isAllowed) saveNotificationId() else deleteNotificationId()
    }

    fun saveNotificationIsAllowed(isAllowed: Boolean) {
        festivalNotificationRepository.setFestivalNotificationIsAllow(isAllowed)
    }

    fun updateNotificationIsAllowed(allowed: Boolean) {
        isAllowed = allowed
    }

    private fun saveNotificationId() {
        viewModelScope.launch {
            val result =
                festivalNotificationRepository.saveFestivalNotification()

            result.onFailure {
            }
        }
    }

    private fun deleteNotificationId() {
        viewModelScope.launch {
            val result =
                festivalNotificationRepository.deleteFestivalNotification()

            result.onFailure {
            }
        }
    }

    companion object {
        fun factory(): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val festivalNotificationRepository =
                        (this[APPLICATION_KEY] as FestaBookApp).appContainer.festivalNotificationRepository
                    SettingViewModel(festivalNotificationRepository)
                }
            }
    }
}

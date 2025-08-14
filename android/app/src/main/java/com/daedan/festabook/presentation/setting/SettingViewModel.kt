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
import timber.log.Timber

class SettingViewModel(
    private val festivalNotificationRepository: FestivalNotificationRepository,
) : ViewModel() {
    private val _allowClickEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val allowClickEvent: LiveData<Event<Unit>> get() = _allowClickEvent

    var isAllowed = festivalNotificationRepository.getFestivalNotificationIsAllow()
        private set

    private val _error: MutableLiveData<Event<Throwable>> = MutableLiveData()
    val error: LiveData<Event<Throwable>> get() = _error

    fun notificationAllowClick() {
        updateNotificationIsAllowed(!isAllowed)
        _allowClickEvent.value = Event(Unit)
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

            result
                .onSuccess {
                    saveNotificationIsAllowed(isAllowed)
                }.onFailure {
                    _error.value = Event(it)
                    Timber.e(it, "${::SettingViewModel.name} NotificationId 저장 실패")
                }
        }
    }

    private fun deleteNotificationId() {
        viewModelScope.launch {
            val result =
                festivalNotificationRepository.deleteFestivalNotification()

            result
                .onSuccess {
                    saveNotificationIsAllowed(isAllowed)
                }.onFailure {
                    _error.value = Event(it)
                    Timber.e(it, "${::SettingViewModel.name} NotificationId 삭제 실패")
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

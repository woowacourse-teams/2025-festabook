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
import com.daedan.festabook.presentation.common.SingleLiveData
import kotlinx.coroutines.launch
import timber.log.Timber

class SettingViewModel(
    private val festivalNotificationRepository: FestivalNotificationRepository,
) : ViewModel() {
    private val _allowClickEvent: SingleLiveData<Unit> = SingleLiveData()
    val allowClickEvent: LiveData<Unit> get() = _allowClickEvent

    var isAllowed = festivalNotificationRepository.getFestivalNotificationIsAllow()
        private set

    private val _error: SingleLiveData<Throwable> = SingleLiveData()
    val error: LiveData<Throwable> get() = _error

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun notificationAllowClick() {
        updateNotificationIsAllowed(!isAllowed)
        _allowClickEvent.setValue(Unit)
        Timber.d("$isAllowed")
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
                    _error.setValue(it)
                    Timber.e(it, "${::SettingViewModel.javaClass.simpleName} NotificationId 저장 실패")
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
                    _error.setValue(it)
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

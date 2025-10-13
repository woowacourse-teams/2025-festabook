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
    private val _permissionCheckEvent: SingleLiveData<Unit> = SingleLiveData()
    val permissionCheckEvent: LiveData<Unit> get() = _permissionCheckEvent

    private val _isAllowed =
        MutableLiveData(
            festivalNotificationRepository.getFestivalNotificationIsAllow(),
        )
    val isAllowed: LiveData<Boolean> get() = _isAllowed

    private val _error: SingleLiveData<Throwable> = SingleLiveData()
    val error: LiveData<Throwable> get() = _error

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _success: SingleLiveData<Unit> = SingleLiveData()
    val success: LiveData<Unit> get() = _success

    fun notificationAllowClick() {
        if (_isAllowed.value == false) {
            _permissionCheckEvent.setValue(Unit)
        } else {
            deleteNotificationId()
        }
    }

    private fun saveNotificationIsAllowed(isAllowed: Boolean) {
        festivalNotificationRepository.setFestivalNotificationIsAllow(isAllowed)
    }

    private fun updateNotificationIsAllowed(allowed: Boolean) {
        _isAllowed.value = allowed
    }

    fun saveNotificationId() {
        if (_isLoading.value == true) return
        _isLoading.value = true

        // Optimistic UI 적용, 요청 실패 시 원복
        saveNotificationIsAllowed(true)
        updateNotificationIsAllowed(true)
        _success.setValue(Unit)

        viewModelScope.launch {
            val result =
                festivalNotificationRepository.saveFestivalNotification()

            result
                .onFailure {
                    _error.setValue(it)
                    saveNotificationIsAllowed(false)
                    updateNotificationIsAllowed(false)
                    Timber.e(it, "${this::class.java.simpleName} NotificationId 저장 실패")
                }.also {
                    _isLoading.value = false
                }
        }
    }

    private fun deleteNotificationId() {
        if (_isLoading.value == true) return
        _isLoading.value = true

        // Optimistic UI 적용, 요청 실패 시 원복
        saveNotificationIsAllowed(false)
        updateNotificationIsAllowed(false)

        viewModelScope.launch {
            val result =
                festivalNotificationRepository.deleteFestivalNotification()

            result
                .onFailure {
                    _error.setValue(it)
                    saveNotificationIsAllowed(true)
                    updateNotificationIsAllowed(true)
                    Timber.e(it, "${this::class.java.simpleName} NotificationId 삭제 실패")
                }.also {
                    _isLoading.value = false
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

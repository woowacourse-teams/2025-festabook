// presentation/main/MainViewModel.kt
package com.daedan.festabook.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.daedan.festabook.FestaBookApp
import com.daedan.festabook.data.datasource.local.AppPreferencesManager
import com.daedan.festabook.domain.repository.DeviceRepository
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel(
    private val deviceRepository: DeviceRepository,
    private val preferencesManager: AppPreferencesManager,
) : ViewModel() {
    fun registerDevice(
        uuid: String,
        fcmToken: String,
    ) {
        viewModelScope.launch {
            Timber.d("UUID: $uuid, FCM Token: $fcmToken")
            deviceRepository
                .registerDevice(uuid, fcmToken)
                .onSuccess { id ->
                    Timber.d("기기 등록 성공! 서버에서 받은 ID: $id")
                    preferencesManager.saveDeviceId(id)
                }.onFailure { throwable ->
                    Timber.e(throwable, "기기 등록 실패: ${throwable.message}")
                }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val app = this[APPLICATION_KEY] as FestaBookApp
                    val deviceRepository = app.appContainer.deviceRepository
                    val preferencesManager = app.appContainer.preferencesManager
                    MainViewModel(deviceRepository, preferencesManager)
                }
            }
    }
}

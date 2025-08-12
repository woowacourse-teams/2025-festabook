// presentation/main/MainViewModel.kt
package com.daedan.festabook.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.daedan.festabook.FestaBookApp
import com.daedan.festabook.domain.repository.DeviceRepository
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel(
    private val deviceRepository: DeviceRepository,
) : ViewModel() {
    fun registerDeviceAndFcmToken() {
        val uuid = deviceRepository.getUuid().orEmpty()
        val fcmToken = deviceRepository.getFcmToken()

        Timber.d("registerDeviceAndFcmToken() UUID: $uuid, FCM: $fcmToken")

        // UUID는 항상 있으므로, FCM 없으면 기다렸다가 호출
        if (uuid.isNotBlank() && fcmToken.isNullOrBlank()) {
            FirebaseMessaging
                .getInstance()
                .token
                .addOnSuccessListener { token ->
                    deviceRepository.saveFcmToken(token)
                    Timber.d("🪄 받은 FCM 토큰으로 디바이스 등록: $token")
                    registerDevice(uuid, token)
                }.addOnFailureListener {
                    Timber.w(it, "❌ FCM 토큰 받기 실패")
                }
        } else if (fcmToken != null) {
            if (uuid.isNotBlank() && fcmToken.isNotBlank()) {
                Timber.d("✅ 기존 값으로 디바이스 등록 실행")
            } else {
                Timber.w("❌ UUID 생성 전 or FCM 토큰 없음")
            }
        }
    }

    private fun registerDevice(
        uuid: String,
        fcmToken: String,
    ) {
        viewModelScope.launch {
            Timber.d("UUID: $uuid, FCM Token: $fcmToken")
            deviceRepository
                .registerDevice(uuid, fcmToken)
                .onSuccess { id ->
                    Timber.d("기기 등록 성공! 서버에서 받은 ID: $id")
                    deviceRepository.saveDeviceId(id)
                }.onFailure { throwable ->
                    Timber.e(throwable, "MainViewModel: 기기 등록 실패: ${throwable.message}")
                }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val app = this[APPLICATION_KEY] as FestaBookApp
                    val deviceRepository = app.appContainer.deviceRepository
                    MainViewModel(deviceRepository)
                }
            }
    }
}

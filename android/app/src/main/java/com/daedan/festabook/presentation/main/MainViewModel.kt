package com.daedan.festabook.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.daedan.festabook.FestaBookApp
import com.daedan.festabook.domain.repository.DeviceRepository
import com.daedan.festabook.presentation.common.Event
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel(
    private val deviceRepository: DeviceRepository,
) : ViewModel() {
    private val _backPressEvent: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val backPressEvent: LiveData<Event<Boolean>> get() = _backPressEvent

    private var lastBackPressedTime: Long = 0

    fun registerDeviceAndFcmToken() {
        val uuid = deviceRepository.getUuid().orEmpty()
        val fcmToken = deviceRepository.getFcmToken()
        Timber.d("registerDeviceAndFcmToken() UUID: $uuid, FCM: $fcmToken")

        when {
            uuid.isBlank() -> Timber.w("❌ UUID 생성 전")
            !fcmToken.isNullOrBlank() -> {
                Timber.d("✅ 기존 값으로 디바이스 등록 실행")
                registerDevice(uuid, fcmToken)
            }

            else -> {
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
            }
        }
    }

    fun onBackPressed() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastBackPressedTime < BACK_PRESS_INTERVAL) {
            _backPressEvent.value = Event(true)
        } else {
            lastBackPressedTime = currentTime
            _backPressEvent.value = Event(false)
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
        private const val BACK_PRESS_INTERVAL: Long = 2000L
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

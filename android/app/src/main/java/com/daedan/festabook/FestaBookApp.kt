package com.daedan.festabook

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.daedan.festabook.di.FestaBookAppGraph
import com.daedan.festabook.logging.FirebaseCrashlyticsTree
import com.daedan.festabook.service.NotificationHelper
import com.naver.maps.map.NaverMapSdk
import dev.zacsweers.metro.createGraphFactory
import timber.log.Timber
import java.util.UUID

class FestaBookApp : Application() {
    val festaBookGraph: FestaBookAppGraph by lazy {
        createGraphFactory<FestaBookAppGraph.Factory>().create(this)
    }

    override fun onCreate() {
        super.onCreate()
        setupTimber()
        setupNaverSdk()
        setupNotificationChannel()
        setLightTheme()
        setupDeviceIdentifiers()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Timber.w("FestabookApp: onLowMemory 호출됨")
    }

    private fun setupNotificationChannel() {
        runCatching {
            NotificationHelper.createNotificationChannel(this)
        }.onSuccess {
            Timber.d("알림 채널 설정 완료")
        }.onFailure { e ->
            Timber.e(e, "FestabookApp: 알림 채널 설정 실패 ${e.message}")
        }
    }

    private fun setupTimber() {
        plantDebugTimberTree()
        plantInfoTimberTree()

//        if (BuildConfig.DEBUG) {
//            plantDebugTimberTree()
//        } else {
//            plantInfoTimberTree()
//        }
        Timber.plant(FirebaseCrashlyticsTree())
    }

    private fun plantDebugTimberTree() {
        Timber.plant(
            object : Timber.DebugTree() {
                override fun createStackElementTag(element: StackTraceElement): String =
                    "${super.createStackElementTag(element)}:${element.lineNumber}"
            },
        )
    }

    private fun plantInfoTimberTree() {
        Timber.plant(festaBookGraph.firebaseAnalyticsTree)
    }

    private fun setupNaverSdk() {
        NaverMapSdk.getInstance(this).client =
            NaverMapSdk.NcpKeyClient(BuildConfig.NAVER_MAP_CLIENT_ID)
    }

    private fun setLightTheme() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    private fun setupDeviceIdentifiers() {
        if (festaBookGraph.deviceLocalDataSource
                .getUuid()
                .isNullOrEmpty()
        ) {
            val uuid = UUID.randomUUID().toString()
            festaBookGraph.deviceLocalDataSource.saveUuid(uuid)
            Timber.d("🆕 UUID 생성 및 저장: $uuid")
        }

        festaBookGraph.firebaseMessaging
            .token
            .addOnSuccessListener { token ->
                festaBookGraph.fcmDataSource.saveFcmToken(token)
                Timber.d("📡 FCM 토큰 저장: $token")
            }.addOnFailureListener {
                Timber.w(it, "❌ FCM 토큰 수신 실패")
            }
    }
}

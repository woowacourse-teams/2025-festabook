package com.daedan.festabook

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.daedan.festabook.data.datasource.local.DeviceLocalDataSource
import com.daedan.festabook.data.datasource.local.FcmDataSource
import com.daedan.festabook.di.FestaBookAppGraph
import com.daedan.festabook.logging.FirebaseCrashlyticsTree
import com.daedan.festabook.service.NotificationHelper
import com.daedan.festabook.util.FestabookGlobalExceptionHandler
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.google.firebase.messaging.FirebaseMessaging
import com.naver.maps.map.NaverMapSdk
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.createGraphFactory
import timber.log.Timber
import java.util.UUID

class FestaBookApp : Application() {
    val festaBookGraph: FestaBookAppGraph by lazy {
        createGraphFactory<FestaBookAppGraph.Factory>().create(this)
    }

    @Inject
    private lateinit var firebaseAnalyticsTree: Timber.Tree

    @Inject
    private lateinit var deviceLocalDataSource: DeviceLocalDataSource

    @Inject
    private lateinit var firebaseMessaging: FirebaseMessaging

    @Inject
    private lateinit var fcmDataSource: FcmDataSource

    override fun onCreate() {
        super.onCreate()
        setGlobalExceptionHandler()
        festaBookGraph.inject(this)
        setupTimber()
        setupNaverSdk()
        setupNotificationChannel()
        setLightTheme()
        sendUnsentReports()
        setupDeviceIdentifiers()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Timber.w("FestabookApp: onLowMemory 호출됨")
    }

    private fun sendUnsentReports() {
        Firebase.crashlytics.sendUnsentReports()
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
        Timber.plant(firebaseAnalyticsTree)
    }

    private fun setupNaverSdk() {
        NaverMapSdk.getInstance(this).client =
            NaverMapSdk.NcpKeyClient(BuildConfig.NAVER_MAP_CLIENT_ID)
    }

    private fun setLightTheme() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    private fun setupDeviceIdentifiers() {
        if (deviceLocalDataSource
                .getUuid()
                .isNullOrEmpty()
        ) {
            val uuid = UUID.randomUUID().toString()
            deviceLocalDataSource.saveUuid(uuid)
            Timber.d("🆕 UUID 생성 및 저장: $uuid")
        }

        firebaseMessaging
            .token
            .addOnSuccessListener { token ->
                fcmDataSource.saveFcmToken(token)
                Timber.d("📡 FCM 토큰 저장: $token")
            }.addOnFailureListener {
                Timber.w(it, "❌ FCM 토큰 수신 실패")
            }
    }

    private fun setGlobalExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(
            FestabookGlobalExceptionHandler(
                this,
            ),
        )
    }
}

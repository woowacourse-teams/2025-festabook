package com.daedan.festabook

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.daedan.festabook.data.service.api.ApiClient
import com.daedan.festabook.logging.FirebaseAnalyticsTree
import com.daedan.festabook.service.NotificationHelper
import com.daedan.festabook.util.CrashlyticsTree
import com.daedan.festabook.util.FestabookGlobalExceptionHandler
import com.google.firebase.analytics.FirebaseAnalytics
import com.naver.maps.map.NaverMapSdk
import timber.log.Timber

class FestaBookApp : Application() {
    val appContainer: AppContainer by lazy {
        AppContainer(this)
    }

    private val fireBaseAnalytics: FirebaseAnalytics by lazy {
        FirebaseAnalytics.getInstance(this)
    }

    override fun onCreate() {
        super.onCreate()
        initializeApiClient()
        setupTimber()
        setupNaverSdk()
        setupNotificationChannel()
        setLightTheme()
        setGlobalExceptionHandler()
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
        if (BuildConfig.DEBUG) {
            plantDebugTimberTree()
        } else {
            plantInfoTimberTree()
        }
        Timber.plant(CrashlyticsTree())
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
        Timber.plant(FirebaseAnalyticsTree(fireBaseAnalytics))
    }

    private fun setupNaverSdk() {
        NaverMapSdk.getInstance(this).client =
            NaverMapSdk.NcpKeyClient(BuildConfig.NAVER_MAP_CLIENT_ID)
    }

    private fun setLightTheme() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    private fun setGlobalExceptionHandler() {
        val defaultExceptionHandler: Thread.UncaughtExceptionHandler? =
            Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(
            FestabookGlobalExceptionHandler(
                this,
                defaultExceptionHandler,
            ),
        )
    }

    private fun initializeApiClient() {
        runCatching {
            ApiClient.initialize(this)
        }.onSuccess {
            Timber.d("API 클라이언트 초기화 완료")
        }.onFailure { e ->
            Timber.e(e, "FestabookApp: API 클라이언트 초기화 실패 ${e.message}")
        }
    }
}

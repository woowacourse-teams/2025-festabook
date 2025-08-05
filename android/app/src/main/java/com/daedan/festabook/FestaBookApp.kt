package com.daedan.festabook

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.daedan.festabook.service.NotificationHelper
import com.google.firebase.analytics.FirebaseAnalytics
import com.naver.maps.map.NaverMapSdk
import timber.log.Timber

class FestaBookApp : Application() {
    lateinit var appContainer: AppContainer
        private set

    private lateinit var fireBaseAnalytics: FirebaseAnalytics

    override fun onCreate() {
        super.onCreate()
        setupFirebaseAnalytics()
        setupTimber()
        setupNaverSdk()
        setupNotificationChannel()
        appContainer = AppContainer(this)
        setLightTheme()
    }

    private fun setupFirebaseAnalytics() {
        fireBaseAnalytics = FirebaseAnalytics.getInstance(this)
    }

    private fun setupNotificationChannel() {
        runCatching {
            NotificationHelper.createNotificationChannel(this)
        }.onSuccess {
            Timber.d("알림 채널 설정 완료")
        }.onFailure { e ->
            Timber.e(e, "알림 채널 설정 실패")
        }
    }

    private fun setupTimber() {
        if (BuildConfig.DEBUG) plantDebugTimberTree()
    }

    private fun plantDebugTimberTree() {
        Timber.plant(
            object : Timber.DebugTree() {
                override fun createStackElementTag(element: StackTraceElement): String =
                    "${super.createStackElementTag(element)}:${element.lineNumber}"
            },
        )
    }

    private fun setupNaverSdk() {
        NaverMapSdk.getInstance(this).client =
            NaverMapSdk.NcpKeyClient(BuildConfig.NAVER_MAP_CLIENT_ID)
    }

    private fun setLightTheme() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}

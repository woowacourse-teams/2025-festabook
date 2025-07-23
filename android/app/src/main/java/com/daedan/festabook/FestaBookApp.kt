package com.daedan.festabook

import android.app.Application
import com.daedan.festabook.service.NotificationHelper
import com.naver.maps.map.NaverMapSdk
import timber.log.Timber

class FestaBookApp : Application() {
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        setupTimber()
        setupNaverSdk()
        setupNotificationChannel()
        appContainer = AppContainer(this)
    }

    private fun setupNotificationChannel() {
        NotificationHelper.createNotificationChannel(this)
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
}

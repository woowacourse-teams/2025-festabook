package com.daedan.festabook

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.core.content.edit
import com.daedan.festabook.service.NotificationHelper
import java.util.UUID
import com.naver.maps.map.NaverMapSdk
import timber.log.Timber

class FestaBookApp : Application() {
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        setupTimber()
        appContainer = AppContainer()

        setupNotificationChannel()
        val deviceUuid = getOrCreateDeviceUuid(this)
    }

    private fun setupNotificationChannel() {
        NotificationHelper.createNotificationChannel(this)
    }

    private fun getOrCreateDeviceUuid(context: Context): String {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        var uuid = prefs.getString("device_uuid", null)
        if (uuid == null) {
            uuid = UUID.randomUUID().toString()
            prefs.edit { putString("device_uuid", uuid) }
            Log.d("uuid", "Generated new UUID: $uuid")
        } else {
            Log.d("uuid", "Retrieved existing UUID: $uuid")
        }
        return uuid
        setUpNaverSdk()
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

    private fun setUpNaverSdk() {
        NaverMapSdk.getInstance(this).client =
            NaverMapSdk.NcpKeyClient(BuildConfig.NAVER_MAP_CLIENT_ID)
    }
}

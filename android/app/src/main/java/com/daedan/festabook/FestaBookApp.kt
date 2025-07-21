package com.daedan.festabook

import android.app.Application
import com.daedan.festabook.data.repository.ScheduleRepositoryImpl
import com.daedan.festabook.domain.repository.ScheduleRepository
import com.naver.maps.map.NaverMapSdk

class FestaBookApp : Application() {
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer()
        setUpNaverSdk()

    }

    private fun setUpNaverSdk() {
        NaverMapSdk.getInstance(this).client =
            NaverMapSdk.NcpKeyClient(BuildConfig.NAVER_MAP_CLIENT_ID)
    }
}

package com.daedan.festabook

import android.app.Application
import com.daedan.festabook.data.datasource.remote.schedule.ScheduleDataSource
import com.daedan.festabook.data.datasource.remote.schedule.ScheduleDataSourceImpl
import com.daedan.festabook.data.repository.ScheduleRepositoryImpl
import com.daedan.festabook.data.service.api.ApiClient.scheduleService
import com.daedan.festabook.domain.repository.ScheduleRepository

class FestaBookApp : Application() {
    private val scheduleDataSource: ScheduleDataSource by lazy {
        ScheduleDataSourceImpl(scheduleService)
    }

    val scheduleRepository: ScheduleRepository by lazy {
        ScheduleRepositoryImpl(scheduleDataSource)
    }
}

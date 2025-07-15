package com.daedan.festabook

import android.app.Application
import com.daedan.festabook.data.repository.ScheduleRepositoryImpl
import com.daedan.festabook.domain.repository.ScheduleRepository

class FestaBookApp : Application() {
    val scheduleRepository: ScheduleRepository by lazy {
        ScheduleRepositoryImpl()
    }
}

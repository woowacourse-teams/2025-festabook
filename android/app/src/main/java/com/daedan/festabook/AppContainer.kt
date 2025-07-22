package com.daedan.festabook

import com.daedan.festabook.data.datasource.remote.NoticeDataSource
import com.daedan.festabook.data.datasource.remote.NoticeDataSourceImpl
import com.daedan.festabook.data.datasource.remote.placeDetail.PlaceDetailDataSource
import com.daedan.festabook.data.datasource.remote.placeDetail.PlaceDetailDataSourceImpl
import com.daedan.festabook.data.datasource.remote.placeList.PlaceListDataSource
import com.daedan.festabook.data.datasource.remote.placeList.PlaceListDataSourceImpl
import com.daedan.festabook.data.datasource.remote.schedule.ScheduleDataSource
import com.daedan.festabook.data.datasource.remote.schedule.ScheduleDataSourceImpl
import com.daedan.festabook.data.repository.NoticeRepositoryImpl
import com.daedan.festabook.data.repository.PlaceDetailRepositoryImpl
import com.daedan.festabook.data.repository.PlaceListRepositoryImpl
import com.daedan.festabook.data.repository.ScheduleRepositoryImpl
import com.daedan.festabook.data.service.api.ApiClient.noticeService
import com.daedan.festabook.data.service.api.ApiClient.placeService
import com.daedan.festabook.data.service.api.ApiClient.scheduleService
import com.daedan.festabook.domain.repository.NoticeRepository
import com.daedan.festabook.domain.repository.PlaceDetailRepository
import com.daedan.festabook.domain.repository.PlaceListRepository
import com.daedan.festabook.domain.repository.ScheduleRepository

class AppContainer {
    private val scheduleDataSource: ScheduleDataSource by lazy {
        ScheduleDataSourceImpl(scheduleService)
    }
    private val noticeDataSource: NoticeDataSource by lazy {
        NoticeDataSourceImpl(noticeService)
    }

    private val placeListDataSource: PlaceListDataSource by lazy {
        PlaceListDataSourceImpl(placeService)
    }

    private val placeDetailDataSource: PlaceDetailDataSource by lazy {
        PlaceDetailDataSourceImpl(placeService)
    }

    val placeDetailRepository: PlaceDetailRepository by lazy {
        PlaceDetailRepositoryImpl(placeDetailDataSource)
    }

    val placeListRepository: PlaceListRepository by lazy {
        PlaceListRepositoryImpl(placeListDataSource)
    }

    val scheduleRepository: ScheduleRepository by lazy {
        ScheduleRepositoryImpl(scheduleDataSource)
    }
    val noticeRepository: NoticeRepository by lazy {
        NoticeRepositoryImpl(noticeDataSource)
    }
}

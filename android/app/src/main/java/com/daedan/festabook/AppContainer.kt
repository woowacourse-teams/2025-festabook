package com.daedan.festabook

import android.app.Application
import com.daedan.festabook.data.datasource.local.AppPreferencesManager
import com.daedan.festabook.data.datasource.remote.device.DeviceDataSource
import com.daedan.festabook.data.datasource.remote.device.DeviceDataSourceImpl
import com.daedan.festabook.data.datasource.remote.faq.FAQDataSource
import com.daedan.festabook.data.datasource.remote.faq.FAQDataSourceImpl
import com.daedan.festabook.data.datasource.remote.notice.NoticeDataSource
import com.daedan.festabook.data.datasource.remote.notice.NoticeDataSourceImpl
import com.daedan.festabook.data.datasource.remote.organization.FestivalDataSource
import com.daedan.festabook.data.datasource.remote.organization.FestivalDataSourceImpl
import com.daedan.festabook.data.datasource.remote.organization.OrganizationBookmarkDataSource
import com.daedan.festabook.data.datasource.remote.organization.OrganizationBookmarkDataSourceImpl
import com.daedan.festabook.data.datasource.remote.place.PlaceDataSource
import com.daedan.festabook.data.datasource.remote.place.PlaceDataSourceImpl
import com.daedan.festabook.data.datasource.remote.schedule.ScheduleDataSource
import com.daedan.festabook.data.datasource.remote.schedule.ScheduleDataSourceImpl
import com.daedan.festabook.data.repository.BookmarkRepositoryImpl
import com.daedan.festabook.data.repository.DeviceRepositoryImpl
import com.daedan.festabook.data.repository.FAQRepositoryImpl
import com.daedan.festabook.data.repository.FestivalRepositoryImpl
import com.daedan.festabook.data.repository.LostItemRepositoryImpl
import com.daedan.festabook.data.repository.NoticeRepositoryImpl
import com.daedan.festabook.data.repository.PlaceDetailRepositoryImpl
import com.daedan.festabook.data.repository.PlaceListRepositoryImpl
import com.daedan.festabook.data.repository.ScheduleRepositoryImpl
import com.daedan.festabook.data.service.api.ApiClient.deviceService
import com.daedan.festabook.data.service.api.ApiClient.faqService
import com.daedan.festabook.data.service.api.ApiClient.festivalService
import com.daedan.festabook.data.service.api.ApiClient.noticeService
import com.daedan.festabook.data.service.api.ApiClient.organizationBookmarkService
import com.daedan.festabook.data.service.api.ApiClient.placeService
import com.daedan.festabook.data.service.api.ApiClient.scheduleService
import com.daedan.festabook.domain.repository.BookmarkRepository
import com.daedan.festabook.domain.repository.DeviceRepository
import com.daedan.festabook.domain.repository.FAQRepository
import com.daedan.festabook.domain.repository.FestivalRepository
import com.daedan.festabook.domain.repository.LostItemRepository
import com.daedan.festabook.domain.repository.NoticeRepository
import com.daedan.festabook.domain.repository.PlaceDetailRepository
import com.daedan.festabook.domain.repository.PlaceListRepository
import com.daedan.festabook.domain.repository.ScheduleRepository
import com.google.firebase.messaging.FirebaseMessaging
import timber.log.Timber
import java.util.UUID

class AppContainer(
    application: Application,
) {
    val preferencesManager = AppPreferencesManager(application)

    private val scheduleDataSource: ScheduleDataSource by lazy {
        ScheduleDataSourceImpl(scheduleService)
    }
    private val noticeDataSource: NoticeDataSource by lazy {
        NoticeDataSourceImpl(noticeService)
    }
    private val deviceDataSource: DeviceDataSource by lazy {
        DeviceDataSourceImpl(deviceService)
    }
    private val organizationBookmarkDataSource: OrganizationBookmarkDataSource by lazy {
        OrganizationBookmarkDataSourceImpl(organizationBookmarkService)
    }
    private val placeListDataSource: PlaceDataSource by lazy {
        PlaceDataSourceImpl(placeService, festivalService)
    }
    private val placeDetailDataSource: PlaceDataSource by lazy {
        PlaceDataSourceImpl(placeService, festivalService)
    }
    private val faqDataSource: FAQDataSource by lazy {
        FAQDataSourceImpl(faqService)
    }

    private val festivalDataSource: FestivalDataSource by lazy {
        FestivalDataSourceImpl(festivalService)
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
    val deviceRepository: DeviceRepository by lazy {
        DeviceRepositoryImpl(deviceDataSource)
    }
    val bookmarkRepository: BookmarkRepository by lazy {
        BookmarkRepositoryImpl(organizationBookmarkDataSource)
    }
    val festivalRepository: FestivalRepository by lazy {
        FestivalRepositoryImpl(festivalDataSource)
    }
    val faqRepository: FAQRepository by lazy {
        FAQRepositoryImpl(faqDataSource)
    }

    val lostItemRepository: LostItemRepository by lazy {
        LostItemRepositoryImpl()
    }

    init {
        ensureDeviceIdentifiers()
    }

    private fun ensureDeviceIdentifiers() {
        if (preferencesManager.getUuid().isNullOrEmpty()) {
            val uuid = UUID.randomUUID().toString()
            preferencesManager.saveUuid(uuid)
            Timber.d("🆕 UUID 생성 및 저장: $uuid")
        }

        FirebaseMessaging
            .getInstance()
            .token
            .addOnSuccessListener { token ->
                preferencesManager.saveFcmToken(token)
                Timber.d("📡 FCM 토큰 저장: $token")
            }.addOnFailureListener {
                Timber.w(it, "❌ FCM 토큰 수신 실패")
            }
    }
}

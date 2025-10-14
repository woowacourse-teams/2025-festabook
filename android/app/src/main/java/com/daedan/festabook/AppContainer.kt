package com.daedan.festabook

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.daedan.festabook.data.datasource.local.DeviceLocalDataSource
import com.daedan.festabook.data.datasource.local.DeviceLocalDataSourceImpl
import com.daedan.festabook.data.datasource.local.FcmDataSource
import com.daedan.festabook.data.datasource.local.FcmDataSourceImpl
import com.daedan.festabook.data.datasource.local.FestivalLocalDataSource
import com.daedan.festabook.data.datasource.local.FestivalLocalDataSourceImpl
import com.daedan.festabook.data.datasource.local.FestivalNotificationLocalDataSource
import com.daedan.festabook.data.datasource.local.FestivalNotificationLocalDataSourceImpl
import com.daedan.festabook.data.datasource.remote.device.DeviceDataSource
import com.daedan.festabook.data.datasource.remote.device.DeviceDataSourceImpl
import com.daedan.festabook.data.datasource.remote.faq.FAQDataSource
import com.daedan.festabook.data.datasource.remote.faq.FAQDataSourceImpl
import com.daedan.festabook.data.datasource.remote.festival.FestivalDataSource
import com.daedan.festabook.data.datasource.remote.festival.FestivalDataSourceImpl
import com.daedan.festabook.data.datasource.remote.festival.FestivalNotificationDataSource
import com.daedan.festabook.data.datasource.remote.festival.FestivalNotificationDataSourceImpl
import com.daedan.festabook.data.datasource.remote.lineup.LineupDataSource
import com.daedan.festabook.data.datasource.remote.lineup.LineupDataSourceImpl
import com.daedan.festabook.data.datasource.remote.lostitem.LostItemDataSource
import com.daedan.festabook.data.datasource.remote.lostitem.LostItemDataSourceImpl
import com.daedan.festabook.data.datasource.remote.notice.NoticeDataSource
import com.daedan.festabook.data.datasource.remote.notice.NoticeDataSourceImpl
import com.daedan.festabook.data.datasource.remote.place.PlaceDataSource
import com.daedan.festabook.data.datasource.remote.place.PlaceDataSourceImpl
import com.daedan.festabook.data.datasource.remote.schedule.ScheduleDataSource
import com.daedan.festabook.data.datasource.remote.schedule.ScheduleDataSourceImpl
import com.daedan.festabook.data.repository.DeviceRepositoryImpl
import com.daedan.festabook.data.repository.ExploreRepositoryImpl
import com.daedan.festabook.data.repository.FAQRepositoryImpl
import com.daedan.festabook.data.repository.FestivalNotificationRepositoryImpl
import com.daedan.festabook.data.repository.FestivalRepositoryImpl
import com.daedan.festabook.data.repository.LostItemRepositoryImpl
import com.daedan.festabook.data.repository.NoticeRepositoryImpl
import com.daedan.festabook.data.repository.PlaceDetailRepositoryImpl
import com.daedan.festabook.data.repository.PlaceListRepositoryImpl
import com.daedan.festabook.data.repository.ScheduleRepositoryImpl
import com.daedan.festabook.data.service.api.ApiClient.deviceService
import com.daedan.festabook.data.service.api.ApiClient.faqService
import com.daedan.festabook.data.service.api.ApiClient.festivalLineupService
import com.daedan.festabook.data.service.api.ApiClient.festivalNotificationService
import com.daedan.festabook.data.service.api.ApiClient.festivalService
import com.daedan.festabook.data.service.api.ApiClient.lostItemService
import com.daedan.festabook.data.service.api.ApiClient.noticeService
import com.daedan.festabook.data.service.api.ApiClient.placeService
import com.daedan.festabook.data.service.api.ApiClient.scheduleService
import com.daedan.festabook.domain.repository.DeviceRepository
import com.daedan.festabook.domain.repository.ExploreRepository
import com.daedan.festabook.domain.repository.FAQRepository
import com.daedan.festabook.domain.repository.FestivalNotificationRepository
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
    private val prefs: SharedPreferences =
        application.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val deviceLocalDataSource: DeviceLocalDataSource by lazy {
        DeviceLocalDataSourceImpl(prefs)
    }

    private val fcmDataSource: FcmDataSource by lazy {
        FcmDataSourceImpl(prefs)
    }

    val festivalNotificationLocalDataSource: FestivalNotificationLocalDataSource by lazy {
        FestivalNotificationLocalDataSourceImpl(prefs)
    }

    val festivalLocalDataSource: FestivalLocalDataSource by lazy {
        FestivalLocalDataSourceImpl(prefs)
    }

    private val scheduleDataSource: ScheduleDataSource by lazy {
        ScheduleDataSourceImpl(scheduleService)
    }
    private val noticeDataSource: NoticeDataSource by lazy {
        NoticeDataSourceImpl(noticeService)
    }
    private val deviceDataSource: DeviceDataSource by lazy {
        DeviceDataSourceImpl(deviceService)
    }
    private val festivalNotificationDataSource: FestivalNotificationDataSource by lazy {
        FestivalNotificationDataSourceImpl(festivalNotificationService)
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

    private val lostItemDataSource: LostItemDataSource by lazy {
        LostItemDataSourceImpl(lostItemService, festivalService)
    }

    private val lineupDataSource: LineupDataSource by lazy {
        LineupDataSourceImpl(festivalLineupService)
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
        DeviceRepositoryImpl(deviceDataSource, deviceLocalDataSource, fcmDataSource)
    }
    val festivalNotificationRepository: FestivalNotificationRepository by lazy {
        FestivalNotificationRepositoryImpl(
            festivalNotificationDataSource,
            deviceLocalDataSource,
            festivalNotificationLocalDataSource,
            festivalLocalDataSource,
        )
    }
    val festivalRepository: FestivalRepository by lazy {
        FestivalRepositoryImpl(festivalDataSource, festivalLocalDataSource, lineupDataSource)
    }
    val faqRepository: FAQRepository by lazy {
        FAQRepositoryImpl(faqDataSource)
    }

    val lostItemRepository: LostItemRepository by lazy {
        LostItemRepositoryImpl(lostItemDataSource)
    }

    val exploreRepository: ExploreRepository by lazy {
        ExploreRepositoryImpl(
            festivalDataSource,
            festivalLocalDataSource,
        )
    }

    init {
        ensureDeviceIdentifiers()
    }

    private fun ensureDeviceIdentifiers() {
        if (deviceLocalDataSource.getUuid().isNullOrEmpty()) {
            val uuid = UUID.randomUUID().toString()
            deviceLocalDataSource.saveUuid(uuid)
            Timber.d("🆕 UUID 생성 및 저장: $uuid")
        }

        FirebaseMessaging
            .getInstance()
            .token
            .addOnSuccessListener { token ->
                fcmDataSource.saveFcmToken(token)
                Timber.d("📡 FCM 토큰 저장: $token")
            }.addOnFailureListener {
                Timber.w(it, "❌ FCM 토큰 수신 실패")
            }
    }

    companion object {
        private const val PREFS_NAME = "app_prefs"
    }
}

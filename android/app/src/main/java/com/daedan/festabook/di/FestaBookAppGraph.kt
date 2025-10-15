package com.daedan.festabook.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.daedan.festabook.BuildConfig
import com.daedan.festabook.data.datasource.local.DeviceLocalDataSource
import com.daedan.festabook.data.datasource.local.FcmDataSource
import com.daedan.festabook.data.datasource.local.FestivalLocalDataSource
import com.daedan.festabook.data.datasource.local.FestivalNotificationLocalDataSource
import com.daedan.festabook.data.datasource.remote.faq.FAQDataSource
import com.daedan.festabook.data.datasource.remote.festival.FestivalDataSource
import com.daedan.festabook.data.datasource.remote.festival.FestivalNotificationDataSource
import com.daedan.festabook.data.datasource.remote.lineup.LineupDataSource
import com.daedan.festabook.data.datasource.remote.lostitem.LostItemDataSource
import com.daedan.festabook.data.datasource.remote.notice.NoticeDataSource
import com.daedan.festabook.data.datasource.remote.place.PlaceDataSource
import com.daedan.festabook.data.datasource.remote.schedule.ScheduleDataSource
import com.daedan.festabook.data.service.DeviceService
import com.daedan.festabook.data.service.FAQService
import com.daedan.festabook.data.service.FestivalLineupService
import com.daedan.festabook.data.service.FestivalNotificationService
import com.daedan.festabook.data.service.FestivalService
import com.daedan.festabook.data.service.LostItemService
import com.daedan.festabook.data.service.NoticeService
import com.daedan.festabook.data.service.PlaceService
import com.daedan.festabook.data.service.ScheduleService
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
import com.daedan.festabook.presentation.placeDetail.PlaceDetailViewModel
import com.daedan.festabook.presentation.schedule.ScheduleViewModel
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

private const val BASE_URL = BuildConfig.FESTABOOK_URL
private const val PREFS_NAME = "app_prefs"

@DependencyGraph(AppScope::class)
interface FestaBookAppGraph {
    @DependencyGraph.Factory
    fun interface Factory {
        fun createAppGraph(
            @Provides application: Application,
        ): FestaBookAppGraph
    }

    val application: Application
    val sharedPreferences: SharedPreferences

    val retrofit: Retrofit
    val okHttpClient: OkHttpClient
    val authInterceptor: Interceptor
    val json: Json

    // dataSource
    val deviceLocalDataSource: DeviceLocalDataSource
    val festivalLocalDataSource: FestivalLocalDataSource
    val fcmDataSource: FcmDataSource
    val scheduleDataSource: ScheduleDataSource
    val noticeDataSource: NoticeDataSource
    val festivalDataSource: FestivalDataSource
    val festivalNotificationDataSource: FestivalNotificationDataSource
    val placeDataSource: PlaceDataSource
    val faqDataSource: FAQDataSource
    val lostItemDataSource: LostItemDataSource
    val lineupDataSource: LineupDataSource
    val festivalNotificationLocalDataSource: FestivalNotificationLocalDataSource

    // repository
    val deviceRepository: DeviceRepository
    val festivalRepository: FestivalRepository
    val scheduleRepository: ScheduleRepository
    val noticeRepository: NoticeRepository
    val faqRepository: FAQRepository
    val lostItemRepository: LostItemRepository
    val exploreRepository: ExploreRepository
    val festivalNotificationRepository: FestivalNotificationRepository
    val placeListRepository: PlaceListRepository
    val placeDetailRepository: PlaceDetailRepository

    // viewModelGraphFactory
    val viewModelGraphFactory: ViewModelGraph.Factory
    val metroViewModelFactory: MetroViewModelFactory
    val scheduleViewModelFactory: ScheduleViewModel.Factory
    val placeDetailViewModelPlaceFactory: PlaceDetailViewModel.PlaceFactory

    @Provides
    fun provideSharedPreferences(): SharedPreferences = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    @Provides
    fun provideContext(application: Application): Context = application.applicationContext

    @Provides
    fun provideJson(): Json = Json { ignoreUnknownKeys = true }

    @Provides
    fun providesRetrofit(): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    @Provides
    fun providesOkHttpClient(): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(authInterceptor)
            .build()

    @Provides
    fun provideScheduleService(retrofit: Retrofit): ScheduleService = retrofit.create(ScheduleService::class.java)

    @Provides
    fun provideNoticeService(retrofit: Retrofit): NoticeService = retrofit.create(NoticeService::class.java)

    @Provides
    fun providePlaceService(retrofit: Retrofit): PlaceService = retrofit.create(PlaceService::class.java)

    @Provides
    fun provideDeviceService(retrofit: Retrofit): DeviceService = retrofit.create(DeviceService::class.java)

    @Provides
    fun provideFestivalNotificationService(retrofit: Retrofit): FestivalNotificationService =
        retrofit.create(FestivalNotificationService::class.java)

    @Provides
    fun provideFAQService(retrofit: Retrofit): FAQService = retrofit.create(FAQService::class.java)

    @Provides
    fun provideFestivalService(retrofit: Retrofit): FestivalService = retrofit.create(FestivalService::class.java)

    @Provides
    fun provideLostItemService(retrofit: Retrofit): LostItemService = retrofit.create(LostItemService::class.java)

    @Provides
    fun provideFestivalLineupService(retrofit: Retrofit): FestivalLineupService = retrofit.create(FestivalLineupService::class.java)
}

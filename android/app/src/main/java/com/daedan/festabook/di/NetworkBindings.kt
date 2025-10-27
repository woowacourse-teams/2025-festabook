package com.daedan.festabook.di

import com.daedan.festabook.BuildConfig
import com.daedan.festabook.data.service.DeviceService
import com.daedan.festabook.data.service.FAQService
import com.daedan.festabook.data.service.FestivalLineupService
import com.daedan.festabook.data.service.FestivalNotificationService
import com.daedan.festabook.data.service.FestivalService
import com.daedan.festabook.data.service.LostItemService
import com.daedan.festabook.data.service.NoticeService
import com.daedan.festabook.data.service.PlaceService
import com.daedan.festabook.data.service.ScheduleService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

@BindingContainer
@ContributesTo(AppScope::class)
object NetworkBindings {
    private const val BASE_URL = BuildConfig.FESTABOOK_URL

    @Provides
    fun provideJson(): Json = Json { ignoreUnknownKeys = true }

    @Provides
    fun providesRetrofit(
        okHttpClient: OkHttpClient,
        json: Json,
    ): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    @Provides
    fun providesOkHttpClient(authInterceptor: Interceptor): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(authInterceptor)
            .build()

    @Provides
    fun provideScheduleService(retrofit: Retrofit): ScheduleService =
        retrofit.create(
            ScheduleService::class.java,
        )

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
    fun provideFestivalService(retrofit: Retrofit): FestivalService =
        retrofit.create(
            FestivalService::class.java,
        )

    @Provides
    fun provideLostItemService(retrofit: Retrofit): LostItemService =
        retrofit.create(
            LostItemService::class.java,
        )

    @Provides
    fun provideFestivalLineupService(retrofit: Retrofit): FestivalLineupService =
        retrofit.create(
            FestivalLineupService::class.java,
        )
}

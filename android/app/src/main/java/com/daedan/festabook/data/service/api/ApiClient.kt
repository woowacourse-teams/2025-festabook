package com.daedan.festabook.data.service.api

import com.daedan.festabook.BuildConfig
import com.daedan.festabook.data.service.DeviceService
import com.daedan.festabook.data.service.FAQService
import com.daedan.festabook.data.service.FestivalService
import com.daedan.festabook.data.service.NoticeService
import com.daedan.festabook.data.service.OrganizationBookmarkService
import com.daedan.festabook.data.service.PlaceService
import com.daedan.festabook.data.service.ScheduleService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

object ApiClient {
    private const val BASE_URL = BuildConfig.FESTABOOK_URL
    private val json = Json { ignoreUnknownKeys = true }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient
            .Builder()
            .addInterceptor(FestaBookAuthInterceptor("1"))
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    val scheduleService: ScheduleService by lazy { retrofit.create(ScheduleService::class.java) }
    val noticeService: NoticeService = retrofit.create(NoticeService::class.java)
    val placeService: PlaceService = retrofit.create(PlaceService::class.java)
    val deviceService: DeviceService by lazy { retrofit.create(DeviceService::class.java) }
    val organizationBookmarkService: OrganizationBookmarkService by lazy {
        retrofit.create(
            OrganizationBookmarkService::class.java,
        )
    }
    val faqService: FAQService by lazy { retrofit.create(FAQService::class.java) }

    val festivalService: FestivalService = retrofit.create(FestivalService::class.java)
}

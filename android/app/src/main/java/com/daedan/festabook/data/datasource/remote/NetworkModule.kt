package com.daedan.festabook.data.datasource.remote

import com.daedan.festabook.BuildConfig
import com.daedan.festabook.data.api.ScheduleApi
import com.daedan.festabook.data.datasource.remote.adapter.ApiResultCallAdapterFactory
import com.daedan.festabook.data.interceptor.FestaBookAuthInterceptor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

object NetworkModule {
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
            .baseUrl(BuildConfig.FESTABOOK_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .addCallAdapterFactory(ApiResultCallAdapterFactory())
            .build()
    }

    val scheduleApi: ScheduleApi by lazy {
        retrofit.create(ScheduleApi::class.java)
    }
}

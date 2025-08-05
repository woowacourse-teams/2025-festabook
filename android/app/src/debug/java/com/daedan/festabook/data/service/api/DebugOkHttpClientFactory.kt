package com.daedan.festabook.data.service.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

fun createDebugOkHttpClient(): OkHttpClient =
    createOkHttpClient()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        .build()

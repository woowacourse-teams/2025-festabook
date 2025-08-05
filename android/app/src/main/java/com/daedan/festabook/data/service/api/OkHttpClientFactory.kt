package com.daedan.festabook.data.service.api

import okhttp3.OkHttpClient

fun createOkHttpClient(): OkHttpClient.Builder =
    OkHttpClient
        .Builder()
        .addInterceptor(FestaBookAuthInterceptor("1"))

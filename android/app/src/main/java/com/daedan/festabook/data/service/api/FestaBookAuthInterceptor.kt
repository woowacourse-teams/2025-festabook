package com.daedan.festabook.data.service.api

import okhttp3.Interceptor
import okhttp3.Response

class FestaBookAuthInterceptor(
    private val organizationId: String,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestWithHeader =
            originalRequest
                .newBuilder()
                .addHeader("organization", organizationId)
                .build()

        return chain.proceed(requestWithHeader)
    }
}

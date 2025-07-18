package com.daedan.festabook.data.datasource.remote

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

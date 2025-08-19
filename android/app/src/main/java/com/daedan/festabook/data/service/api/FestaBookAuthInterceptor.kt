package com.daedan.festabook.data.service.api

import com.daedan.festabook.data.datasource.local.FestivalLocalDataSource
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber

class FestaBookAuthInterceptor(
    private val festivalLocalDataSource: FestivalLocalDataSource,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val festivalId = festivalLocalDataSource.getFestivalId()
        val requestBuilder = originalRequest.newBuilder()

        if (festivalId != null) {
            Timber.d("festivalId : $festivalId")
            requestBuilder.addHeader("festival", festivalId.toString())
        }

        val requestWithHeader = requestBuilder.build()
        return chain.proceed(requestWithHeader)
    }
}

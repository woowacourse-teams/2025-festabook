package com.daedan.festabook.data.service.api

import com.daedan.festabook.data.datasource.local.FestivalLocalDataSource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber

@ContributesBinding(AppScope::class)
class FestaBookAuthInterceptor @Inject constructor(
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

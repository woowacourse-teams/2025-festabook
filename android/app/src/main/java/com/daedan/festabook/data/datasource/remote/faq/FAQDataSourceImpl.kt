package com.daedan.festabook.data.datasource.remote.faq

import com.daedan.festabook.data.datasource.remote.ApiResult
import com.daedan.festabook.data.model.response.faq.FAQResponse
import com.daedan.festabook.data.service.FAQService
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@ContributesBinding(AppScope::class)
class FAQDataSourceImpl @Inject constructor(
    private val faqService: FAQService,
) : FAQDataSource {
    override suspend fun fetchAllFAQs(): ApiResult<List<FAQResponse>> = ApiResult.toApiResult { faqService.fetchAllFAQs() }
}

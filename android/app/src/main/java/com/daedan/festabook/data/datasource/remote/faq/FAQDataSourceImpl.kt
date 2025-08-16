package com.daedan.festabook.data.datasource.remote.faq

import com.daedan.festabook.data.datasource.remote.ApiResult
import com.daedan.festabook.data.model.response.faq.FAQResponse
import com.daedan.festabook.data.service.FAQService

class FAQDataSourceImpl(
    private val faqService: FAQService,
) : FAQDataSource {
    override suspend fun fetchAllFAQs(): ApiResult<List<FAQResponse>> = ApiResult.toApiResult { faqService.fetchAllFAQs() }
}

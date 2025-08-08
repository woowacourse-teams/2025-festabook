package com.daedan.festabook.data.datasource.remote.faq

import com.daedan.festabook.data.datasource.remote.ApiResult
import com.daedan.festabook.data.model.response.FAQResponse

interface FAQDataSource {
    suspend fun fetchAllFAQs(): ApiResult<List<FAQResponse>>
}

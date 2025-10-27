package com.daedan.festabook.data.repository

import com.daedan.festabook.data.datasource.remote.faq.FAQDataSource
import com.daedan.festabook.data.model.response.faq.toDomain
import com.daedan.festabook.data.util.toResult
import com.daedan.festabook.domain.model.FAQItem
import com.daedan.festabook.domain.repository.FAQRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@ContributesBinding(AppScope::class)
class FAQRepositoryImpl @Inject constructor(
    private val faqDataSource: FAQDataSource,
) : FAQRepository {
    override suspend fun getAllFAQ(): Result<List<FAQItem>> {
        val response = faqDataSource.fetchAllFAQs().toResult()
        return response.mapCatching { fAQItems -> fAQItems.map { it.toDomain() } }
    }
}

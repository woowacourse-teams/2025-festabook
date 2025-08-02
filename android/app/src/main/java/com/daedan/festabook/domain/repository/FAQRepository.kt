package com.daedan.festabook.domain.repository

import com.daedan.festabook.domain.model.FAQItem

interface FAQRepository {
    suspend fun getAllFAQ(): Result<List<FAQItem>>
}

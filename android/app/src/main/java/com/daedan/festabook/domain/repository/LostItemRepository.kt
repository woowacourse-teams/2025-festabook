package com.daedan.festabook.domain.repository

import com.daedan.festabook.domain.model.LostItem

interface LostItemRepository {
    suspend fun getPendingLostItems(): Result<List<LostItem>>
}

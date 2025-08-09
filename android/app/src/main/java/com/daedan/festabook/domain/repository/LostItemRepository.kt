package com.daedan.festabook.domain.repository

import com.daedan.festabook.domain.model.LostItem

interface LostItemRepository {
    suspend fun getAllLostItems(): Result<List<LostItem>>
}

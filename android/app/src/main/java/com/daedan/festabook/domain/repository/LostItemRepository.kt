package com.daedan.festabook.domain.repository

import com.daedan.festabook.domain.model.Lost

interface LostItemRepository {
    suspend fun getPendingLostItems(): Result<List<Lost>>

    suspend fun getLostGuideItem(): Result<Lost>

    suspend fun getLost(): List<Lost?>
}

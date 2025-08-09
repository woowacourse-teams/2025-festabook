package com.daedan.festabook.data.repository

import com.daedan.festabook.data.datasource.remote.lostitem.LostItemDataSource
import com.daedan.festabook.data.model.response.lostitem.toDomain
import com.daedan.festabook.data.util.toResult
import com.daedan.festabook.domain.model.LostItem
import com.daedan.festabook.domain.repository.LostItemRepository

class LostItemRepositoryImpl(
    private val lostItemDataSource: LostItemDataSource,
) : LostItemRepository {
    override suspend fun getAllLostItems(): Result<List<LostItem>> =
        lostItemDataSource
            .fetchAllLostItem()
            .toResult()
            .mapCatching { it.map { lostItemResponse -> lostItemResponse.toDomain() } }
}

package com.daedan.festabook.data.repository

import com.daedan.festabook.data.datasource.remote.lostitem.LostItemDataSource
import com.daedan.festabook.data.model.response.lostitem.toDomain
import com.daedan.festabook.data.util.toResult
import com.daedan.festabook.domain.model.Lost
import com.daedan.festabook.domain.model.LostItemStatus
import com.daedan.festabook.domain.repository.LostItemRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

@ContributesBinding(AppScope::class)
class LostItemRepositoryImpl @Inject constructor(
    private val lostItemDataSource: LostItemDataSource,
) : LostItemRepository {
    override suspend fun getPendingLostItems(): Result<List<Lost>> =
        lostItemDataSource
            .fetchAllLostItems()
            .toResult()
            .mapCatching { lostItemResponses ->
                lostItemResponses
                    .map { lostItemResponse -> lostItemResponse.toDomain() }
                    .filter { it.status == LostItemStatus.PENDING }
                    .sortedByDescending { it.createdAt }
            }

    override suspend fun getLostGuideItem(): Result<Lost> =
        lostItemDataSource.fetchLostGuideItem().toResult().mapCatching {
            it.toDomain()
        }

    override suspend fun getLost(): List<Lost?> =
        coroutineScope {
            val guide = async { getLostGuideItem() }
            val pendingItems = async { getPendingLostItems() }

            val guideResult = guide.await()
            val pendingItemsResult = pendingItems.await()

            val total = mutableListOf<Lost?>()

            total.add(0, guideResult.getOrNull())
            pendingItemsResult.getOrNull()?.let { total.addAll(it) }

            total
        }
}

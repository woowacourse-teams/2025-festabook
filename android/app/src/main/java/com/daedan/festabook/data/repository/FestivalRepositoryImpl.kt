package com.daedan.festabook.data.repository

import com.daedan.festabook.data.datasource.remote.festival.FestivalDataSource
import com.daedan.festabook.data.model.response.festival.toDomain
import com.daedan.festabook.data.util.toResult
import com.daedan.festabook.domain.model.LineupItem
import com.daedan.festabook.domain.model.Organization
import com.daedan.festabook.domain.repository.FestivalRepository

class FestivalRepositoryImpl(
    private val festivalDataSource: FestivalDataSource,
) : FestivalRepository {
    override suspend fun getFestivalInfo(): Result<Organization> {
        val response = festivalDataSource.fetchFestival().toResult()
        return response.mapCatching { it.toDomain() }
    }

    override suspend fun getLineup(): Result<List<LineupItem>> {
        val dummyData =
            listOf(
                LineupItem(
                    id = 1,
                    imageUrl = "https://images.unsplash.com/photo-1754951661102-341dfb58bd26?q=80&w=1335&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                    name = "실리카겔",
                ),
                LineupItem(
                    id = 2,
                    imageUrl = "https://images.unsplash.com/photo-1754951661102-341dfb58bd26?q=80&w=1335&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                    name = "요네즈 켄시",
                ),
                LineupItem(
                    id = 3,
                    imageUrl = "https://images.unsplash.com/photo-1754951661102-341dfb58bd26?q=80&w=1335&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                    name = "하츠네 미쿠",
                ),
            )
        return Result.success(dummyData)
    }
}

package com.daedan.festabook.data.repository

import com.daedan.festabook.data.datasource.remote.festival.FestivalDataSource
import com.daedan.festabook.data.datasource.remote.lineup.LineupDataSource
import com.daedan.festabook.data.model.response.festival.toDomain
import com.daedan.festabook.data.model.response.lineup.toDomain
import com.daedan.festabook.data.util.toResult
import com.daedan.festabook.domain.model.LineupItem
import com.daedan.festabook.domain.model.Organization
import com.daedan.festabook.domain.repository.FestivalRepository

class FestivalRepositoryImpl(
    private val festivalDataSource: FestivalDataSource,
    private val lineupDataSource: LineupDataSource,
) : FestivalRepository {
    override suspend fun getFestivalInfo(): Result<Organization> {
        val response = festivalDataSource.fetchFestival().toResult()
        return response.mapCatching { it.toDomain() }
    }

    override suspend fun getLineup(): Result<List<LineupItem>> {
        val response = lineupDataSource.fetchLineup().toResult()

        return response.mapCatching { lineupResponses -> lineupResponses.map { it.toDomain() } }
    }
}

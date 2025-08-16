package com.daedan.festabook.data.repository

import com.daedan.festabook.data.datasource.remote.festival.FestivalDataSource
import com.daedan.festabook.data.model.response.festival.toDomain
import com.daedan.festabook.data.util.toResult
import com.daedan.festabook.domain.model.Organization
import com.daedan.festabook.domain.repository.FestivalRepository

class FestivalRepositoryImpl(
    private val festivalDataSource: FestivalDataSource,
) : FestivalRepository {
    override suspend fun getFestivalInfo(): Result<Organization> {
        val response = festivalDataSource.fetchFestival().toResult()
        return response.mapCatching { it.toDomain() }
    }
}

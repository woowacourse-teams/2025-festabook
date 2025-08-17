package com.daedan.festabook.data.repository

import com.daedan.festabook.data.datasource.local.FestivalLocalDataSource
import com.daedan.festabook.data.datasource.remote.festival.FestivalDataSource
import com.daedan.festabook.data.model.response.toDomain
import com.daedan.festabook.data.util.toResult
import com.daedan.festabook.domain.model.University
import com.daedan.festabook.domain.repository.ExploreRepository
import timber.log.Timber

class ExploreRepositoryImpl(
    private val festivalDataSource: FestivalDataSource,
    private val festivalLocalDataSource: FestivalLocalDataSource,
) : ExploreRepository {
    override suspend fun search(query: String): Result<List<University>> {
        Timber.d("Searching for query: $query")

        val response =
            festivalDataSource
                .findUniversitiesByName(universityName = query)
                .toResult()

        return response.mapCatching { universities -> universities.map { it.toDomain() } }
    }

    override fun saveFestivalId(festivalId: Long) = festivalLocalDataSource.saveFestivalId(festivalId)

    override fun getFestivalId(): Long? = festivalLocalDataSource.getFestivalId()
}

package com.daedan.festabook.domain.repository

import com.daedan.festabook.domain.model.University

interface ExploreRepository {
    suspend fun search(query: String): Result<List<University>>

    fun saveFestivalId(festivalId: Long)

    fun getFestivalId(): Long?
}

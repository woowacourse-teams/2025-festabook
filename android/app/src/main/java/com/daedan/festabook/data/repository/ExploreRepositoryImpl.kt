package com.daedan.festabook.data.repository

import com.daedan.festabook.domain.model.University
import com.daedan.festabook.domain.repository.ExploreRepository
import timber.log.Timber

class ExploreRepositoryImpl : ExploreRepository {
    override suspend fun search(query: String): Result<University?> {
        Timber.d("Searching for query: $query") // 쿼리 값 로그
        return if (query.contains("서울시립대학교")) {
            val university =
                University(
                    festivalId = 1,
                    universityName = "서울시립대학교",
                )
            Timber.d("Found university: $university") // 찾았을 때 로그
            Result.success(university)
        } else {
            Timber.d("No university found for query: $query") // 못 찾았을 때 로그
            Result.success(null)
        }
    }
}

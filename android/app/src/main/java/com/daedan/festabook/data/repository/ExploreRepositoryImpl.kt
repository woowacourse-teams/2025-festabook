package com.daedan.festabook.data.repository

import com.daedan.festabook.domain.model.University
import com.daedan.festabook.domain.repository.ExploreRepository
import timber.log.Timber

class ExploreRepositoryImpl : ExploreRepository {
    override suspend fun search(query: String): Result<List<University>> {
        Timber.d("Searching for query: $query")
        return if (query.contains("서울")) {
            val universities =
                listOf(
                    University(
                        festivalId = 1,
                        universityName = "서울시립대학교",
                    ),
                    University(
                        festivalId = 2,
                        universityName = "서울대학교",
                    ),
                    University(
                        festivalId = 3,
                        universityName = "서울과학기술대학교",
                    ),
                )
            Timber.d("검색 결과: $universities")
            Result.success(universities)
        } else {
            Timber.d("검색결과 없음: $query")
            Result.success(emptyList())
        }
    }
}

package com.daedan.festabook.logging.model.explore

import com.daedan.festabook.logging.model.BaseLogData
import kotlinx.parcelize.Parcelize

// 탐색 화면 진입
@Parcelize
data class ExploreViewLogData(
    override val baseLogData: BaseLogData.CommonLogData,
    val hasFestivalId: Boolean,
) : BaseLogData

// 검색 요청에 따른 결과 정보
@Parcelize
data class ExploreSearchResultLogData(
    override val baseLogData: BaseLogData.CommonLogData,
    val query: String,
    val resultCount: Int,
) : BaseLogData

// 대학교 선택
@Parcelize
data class ExploreSelectUniversityLogData(
    override val baseLogData: BaseLogData.CommonLogData,
    val universityName: String,
) : BaseLogData

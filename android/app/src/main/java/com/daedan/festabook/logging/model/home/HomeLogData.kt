package com.daedan.festabook.logging.model.home

import com.daedan.festabook.logging.model.BaseLogData
import kotlinx.parcelize.Parcelize

// 홈 화면 진입
@Parcelize
data class HomeViewLogData(
    override val baseLogData: BaseLogData.CommonLogData,
    val universityName: String,
    val festivalId: Long,
) : BaseLogData

// Explore 버튼 클릭
@Parcelize
data class ExploreClickLogData(
    override val baseLogData: BaseLogData.CommonLogData,
) : BaseLogData

// Schedule 버튼 클릭
@Parcelize
data class ScheduleClickLogData(
    override val baseLogData: BaseLogData.CommonLogData,
) : BaseLogData

// 라인업 아티스트 클릭
@Parcelize
data class LineupClickLogData(
    override val baseLogData: BaseLogData.CommonLogData,
    val artistId: Long,
) : BaseLogData

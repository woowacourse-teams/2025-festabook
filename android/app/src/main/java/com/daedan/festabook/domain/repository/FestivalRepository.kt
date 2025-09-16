package com.daedan.festabook.domain.repository

import com.daedan.festabook.domain.model.LineupItem
import com.daedan.festabook.domain.model.Organization
import java.time.LocalDate

interface FestivalRepository {
    suspend fun getFestivalInfo(): Result<Organization>

    suspend fun getLineUpGroupByDate(): Result<Map<LocalDate, List<LineupItem>>>

    fun getIsFirstVisit(): Result<Boolean>
}

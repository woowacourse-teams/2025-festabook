package com.daedan.festabook.domain.repository

import com.daedan.festabook.domain.model.LineupItem
import com.daedan.festabook.domain.model.Organization
import java.time.LocalDateTime

interface FestivalRepository {
    suspend fun getFestivalInfo(): Result<Organization>

    suspend fun getLineUpGroupByDate(): Result<Map<LocalDateTime, List<LineupItem>>>
}

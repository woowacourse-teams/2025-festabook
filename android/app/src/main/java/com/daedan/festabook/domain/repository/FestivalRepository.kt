package com.daedan.festabook.domain.repository

import com.daedan.festabook.domain.model.Organization
import com.daedan.festabook.presentation.home.LineupItemUiModel

interface FestivalRepository {
    suspend fun getFestivalInfo(): Result<Organization>

    suspend fun getLineup(): Result<List<LineupItemUiModel>>
}

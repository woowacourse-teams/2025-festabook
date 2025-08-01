package com.daedan.festabook.domain.repository

import com.daedan.festabook.domain.model.Organization

interface FestivalRepository {
    suspend fun getFestivalInfo(): Result<Organization>
}

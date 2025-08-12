package com.daedan.festabook.domain.repository

interface FestivalNotificationRepository {
    suspend fun saveFestivalNotification(): Result<Unit>

    suspend fun deleteFestivalNotification(): Result<Unit>

    fun getFestivalNotificationIsAllow(): Boolean

    fun setFestivalNotificationIsAllow(isAllowed: Boolean)
}

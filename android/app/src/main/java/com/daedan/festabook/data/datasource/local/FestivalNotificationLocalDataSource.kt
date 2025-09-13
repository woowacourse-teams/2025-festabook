package com.daedan.festabook.data.datasource.local

interface FestivalNotificationLocalDataSource {
    fun saveFestivalNotificationId(festivalNotificationId: Long)

    fun getFestivalNotificationId(): Long

    fun deleteFestivalNotificationId()

    fun clearAll()

    fun saveFestivalNotificationIsAllowed(
        festivalId: Long,
        isAllowed: Boolean,
    )

    fun getFestivalNotificationIsAllowed(festivalId: Long): Boolean
}

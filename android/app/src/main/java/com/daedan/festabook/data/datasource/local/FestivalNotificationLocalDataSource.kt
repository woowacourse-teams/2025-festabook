package com.daedan.festabook.data.datasource.local

interface FestivalNotificationLocalDataSource {
    fun saveFestivalNotificationId(
        festivalId: Long,
        festivalNotificationId: Long,
    )

    fun getFestivalNotificationId(festivalId: Long): Long

    fun deleteFestivalNotificationId(festivalId: Long)

    fun clearAll()

    fun saveFestivalNotificationIsAllowed(
        festivalId: Long,
        isAllowed: Boolean,
    )

    fun getFestivalNotificationIsAllowed(festivalId: Long): Boolean
}

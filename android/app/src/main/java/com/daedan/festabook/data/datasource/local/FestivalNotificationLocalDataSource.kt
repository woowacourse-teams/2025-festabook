package com.daedan.festabook.data.datasource.local

interface FestivalNotificationLocalDataSource {
    fun saveFestivalNotificationId(festivalNotificationId: Long)

    fun getFestivalNotificationId(): Long

    fun deleteFestivalNotificationId()

    fun clearAll()

    fun saveFestivalNotificationIsAllowed(isAllowed: Boolean)

    fun getFestivalNotificationIsAllowed(): Boolean

    companion object {
        const val KEY_FESTIVAL_NOTIFICATION_ID = "festival_notification_id"
        const val DEFAULT_FESTIVAL_NOTIFICATION_ID = -1L
        const val KEY_FESTIVAL_NOTIFICATION_IS_ALLOWED = "key_festival_notification_allowed"
    }
}

package com.daedan.festabook.data.datasource.local // 또는 data.repository 또는 domain.repository 등 적절한 위치

interface AppDataSource {
    fun saveUuid(uuid: String)

    fun getUuid(): String?

    fun saveFcmToken(token: String)

    fun getFcmToken(): String?

    fun saveDeviceId(deviceId: Long)

    fun getDeviceId(): Long

    fun saveFestivalNotificationId(festivalNotificationId: Long)

    fun getFestivalNotificationId(): Long

    fun deleteFestivalNotificationId()

    fun clearAll()

    fun saveFestivalNotificationIsAllowed(isAllowed: Boolean)

    fun getFestivalNotificationIsAllowed(): Boolean
}

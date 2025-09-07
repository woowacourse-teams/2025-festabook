package com.daedan.festabook.data.datasource.local

import android.content.SharedPreferences
import androidx.core.content.edit

class FestivalNotificationLocalDataSourceImpl(
    private val prefs: SharedPreferences,
) : FestivalNotificationLocalDataSource {
    override fun saveFestivalNotificationId(festivalNotificationId: Long) {
        prefs.edit { putLong(KEY_FESTIVAL_NOTIFICATION_ID, festivalNotificationId) }
    }

    override fun getFestivalNotificationId(): Long = prefs.getLong(KEY_FESTIVAL_NOTIFICATION_ID, DEFAULT_FESTIVAL_NOTIFICATION_ID)

    override fun deleteFestivalNotificationId() {
        prefs.edit { remove(KEY_FESTIVAL_NOTIFICATION_ID) }
    }

    override fun clearAll() {
        prefs.edit { clear() }
    }

    override fun saveFestivalNotificationIsAllowed(isAllowed: Boolean) {
        prefs.edit { putBoolean(KEY_FESTIVAL_NOTIFICATION_IS_ALLOWED, isAllowed) }
    }

    override fun getFestivalNotificationIsAllowed(): Boolean =
        prefs.getBoolean(
            KEY_FESTIVAL_NOTIFICATION_IS_ALLOWED,
            false,
        )

    companion object {
        private const val KEY_FESTIVAL_NOTIFICATION_ID = "festival_notification_id"
        private const val DEFAULT_FESTIVAL_NOTIFICATION_ID = -1L
        private const val KEY_FESTIVAL_NOTIFICATION_IS_ALLOWED = "key_festival_notification_allowed"
    }
}

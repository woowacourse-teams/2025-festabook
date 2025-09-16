package com.daedan.festabook.data.datasource.local

import android.content.SharedPreferences
import androidx.core.content.edit

class FestivalNotificationLocalDataSourceImpl(
    private val prefs: SharedPreferences,
) : FestivalNotificationLocalDataSource {
    override fun saveFestivalNotificationId(
        festivalId: Long,
        festivalNotificationId: Long,
    ) {
        prefs.edit { putLong("${KEY_FESTIVAL_NOTIFICATION_ID}_$festivalId", festivalNotificationId) }
    }

    override fun getFestivalNotificationId(festivalId: Long): Long =
        prefs.getLong("${KEY_FESTIVAL_NOTIFICATION_ID}_$festivalId", DEFAULT_FESTIVAL_NOTIFICATION_ID)

    override fun deleteFestivalNotificationId(festivalId: Long) {
        prefs.edit { remove("${KEY_FESTIVAL_NOTIFICATION_ID}_$festivalId") }
    }

    override fun clearAll() {
        prefs.edit { clear() }
    }

    override fun saveFestivalNotificationIsAllowed(
        festivalId: Long,
        isAllowed: Boolean,
    ) {
        prefs.edit {
            putBoolean(
                "${KEY_FESTIVAL_NOTIFICATION_IS_ALLOWED}_$festivalId",
                isAllowed,
            )
        }
    }

    override fun getFestivalNotificationIsAllowed(festivalId: Long): Boolean =
        prefs.getBoolean(
            "${KEY_FESTIVAL_NOTIFICATION_IS_ALLOWED}_$festivalId",
            false,
        )

    companion object {
        private const val KEY_FESTIVAL_NOTIFICATION_ID = "festival_notification_id"
        private const val DEFAULT_FESTIVAL_NOTIFICATION_ID = -1L
        private const val KEY_FESTIVAL_NOTIFICATION_IS_ALLOWED = "key_festival_notification_allowed"
    }
}

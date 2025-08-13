package com.daedan.festabook.data.datasource.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class AppPreferencesManager(
    context: Context,
) {
    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveFestivalId(festivalId: Long) {
        prefs.edit { putLong(KEY_FESTIVAL_ID, festivalId) }
    }

    fun getFestivalId(): Long? {
        val id = prefs.getLong(KEY_FESTIVAL_ID, DEFAULT_FESTIVAL_ID)
        return if (id == DEFAULT_FESTIVAL_ID) null else id
    }
    companion object {
        private const val PREFS_NAME = "app_prefs"
        private const val KEY_FESTIVAL_ID = "festival_id"
        private const val DEFAULT_FESTIVAL_ID = -1L

    }
}

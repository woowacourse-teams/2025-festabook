package com.daedan.festabook.data.datasource.local

import android.content.SharedPreferences
import androidx.core.content.edit

class FestivalLocalDataSourceImpl(
    private val prefs: SharedPreferences,
) : FestivalLocalDataSource {
    override fun saveFestivalId(festivalId: Long) {
        prefs.edit { putLong(KEY_FESTIVAL_ID, festivalId) }
    }

    override fun getFestivalId(): Long? {
        val id = prefs.getLong(KEY_FESTIVAL_ID, DEFAULT_FESTIVAL_ID)
        return if (id == DEFAULT_FESTIVAL_ID) null else id
    }

    companion object {
        private const val KEY_FESTIVAL_ID = "festival_id"
        private const val DEFAULT_FESTIVAL_ID = -1L
    }
}

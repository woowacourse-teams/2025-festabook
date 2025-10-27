package com.daedan.festabook.data.datasource.local

import android.content.SharedPreferences
import androidx.core.content.edit
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@ContributesBinding(AppScope::class)
class FestivalLocalDataSourceImpl @Inject constructor(
    private val prefs: SharedPreferences,
) : FestivalLocalDataSource {
    override fun saveFestivalId(festivalId: Long) {
        prefs.edit { putLong(KEY_FESTIVAL_ID, festivalId) }
    }

    override fun getFestivalId(): Long? {
        val id = prefs.getLong(KEY_FESTIVAL_ID, DEFAULT_FESTIVAL_ID)
        return if (id == DEFAULT_FESTIVAL_ID) null else id
    }

    override fun getIsFirstVisit(): Boolean {
        val festivalId = getFestivalId() ?: return true
        val isFirstVisit =
            prefs.getBoolean(
                "${KEY_IS_FIRST_VISIT}_$festivalId",
                true,
            )
        if (isFirstVisit) {
            prefs.edit { putBoolean("${KEY_IS_FIRST_VISIT}_$festivalId", false) }
        }
        return isFirstVisit
    }

    companion object {
        private const val KEY_IS_FIRST_VISIT = "is_first_visit"
        private const val KEY_FESTIVAL_ID = "festival_id"
        private const val DEFAULT_FESTIVAL_ID = -1L
    }
}

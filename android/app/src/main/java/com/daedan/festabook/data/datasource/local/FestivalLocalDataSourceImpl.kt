package com.daedan.festabook.data.datasource.local

import android.content.SharedPreferences
import androidx.core.content.edit
import com.daedan.festabook.data.datasource.local.FestivalLocalDataSource.Companion.DEFAULT_FESTIVAL_ID
import com.daedan.festabook.data.datasource.local.FestivalLocalDataSource.Companion.KEY_FESTIVAL_ID
import timber.log.Timber

class FestivalLocalDataSourceImpl(
    private val prefs: SharedPreferences,
) : FestivalLocalDataSource {
    override fun saveFestivalId(festivalId: Long) {
        Timber.d("festivalLocalDataSource - saveFestivalId: $festivalId")
        prefs.edit { putLong(KEY_FESTIVAL_ID, festivalId) }
    }

    override fun getFestivalId(): Long? {
        val id = prefs.getLong(KEY_FESTIVAL_ID, DEFAULT_FESTIVAL_ID)
        Timber.d("festivalLocalDataSource - getFestivalId: $id")
        return if (id == DEFAULT_FESTIVAL_ID) null else id
    }
}

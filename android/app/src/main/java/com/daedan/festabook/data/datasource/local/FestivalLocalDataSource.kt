package com.daedan.festabook.data.datasource.local

interface FestivalLocalDataSource {
    fun saveFestivalId(festivalId: Long)

    fun getFestivalId(): Long?

    companion object {
        const val KEY_FESTIVAL_ID = "festival_id"
        const val DEFAULT_FESTIVAL_ID = -1L
    }
}

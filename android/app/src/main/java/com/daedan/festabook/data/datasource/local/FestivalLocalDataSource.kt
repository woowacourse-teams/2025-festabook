package com.daedan.festabook.data.datasource.local

interface FestivalLocalDataSource {
    fun saveFestivalId(festivalId: Long)

    fun getFestivalId(): Long?

    fun getIsFirstVisit(): Boolean
}

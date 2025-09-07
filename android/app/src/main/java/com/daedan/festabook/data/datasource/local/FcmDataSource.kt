package com.daedan.festabook.data.datasource.local

interface FcmDataSource {
    fun saveFcmToken(token: String)

    fun getFcmToken(): String?
}

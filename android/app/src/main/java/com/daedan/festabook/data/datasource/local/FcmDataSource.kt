package com.daedan.festabook.data.datasource.local

interface FcmDataSource {
    fun saveFcmToken(token: String)

    fun getFcmToken(): String?

    companion object {
        const val KEY_FCM_TOKEN = "fcm_token"
    }
}

package com.daedan.festabook.logging

import android.content.Context
import android.os.Bundle
import com.daedan.festabook.FestaBookApp
import com.daedan.festabook.data.datasource.local.FestivalLocalDataSource
import com.daedan.festabook.data.datasource.local.FestivalNotificationLocalDataSource
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime

class DefaultFirebaseLogger(
    private val firebaseAnalytics: FirebaseAnalytics,
    private val festivalLocalDataSource: FestivalLocalDataSource,
    private val festivalNotificationLocalDataSource: FestivalNotificationLocalDataSource,
) {
    private var userId: String? = null
    private var sessionId: Long? = null

    init {
        CoroutineScope(Dispatchers.Default).launch {
            userId = firebaseAnalytics.appInstanceId.await()
            sessionId = firebaseAnalytics.sessionId.await()
        }
    }

    fun log(
        key: String,
        value: LogData,
    ) {
        firebaseAnalytics.logEvent(
            key,
            Bundle().apply {
                putParcelable(key, value)
                putParcelable(KEY_BASE_DATA, getBaseLogData())
            },
        )
    }

    fun getBaseLogData(): BaseLogData =
        BaseLogData(
            festivalId = festivalLocalDataSource.getFestivalId() ?: -1,
            notificationId = festivalNotificationLocalDataSource.getFestivalNotificationId(),
            deviceInfo = android.os.Build.MODEL,
            eventTime = LocalDateTime.now().toString(),
            userId = userId ?: KEY_UNINITIALIZED_USER_ID,
            sessionId = sessionId ?: KEY_UNINITIALIZED_SESSION_ID,
        )

    companion object {
        @Volatile
        private var INSTANCE: DefaultFirebaseLogger? = null

        private const val KEY_BASE_DATA = "base_data"
        private const val KEY_UNINITIALIZED_USER_ID = "undefined"
        private const val KEY_UNINITIALIZED_SESSION_ID = -1L

        fun getInstance(context: Context): DefaultFirebaseLogger =
            INSTANCE ?: synchronized(this) {
                val festivalLocalDataSource =
                    (context.applicationContext as FestaBookApp).appContainer.festivalLocalDataSource
                val festivalNotificationLocalDataSource =
                    (context.applicationContext as FestaBookApp)
                        .appContainer.festivalNotificationLocalDataSource
                val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
                INSTANCE ?: DefaultFirebaseLogger(
                    firebaseAnalytics,
                    festivalLocalDataSource,
                    festivalNotificationLocalDataSource,
                ).also { INSTANCE = it }
            }
    }
}

package com.daedan.festabook.logging

import android.content.Context
import android.os.Build
import com.daedan.festabook.BuildConfig
import com.daedan.festabook.FestaBookApp
import com.daedan.festabook.data.datasource.local.FestivalLocalDataSource
import com.daedan.festabook.data.datasource.local.FestivalNotificationLocalDataSource
import com.daedan.festabook.logging.model.CommonLogData
import com.daedan.festabook.logging.model.LogData
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

    fun log(value: LogData) {
        if (BuildConfig.DEBUG) return
        firebaseAnalytics.logEvent(
            value.javaClass.simpleName,
            value.writeToBundle()
        )
    }

    fun getBaseLogData() = CommonLogData(
        festivalId = festivalLocalDataSource.getFestivalId() ?: -1L,
        notificationId = festivalNotificationLocalDataSource.getFestivalNotificationId(),
        deviceInfo = Build.MODEL,
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

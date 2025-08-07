package com.daedan.festabook.util

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

class CrashlyticsTree : Timber.Tree() {
    private val crashlytics = FirebaseCrashlytics.getInstance()

    override fun log(
        priority: Int,
        tag: String?,
        message: String,
        t: Throwable?,
    ) {
        if (priority == Log.ERROR || priority == Log.WARN) {
            t?.run {
                crashlytics.recordException(t)
            }
            crashlytics.recordException(CrashlyticsLog(message))
        }
    }

    private class CrashlyticsLog(
        override val message: String,
    ) : RuntimeException()
}

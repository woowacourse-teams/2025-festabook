package com.daedan.festabook.util

import android.app.Application
import com.daedan.festabook.presentation.error.ErrorActivity
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlin.system.exitProcess

class FestabookGlobalExceptionHandler(
    private val application: Application,
    private val crashlytics: FirebaseCrashlytics,
) : Thread.UncaughtExceptionHandler {
    override fun uncaughtException(t: Thread, e: Throwable) {
        crashlytics.recordException(e)
        application.startActivity(
            ErrorActivity.newIntent(application)
        )
    }
}
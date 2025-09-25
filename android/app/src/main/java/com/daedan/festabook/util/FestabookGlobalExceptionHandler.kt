package com.daedan.festabook.util

import android.app.Application
import android.os.Process
import com.daedan.festabook.logging.DefaultFirebaseLogger
import com.daedan.festabook.presentation.error.ErrorActivity
import kotlin.system.exitProcess

class FestabookGlobalExceptionHandler(
    private val application: Application,
) : Thread.UncaughtExceptionHandler {
    override fun uncaughtException(t: Thread, e: Throwable) {
        application.startActivity(
            ErrorActivity.newIntent(application, e)
        )
        Process.killProcess(Process.myPid())
        exitProcess(-1)
    }
}
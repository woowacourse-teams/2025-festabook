package com.daedan.festabook.util

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Process
import com.daedan.festabook.presentation.common.showToast
import timber.log.Timber
import kotlin.system.exitProcess

class FestabookGlobalExceptionHandler(
    private val application: Application,
    private val defaultExceptionHandler: Thread.UncaughtExceptionHandler?,
) : Thread.UncaughtExceptionHandler {
    private var lastActivity: Activity? = null
    private var activityCount = 0

    init {
        application.registerActivityLifecycleCallbacks(
            object : Application.ActivityLifecycleCallbacks {
                override fun onActivityCreated(
                    activity: Activity,
                    savedInstanceState: Bundle?,
                ) {
                    activityCount++
                    lastActivity = activity
                }

                override fun onActivityDestroyed(activity: Activity) = Unit

                override fun onActivityPaused(activity: Activity) = Unit

                override fun onActivityResumed(activity: Activity) = Unit

                override fun onActivitySaveInstanceState(
                    activity: Activity,
                    outState: Bundle,
                ) = Unit

                override fun onActivityStarted(activity: Activity) = Unit

                override fun onActivityStopped(activity: Activity) {
                    activityCount--
                    if (activityCount == 0) {
                        lastActivity = null
                    }
                }
            },
        )
    }

    override fun uncaughtException(
        t: Thread,
        e: Throwable,
    ) {
        Timber.e(e, "FestabookGlobalExceptionHandler: ${e.message}")
        lastActivity?.run {
            startActivity(
                intent.apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                },
            )
            Handler(Looper.getMainLooper()).post {
                showToast(e.stackTraceToString())
            }

            finish()
        }
        defaultExceptionHandler?.uncaughtException(t, e)
        Process.killProcess(Process.myPid())
        exitProcess(-1)
    }
}

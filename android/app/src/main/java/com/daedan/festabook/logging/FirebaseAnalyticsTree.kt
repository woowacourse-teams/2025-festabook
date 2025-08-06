package com.daedan.festabook.logging

import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import timber.log.Timber

class FirebaseAnalyticsTree(
    private val analytics: FirebaseAnalytics,
) : Timber.Tree() {
    override fun log(
        priority: Int,
        tag: String?,
        message: String,
        t: Throwable?,
    ) {
        if (priority != Log.INFO) return

        if (message.startsWith("screen_stay:")) {
            setupFragmentStayLog(message)
            return
        }

        val bundle =
            Bundle().apply {
                putString("tag", tag ?: "NoTag")
                putString("message", message.take(MAX_MESSAGE_LENGTH))
                t?.let {
                    putString("exception", it.toString())
                }
            }
        analytics.logEvent("timber_info_log", bundle)
    }

    private fun setupFragmentStayLog(message: String) {
        val data =
            message
                .removePrefix("screen_stay:")
                .split(",")
                .associate {
                    val (key, value) = it.split("=")
                    key to value
                }

        val screen = data["screen"] ?: return
        val duration = data["duration_ms"]?.toLong() ?: return

        val bundle =
            Bundle().apply {
                putString("screen", screen)
                putLong("duration_ms", duration)
            }
        analytics.logEvent("screen_stay_time", bundle)
    }

    companion object {
        private const val MAX_MESSAGE_LENGTH = 100
    }
}

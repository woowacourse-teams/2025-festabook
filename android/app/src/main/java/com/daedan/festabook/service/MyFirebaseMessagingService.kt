package com.daedan.festabook.service

import com.daedan.festabook.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        Timber.d("Refreshed token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Timber.d("From: ${remoteMessage.from}")

        // remoteMessage.data
        if (remoteMessage.data.isNotEmpty()) {
            Timber.d("Data Payload: ${remoteMessage.data}")
            val title =
                remoteMessage.data["title"] ?: getString(R.string.default_notification_title)
            val content =
                remoteMessage.data["body"] ?: getString(R.string.default_notification_body)
            val noticeIdToExpand = remoteMessage.data["announcementId"] ?: "-1"

            NotificationHelper.showNotification(this, title, content, noticeIdToExpand)
        }
    }
}

package com.daedan.festabook.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        Timber.d("Refreshed token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Timber.d("From: ${remoteMessage.from}")

        // remoteMessage.notification
        remoteMessage.notification?.let { notification ->
            val title = notification.title
            val body = notification.body
            Timber.d("Notification Title: $title, Body: $body")

            NotificationHelper.showNotification(this, title, body)
        }

        // remoteMessage.data
        if (remoteMessage.data.isNotEmpty()) {
            Timber.d("Data Payload: ${remoteMessage.data}")
            val targetId = remoteMessage.data["targetId"]
            val customTitle = remoteMessage.data["custom_title"]
            val customMessage = remoteMessage.data["custom_message"]

            val displayTitle = remoteMessage.notification?.title ?: customTitle ?: "알림"
            val displayMessage =
                remoteMessage.notification?.body ?: customMessage ?: "새로운 정보가 있습니다."

            NotificationHelper.showNotification(this, displayTitle, displayMessage, targetId)
        }
    }
}

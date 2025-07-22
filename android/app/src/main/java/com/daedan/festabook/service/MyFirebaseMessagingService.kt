package com.daedan.festabook.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        Log.d("FCM_Token", "Refreshed token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("FCM_MESSAGE", "From: ${remoteMessage.from}")

        // remoteMessage.notification
        remoteMessage.notification?.let { notification ->
            val title = notification.title
            val body = notification.body
            Log.d("FCM_MESSAGE", "Notification Title: $title, Body: $body")

            NotificationHelper.showNotification(this, title, body)
        }

        // remoteMessage.data
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("FCM_MESSAGE", "Data Payload: ${remoteMessage.data}")
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

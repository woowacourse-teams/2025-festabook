package com.daedan.festabook

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    /**
     * 새로운 FCM 등록 토큰이 생성되거나 기존 토큰이 업데이트될 때 호출됩니다.
     * 이 토큰을 서버에 전송하여 저장해야 합니다.
     */
    override fun onNewToken(token: String) {
        Log.d("FCM_Token", "Refreshed token: $token")
        // TODO: 생성된 토큰을 앱 서버로 전송하는 로직을 여기에 구현합니다.
        // 보통 Retrofit 등을 사용하여 서버의 API에 이 토큰과 사용자 ID를 전송합니다.
        sendRegistrationToServer(token)
    }

    /**
     * FCM 메시지를 수신할 때 호출됩니다.
     * 앱이 포그라운드에 있을 때 수신되는 Notification 메시지 또는 Data 메시지를 처리합니다.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("FCM_Message", "From: ${remoteMessage.from}")

        // 메시지에 데이터 페이로드(payload)가 포함되어 있는지 확인합니다.
        remoteMessage.data.isNotEmpty().let {
            Log.d("FCM_Message", "Message data payload: " + remoteMessage.data)

            // TODO: 데이터 메시지 처리 로직을 여기에 구현합니다.
            // 예를 들어, 데이터 메시지에 따라 앱 내 특정 액션을 수행하거나,
            // 자체적으로 알림을 생성하여 표시할 수 있습니다.
        }

        // 메시지에 알림 페이로드(payload)가 포함되어 있는지 확인합니다.
        remoteMessage.notification?.let {
            Log.d("FCM_Message", "Message Notification Body: ${it.body}")

            // TODO: 알림 메시지 처리 로직을 여기에 구현합니다.
            // 앱이 포그라운드에 있을 때는 FCM SDK가 자동으로 알림을 표시하지 않으므로,
            // 여기서 NotificationManager를 사용하여 직접 알림을 생성하고 표시해야 합니다.
            // 앱이 백그라운드에 있을 때는 Notification 메시지가 자동으로 표시됩니다.
            showNotification(it.title, it.body)
        }
    }

    // --- 토큰 서버 전송 및 알림 표시 관련 Helper 함수 (예시) ---
    private fun sendRegistrationToServer(token: String) {
        // 실제 서버 통신 로직을 여기에 구현 (Retrofit 등)
        Log.d("FCM_Token", "Sending token to server: $token")
        // 예: ApiService.uploadFCMToken(userId, token)
    }

    private fun showNotification(
        title: String?,
        body: String?,
    ) {
        // 알림 채널 ID (Android 8.0 이상 필수)
        val channelId = "default_notification_channel"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder =
            NotificationCompat
                .Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_pin) // 알림 아이콘
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true) // 탭하면 자동으로 사라지게
                .setSound(defaultSoundUri) // 알림 소리

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Android 8.0 (API 레벨 26) 이상에서는 알림 채널을 생성해야 합니다.
        val channel =
            NotificationChannel(
                channelId,
                "기본 알림 채널", // 사용자에게 보여질 채널 이름
                NotificationManager.IMPORTANCE_DEFAULT, // 알림 중요도
            )
        notificationManager.createNotificationChannel(channel)

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }
}

package com.daedan.festabook.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.daedan.festabook.R

object NotificationHelper {
    private const val CHANNEL_ID = "notice_channel"
    private const val CHANNEL_NAME = "공지사항"
    private const val NOTIFICATION_ID = 1001

    // Android 8.0에서 알림 채널을 생성
    fun createNotificationChannel(context: Context) {
        val channel =
            NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = "앱 공지사항 알림 채널"
            }

        val notificationManager =
            context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    // 실제 알림을 생성
    fun showNotification(
        context: Context,
        title: String?,
        message: String?,
        targetId: String? = null,
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationBuilder =
            NotificationCompat
                .Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground) // 앱 아이콘
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true) // 클릭 시 자동 삭제
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }
}

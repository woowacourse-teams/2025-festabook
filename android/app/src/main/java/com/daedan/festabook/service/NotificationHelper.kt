package com.daedan.festabook.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.daedan.festabook.R
import com.daedan.festabook.presentation.common.vectorToBitmap
import com.daedan.festabook.presentation.main.MainActivity
import com.daedan.festabook.presentation.main.MainActivity.Companion.KEY_CAN_NAVIGATE_TO_NEWS
import com.daedan.festabook.presentation.main.MainActivity.Companion.KEY_NOTICE_ID_TO_EXPAND

object NotificationHelper {
    private const val CHANNEL_ID = "notice_channel"
    private const val CHANNEL_NAME = "공지사항"

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
        title: String,
        content: String,
        announcementId: String,
    ) {
        val intent =
            MainActivity.newIntent(context).apply {
                putExtra(KEY_CAN_NAVIGATE_TO_NEWS, true)
                putExtra(KEY_NOTICE_ID_TO_EXPAND, announcementId.toLongOrNull())
            }

        val pendingIntent =
            PendingIntent.getActivity(
                context,
                System.currentTimeMillis().toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        val notificationBuilder =
            NotificationCompat
                .Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_festabook_logo_notification_small)
                .setColor(ContextCompat.getColor(context, R.color.gray050))
                .setContentTitle(title)
                .setContentText(content)
                .setLargeIcon(
                    vectorToBitmap(context, R.drawable.ic_festabook_logo_notification_large),
                ).setStyle(NotificationCompat.BigTextStyle().bigText(content))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }
}

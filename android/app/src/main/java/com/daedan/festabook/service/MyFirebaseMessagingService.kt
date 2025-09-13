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
            val announcementId = remoteMessage.data["announcementId"] ?: "-1"

            NotificationHelper.showNotification(this, title, content, announcementId)
        }
    }
}

// fcm 메시지
// {
//    "message": {
//    "topic": "FCM이_사용하는_토픽_식별자",
//    "data": {
//    "title": "공지사항 제목_축제 공지 알림 ",
//    "body": "공지사항 본문 내용_오늘 연예인 공연은 오후 6시부터 시작합니다.",
//    "festivalId": "100",
//    "announcementId": "1"
// }
// }
// }

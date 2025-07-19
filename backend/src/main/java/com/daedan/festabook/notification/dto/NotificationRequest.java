package com.daedan.festabook.notification.dto;

public record NotificationRequest(
        String topic,       // FCM 전송 대상 토픽명 / ex) TopicConstants.place(1)
        String title,       // 알림 제목 / ex) 아이유 공연 시간 변경 안내
        String body         // 알림 본문 / ex) 15시 -> 17시로 변경되었습니다.
) {
}

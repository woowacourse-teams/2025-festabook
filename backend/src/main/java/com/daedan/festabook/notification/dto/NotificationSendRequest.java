package com.daedan.festabook.notification.dto;

import com.daedan.festabook.global.exception.BusinessException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

public class NotificationSendRequest {

    private final String title;
    private final String body;
    private final Map<String, String> data;

    private NotificationSendRequest(String title, String body, Map<String, String> data) {
        validateTitle(title);
        validateBody(body);

        this.title = title;
        this.body = body;
        this.data = new HashMap<>(data);
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getCustomData(String key) {
        String value = data.get(key);
        if (value == null) {
            throw new BusinessException(
                    String.format("Key: [%s]에 해당하는 값이 존재하지 않습니다.", key),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
        return value;
    }

    private void validateTitle(String title) {
        if (!StringUtils.hasText(title)) {
            throw new BusinessException("알림 제목은 비어있을 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateBody(String title) {
        if (!StringUtils.hasText(title)) {
            throw new BusinessException("알림 본문은 비어있을 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String title;
        private String body;
        private Map<String, String> data = new HashMap<>();

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public Builder putData(String key, String value) {
            this.data.put(key, value);
            return this;
        }

        public NotificationSendRequest build() {
            return new NotificationSendRequest(title, body, data);
        }
    }
}

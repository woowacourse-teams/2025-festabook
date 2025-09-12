package com.daedan.festabook.notification.dto;

import com.daedan.festabook.global.exception.BusinessException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;

public class NotificationSendRequest {

    private String title;
    private String body;
    private Map<String, String> data;

    private NotificationSendRequest() {
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
            NotificationSendRequest request = new NotificationSendRequest();
            request.title = this.title;
            request.body = this.body;
            request.data = this.data;
            return request;
        }
    }
}

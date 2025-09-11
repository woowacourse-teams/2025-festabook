package com.daedan.festabook.notification.dto;

import java.util.HashMap;
import java.util.Map;

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

    public Map<String, String> getData() {
        return data;
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

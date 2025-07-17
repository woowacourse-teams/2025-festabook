package com.daedan.festabook.notification.constants;

public class TopicConstants {

    public static final String ORGANIZATION_TOPIC_PREFIX = "notifications-organization-";
    public static final String PLACE_TOPIC_PREFIX = "notifications-place-";

    public static String getOrganizationTopicById(Long organizationId) {
        return ORGANIZATION_TOPIC_PREFIX + organizationId;
    }

    public static String getPlaceTopicById(Long placeId) {
        return PLACE_TOPIC_PREFIX + placeId;
    }
}

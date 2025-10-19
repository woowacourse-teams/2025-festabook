CREATE UNIQUE INDEX uix_active_subscription
    ON festival_notification (festival_id, active_device_id);

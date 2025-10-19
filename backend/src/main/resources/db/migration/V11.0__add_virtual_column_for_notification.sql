-- festival_notification 테이블에 가상 컬럼 `active_device_id`를 추가합니다.
-- 이 컬럼은 `deleted` 상태에 따라 device_id 또는 NULL 값을 가지며,
-- 이후 생성될 UNIQUE 인덱스의 기반이 됩니다.

ALTER TABLE festival_notification
    ADD COLUMN active_device_id BIGINT AS (IF(deleted = false, device_id, NULL)) VIRTUAL;

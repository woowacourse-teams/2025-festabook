CREATE TABLE announcement
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    festival_id BIGINT        NOT NULL,
    title       VARCHAR(50)   NOT NULL,
    content     VARCHAR(1000) NOT NULL,
    is_pinned   BIT(1)        NOT NULL,
    created_at  dateTime(6)      NOT NULL,
    CONSTRAINT pk_announcement PRIMARY KEY (id)
);

CREATE TABLE device
(
    id                BIGINT AUTO_INCREMENT NOT NULL,
    device_identifier VARCHAR(255) NOT NULL,
    fcm_token         VARCHAR(255) NOT NULL,
    CONSTRAINT pk_device PRIMARY KEY (id)
);

CREATE TABLE event
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    event_date_id BIGINT       NOT NULL,
    start_time    time         NOT NULL,
    end_time      time         NOT NULL,
    title         VARCHAR(255) NOT NULL,
    location      VARCHAR(255) NOT NULL,
    CONSTRAINT pk_event PRIMARY KEY (id)
);

CREATE TABLE event_date
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    festival_id BIGINT NOT NULL,
    date        date   NOT NULL,
    CONSTRAINT pk_eventdate PRIMARY KEY (id)
);

CREATE TABLE festival
(
    id              BIGINT AUTO_INCREMENT NOT NULL,
    university_name VARCHAR(255) NOT NULL,
    festival_name   VARCHAR(255) NOT NULL,
    start_date      date         NOT NULL,
    end_date        date         NOT NULL,
    zoom            INT          NOT NULL,
    latitude DOUBLE NULL,
    longitude DOUBLE NULL,
    CONSTRAINT pk_festival PRIMARY KEY (id)
);

CREATE TABLE festival_image
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    festival_id BIGINT       NOT NULL,
    image_url   VARCHAR(255) NOT NULL,
    sequence    INT          NOT NULL,
    CONSTRAINT pk_festivalimage PRIMARY KEY (id)
);

CREATE TABLE festival_notification
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    festival_id BIGINT NOT NULL,
    device_id   BIGINT NOT NULL,
    CONSTRAINT pk_festivalnotification PRIMARY KEY (id)
);

CREATE TABLE festival_polygon_hole_boundary
(
    festival_id BIGINT NOT NULL,
    latitude DOUBLE NULL,
    longitude DOUBLE NULL
);

CREATE TABLE lost_item
(
    id               BIGINT AUTO_INCREMENT NOT NULL,
    festival_id      BIGINT       NOT NULL,
    image_url        VARCHAR(255) NOT NULL,
    storage_location VARCHAR(20)  NOT NULL,
    status           VARCHAR(255) NOT NULL,
    created_at       dateTime(6)     NOT NULL,
    CONSTRAINT pk_lostitem PRIMARY KEY (id)
);

CREATE TABLE place
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    festival_id   BIGINT       NOT NULL,
    category      VARCHAR(255) NOT NULL,
    title         VARCHAR(20)  NOT NULL,
    `description` VARCHAR(100) NULL,
    location      VARCHAR(100) NULL,
    host          VARCHAR(100) NULL,
    start_time    time NULL,
    end_time      time NULL,
    latitude DOUBLE NULL,
    longitude DOUBLE NULL,
    CONSTRAINT pk_place PRIMARY KEY (id)
);

CREATE TABLE place_announcement
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    place_id   BIGINT       NOT NULL,
    title      VARCHAR(255) NOT NULL,
    content    VARCHAR(255) NOT NULL,
    created_at dateTime(6)     NOT NULL,
    CONSTRAINT pk_placeannouncement PRIMARY KEY (id)
);

CREATE TABLE place_favorite
(
    id        BIGINT AUTO_INCREMENT NOT NULL,
    place_id  BIGINT NOT NULL,
    device_id BIGINT NOT NULL,
    CONSTRAINT pk_placefavorite PRIMARY KEY (id)
);

CREATE TABLE place_image
(
    id        BIGINT AUTO_INCREMENT NOT NULL,
    place_id  BIGINT       NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    sequence  INT          NOT NULL,
    CONSTRAINT pk_placeimage PRIMARY KEY (id)
);

CREATE TABLE question
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    festival_id BIGINT       NOT NULL,
    question    VARCHAR(255) NOT NULL,
    answer      VARCHAR(255) NOT NULL,
    sequence    INT          NOT NULL,
    CONSTRAINT pk_question PRIMARY KEY (id)
);

ALTER TABLE announcement
    ADD CONSTRAINT FK_ANNOUNCEMENT_ON_FESTIVAL FOREIGN KEY (festival_id) REFERENCES festival (id);

ALTER TABLE event_date
    ADD CONSTRAINT FK_EVENTDATE_ON_FESTIVAL FOREIGN KEY (festival_id) REFERENCES festival (id);

ALTER TABLE event
    ADD CONSTRAINT FK_EVENT_ON_EVENTDATE FOREIGN KEY (event_date_id) REFERENCES event_date (id);

ALTER TABLE festival_image
    ADD CONSTRAINT FK_FESTIVALIMAGE_ON_FESTIVAL FOREIGN KEY (festival_id) REFERENCES festival (id);

ALTER TABLE festival_notification
    ADD CONSTRAINT FK_FESTIVALNOTIFICATION_ON_DEVICE FOREIGN KEY (device_id) REFERENCES device (id);

ALTER TABLE festival_notification
    ADD CONSTRAINT FK_FESTIVALNOTIFICATION_ON_FESTIVAL FOREIGN KEY (festival_id) REFERENCES festival (id);

ALTER TABLE lost_item
    ADD CONSTRAINT FK_LOSTITEM_ON_FESTIVAL FOREIGN KEY (festival_id) REFERENCES festival (id);

ALTER TABLE place_announcement
    ADD CONSTRAINT FK_PLACEANNOUNCEMENT_ON_PLACE FOREIGN KEY (place_id) REFERENCES place (id);

ALTER TABLE place_favorite
    ADD CONSTRAINT FK_PLACEFAVORITE_ON_DEVICE FOREIGN KEY (device_id) REFERENCES device (id);

ALTER TABLE place_favorite
    ADD CONSTRAINT FK_PLACEFAVORITE_ON_PLACE FOREIGN KEY (place_id) REFERENCES place (id);

ALTER TABLE place_image
    ADD CONSTRAINT FK_PLACEIMAGE_ON_PLACE FOREIGN KEY (place_id) REFERENCES place (id);

ALTER TABLE place
    ADD CONSTRAINT FK_PLACE_ON_FESTIVAL FOREIGN KEY (festival_id) REFERENCES festival (id);

ALTER TABLE question
    ADD CONSTRAINT FK_QUESTION_ON_FESTIVAL FOREIGN KEY (festival_id) REFERENCES festival (id);

ALTER TABLE festival_polygon_hole_boundary
    ADD CONSTRAINT fk_festival_polygon_hole_boundary_on_festival FOREIGN KEY (festival_id) REFERENCES festival (id);

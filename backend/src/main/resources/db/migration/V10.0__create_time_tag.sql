CREATE TABLE `time_tag`
(
    `id`          BIGINT      NOT NULL AUTO_INCREMENT,
    `created_at`  datetime(6) NOT NULL,
    `updated_at`  datetime(6) NOT NULL,
    `deleted`     BIT(1)      NOT NULL,
    `deleted_at`  datetime(6) NULL,

    `festival_id` BIGINT      NOT NULL,
    `name`        VARCHAR(40) NOT NULL,

    CONSTRAINT `pk_time_tag` PRIMARY KEY (`id`),
    CONSTRAINT `fk_time_tag_festival` FOREIGN KEY (`festival_id`) REFERENCES `festival` (`id`)
);

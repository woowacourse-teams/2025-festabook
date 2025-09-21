CREATE TABLE `time_tag`
(
    `id`          BIGINT      NOT NULL AUTO_INCREMENT,
    `festival_id` BIGINT      NOT NULL,
    `name`        VARCHAR(40) NOT NULL,
    CONSTRAINT `pk_time_tag` PRIMARY KEY (`id`)
);

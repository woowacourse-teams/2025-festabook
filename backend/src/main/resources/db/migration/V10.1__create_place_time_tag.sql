CREATE TABLE `place_time_tag`
(
    `id`          BIGINT NOT NULL AUTO_INCREMENT,
    `created_at`  datetime(6)   NOT NULL,
    `updated_at`  datetime(6)   NOT NULL,
    `deleted`     BIT(1) NOT NULL,
    `deleted_at`  datetime(6)   NULL,

    `place_id`    BIGINT NOT NULL,
    `time_tag_id` BIGINT NOT NULL,

    CONSTRAINT `pk_place_time_tag` PRIMARY KEY (`id`),
    CONSTRAINT `fk_place_time_tag_place` FOREIGN KEY (`place_id`) REFERENCES `place` (`id`),
    CONSTRAINT `fk_place_time_tag_time_tag` FOREIGN KEY (`time_tag_id`) REFERENCES `time_tag` (`id`)
);

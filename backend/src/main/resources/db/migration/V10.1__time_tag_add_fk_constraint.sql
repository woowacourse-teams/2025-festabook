ALTER TABLE `time_tag`
    ADD CONSTRAINT `FK_TIME_TAG_FESTIVAL` FOREIGN KEY (`festival_id`) REFERENCES `festival` (`id`);

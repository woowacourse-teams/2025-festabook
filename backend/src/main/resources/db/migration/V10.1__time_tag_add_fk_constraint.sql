ALTER TABLE `time_tag`
    ADD CONSTRAINT `fk_time_tag_festival` FOREIGN KEY (`festival_id`) REFERENCES `festival` (`id`);

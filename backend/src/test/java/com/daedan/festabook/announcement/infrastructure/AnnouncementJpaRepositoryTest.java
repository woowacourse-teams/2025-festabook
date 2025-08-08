package com.daedan.festabook.announcement.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import com.daedan.festabook.announcement.domain.Announcement;
import com.daedan.festabook.announcement.domain.AnnouncementFixture;
import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AnnouncementJpaRepositoryTest {

    @Autowired
    private FestivalJpaRepository festivalJpaRepository;

    @Autowired
    private AnnouncementJpaRepository announcementJpaRepository;

    @Nested
    class countByFestivalIdAndPinned {

        @Test
        void 성공_특정_축제의_고정_공지사항_개수_반환() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            int pinnedSize = 3;
            int unpinnedSize = 2;
            Long expectedPinnedCount = 3L;

            List<Announcement> pinnedAnnouncements = AnnouncementFixture.createList(pinnedSize, true, festival);
            List<Announcement> notPinnedAnnouncements = AnnouncementFixture.createList(unpinnedSize, false,
                    festival);

            announcementJpaRepository.saveAll(pinnedAnnouncements);
            announcementJpaRepository.saveAll(notPinnedAnnouncements);

            // when
            Long result = announcementJpaRepository.countByFestivalIdAndIsPinnedTrue(festival.getId());

            // then
            assertThat(result).isEqualTo(expectedPinnedCount);
        }

        @Test
        void 성공_특정_축제의_고정_공지사항이_없는_경우() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            // when
            Long result = announcementJpaRepository.countByFestivalIdAndIsPinnedTrue(festival.getId());

            // then
            assertThat(result).isZero();
        }
    }
}

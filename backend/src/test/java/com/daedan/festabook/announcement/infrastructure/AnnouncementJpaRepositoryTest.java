package com.daedan.festabook.announcement.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import com.daedan.festabook.announcement.domain.Announcement;
import com.daedan.festabook.announcement.domain.AnnouncementFixture;
import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationFixture;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class AnnouncementJpaRepositoryTest {

    @Autowired
    private OrganizationJpaRepository organizationJpaRepository;

    @Autowired
    private AnnouncementJpaRepository announcementJpaRepository;

    @Nested
    class countByOrganizationIdAndPinned {

        @Test
        void 성공_특정_조직의_고정_공지사항_개수_반환() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Long expectedPinned = 3L;
            List<Announcement> pinnedAnnouncements = AnnouncementFixture.createList(expectedPinned.intValue(), true,
                    organization);
            List<Announcement> notPinnedAnnouncements = AnnouncementFixture.createList(2, false, organization);

            announcementJpaRepository.saveAll(pinnedAnnouncements);
            announcementJpaRepository.saveAll(notPinnedAnnouncements);

            // when
            Long result = announcementJpaRepository.countByOrganizationIdAndIsPinnedTrue(organization.getId());

            // then
            assertThat(result).isEqualTo(expectedPinned);
        }

        @Test
        void 성공_특정_조직의_고정_공지사항이_없는_경우() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            // when
            Long result = announcementJpaRepository.countByOrganizationIdAndIsPinnedTrue(organization.getId());

            // then
            assertThat(result).isEqualTo(0L);
        }
    }
}

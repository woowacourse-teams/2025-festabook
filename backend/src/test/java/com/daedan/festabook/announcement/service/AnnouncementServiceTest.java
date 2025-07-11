package com.daedan.festabook.announcement.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.daedan.festabook.announcement.dto.AnnouncementResponses;
import com.daedan.festabook.announcement.infrastructure.AnnouncementJpaRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AnnouncementServiceTest {

    @Mock
    private AnnouncementJpaRepository announcementJpaRepository;

    @InjectMocks
    private AnnouncementService announcementService;

    @Nested
    class findAllAnnouncement {

        @Test
        void 성공_빈컬렉션() {
            // given
            given(announcementJpaRepository.findAll()).willReturn(List.of());

            AnnouncementResponses expected = new AnnouncementResponses(List.of());

            // when
            AnnouncementResponses result = announcementService.getAllAnnouncement();

            // then
            assertThat(result).isEqualTo(expected);
        }
    }
}

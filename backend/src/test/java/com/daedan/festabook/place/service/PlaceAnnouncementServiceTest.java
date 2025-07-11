package com.daedan.festabook.place.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.daedan.festabook.place.domain.PlaceAnnouncement;
import com.daedan.festabook.place.domain.PlaceAnnouncementFixture;
import com.daedan.festabook.place.dto.PlaceAnnouncementResponses;
import com.daedan.festabook.place.infrastructure.PlaceAnnouncementJpaRepository;
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
class PlaceAnnouncementServiceTest {

    @Mock
    private PlaceAnnouncementJpaRepository placeAnnouncementJpaRepository;

    @InjectMocks
    private PlaceAnnouncementService placeAnnouncementService;

    @Nested
    class getAllPlaceAnnouncementByPlaceId {

        @Test
        void 성공() {
            // given
            Long id = 1L;
            PlaceAnnouncement placeAnnouncement1 = PlaceAnnouncementFixture.create();
            PlaceAnnouncement placeAnnouncement2 = PlaceAnnouncementFixture.create();
            PlaceAnnouncement placeAnnouncement3 = PlaceAnnouncementFixture.create();

            given(placeAnnouncementJpaRepository.findAllByPlaceId(id))
                    .willReturn(List.of(placeAnnouncement1, placeAnnouncement2, placeAnnouncement3));

            // when
            PlaceAnnouncementResponses result = placeAnnouncementService.getAllPlaceAnnouncementByPlaceId(id);

            // then
            assertThat(result.responses()).hasSize(3);
        }
    }
}

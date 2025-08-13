package com.daedan.festabook.place.service;

import static org.mockito.BDDMockito.then;

import com.daedan.festabook.place.infrastructure.PlaceAnnouncementJpaRepository;
import com.daedan.festabook.place.infrastructure.PlaceJpaRepository;
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
    private PlaceJpaRepository placeJpaRepository;

    @Mock
    private PlaceAnnouncementJpaRepository placeAnnouncementJpaRepository;

    @InjectMocks
    private PlaceAnnouncementService placeAnnouncementService;

    @Nested
    class deleteByPlaceAnnouncementId {

        @Test
        void 성공() {
            // given
            Long placeAnnouncementId = 1L;

            // when
            placeAnnouncementService.deleteByPlaceAnnouncementId(placeAnnouncementId);

            // then
            then(placeAnnouncementJpaRepository).should()
                    .deleteById(placeAnnouncementId);
        }
    }
}

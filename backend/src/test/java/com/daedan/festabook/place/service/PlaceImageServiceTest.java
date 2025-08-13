package com.daedan.festabook.place.service;

import static org.mockito.BDDMockito.then;

import com.daedan.festabook.place.infrastructure.PlaceImageJpaRepository;
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
public class PlaceImageServiceTest {

    @Mock
    private PlaceJpaRepository placeJpaRepository;

    @Mock
    private PlaceImageJpaRepository placeImageJpaRepository;

    @InjectMocks
    private PlaceImageService placeImageService;

    @Nested
    class deletePlaceImageByPlaceImageId {

        @Test
        void 성공() {
            // given
            Long placeImageId = 1L;

            // when
            placeImageService.deletePlaceImageByPlaceImageId(placeImageId);

            // then
            then(placeImageJpaRepository).should()
                    .deleteById(placeImageId);
        }
    }
}

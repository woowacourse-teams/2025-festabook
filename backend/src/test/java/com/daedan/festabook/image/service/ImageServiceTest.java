package com.daedan.festabook.image.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.daedan.festabook.image.domain.ImageManager;
import com.daedan.festabook.image.dto.ImageResponse;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ImageServiceTest {

    @Mock
    private ImageManager imageManager;

    @InjectMocks
    private ImageService imageService;

    @Nested
    class uploadImage {

        @Test
        void 성공() {
            // given
            String imageName = "비타민";
            byte[] imageByteValue = {};
            MockMultipartFile image = new MockMultipartFile(imageName, imageByteValue);

            String imageUrl = "https://example.org/image/48329082390482309";

            given(imageManager.upload(image))
                    .willReturn(imageUrl);

            // when
            ImageResponse imageResponse = imageService.uploadImage(image);

            // then
            assertThat(imageResponse.imageUrl()).isEqualTo(imageUrl);
        }
    }
}

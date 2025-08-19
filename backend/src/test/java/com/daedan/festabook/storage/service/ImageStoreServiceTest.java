package com.daedan.festabook.storage.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.storage.dto.ImageUploadResponse;
import com.daedan.festabook.storage.infrastructure.MockStorageManager;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ImageStoreServiceTest {

    private final ImageStoreService imageStoreService;

    public ImageStoreServiceTest() {
        this.imageStoreService = new ImageStoreService(new MockStorageManager());
        ReflectionTestUtils.setField(imageStoreService, "maxImageSize", 10 * 1024 * 1024);
    }

    @Nested
    class UploadImage {

        @Test
        void 성공() {
            // given
            MockMultipartFile file = new MockMultipartFile(
                    "image",
                    "sample.png",
                    "image/png",
                    new byte[100]
            );

            // when
            ImageUploadResponse response = imageStoreService.uploadImage(file);

            // then
            assertThat(response.url()).isNotBlank();
        }
    }

    @Nested
    class ValidateFile {

        @Test
        void 예외_null_파일() {
            // when & then
            assertThatThrownBy(() -> imageStoreService.uploadImage(null))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("파일은 비어있을 수 없습니다.");
        }

        @Test
        void 예외_빈_파일() {
            // given
            MockMultipartFile emptyFile = new MockMultipartFile(
                    "image",
                    "sample.png",
                    "image/png",
                    new byte[0]
            );

            // when & then
            assertThatThrownBy(() -> imageStoreService.uploadImage(emptyFile))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("파일은 비어있을 수 없습니다.");
        }
    }

    @Nested
    class ValidateImageSize {

        @Test
        void 예외_이미지_크기_초과() {
            // given
            int maxImageSize = 10 * 1024 * 1024; // 10MB
            byte[] largeBytes = new byte[maxImageSize + 1];
            MockMultipartFile file = new MockMultipartFile(
                    "image",
                    "sample.png",
                    "image/png",
                    largeBytes
            );

            // when & then
            assertThatThrownBy(() -> imageStoreService.uploadImage(file))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(String.format("이미지 크기는 %d 바이트를 초과할 수 없습니다.", maxImageSize));
        }
    }

    @Nested
    class ValidateImageType {

        @Test
        void 예외_null_콘텐츠타입() {
            // given
            MockMultipartFile file = new MockMultipartFile(
                    "image",
                    "sample.png",
                    null,
                    new byte[10]
            );

            // when & then
            assertThatThrownBy(() -> imageStoreService.uploadImage(file))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Content-Type이 빈 파일은 업로드 할 수 없습니다.");
        }

        @Test
        void 예외_image_아닌_콘텐츠타입() {
            // given
            MockMultipartFile file = new MockMultipartFile(
                    "image",
                    "sample.txt",
                    "text/plain",
                    new byte[10]
            );

            // when & then
            assertThatThrownBy(() -> imageStoreService.uploadImage(file))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("이미지 파일만 업로드할 수 있습니다. 업로드 파일 타입: text/plain");
        }
    }

    @Nested
    class GenerateUniqueFilename {

        @Test
        void 예외_null_파일이름() {
            // given
            MockMultipartFile file = new MockMultipartFile(
                    "image",
                    null,
                    "image/png",
                    new byte[10]
            );

            // when & then
            assertThatThrownBy(() -> imageStoreService.uploadImage(file))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("파일 확장자가 없는 파일은 업로드 할 수 없습니다.");
        }

        @Test
        void 예외_확장자_없는_파일이름() {
            // given
            MockMultipartFile file = new MockMultipartFile(
                    "image",
                    "noExtension",
                    "image/png",
                    new byte[10]
            );

            // when & then
            assertThatThrownBy(() -> imageStoreService.uploadImage(file))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("파일 확장자가 없는 파일은 업로드 할 수 없습니다.");
        }
    }
}

package com.daedan.festabook.storage.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.then;

import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.storage.domain.StorageManager;
import com.daedan.festabook.storage.dto.ImageUploadResponse;
import com.daedan.festabook.storage.dto.StorageUploadRequest;
import com.daedan.festabook.storage.infrastructure.MockStorageManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ImageStoreServiceTest {

    @Captor
    private ArgumentCaptor<StorageUploadRequest> requestCaptor;

    @Spy
    private StorageManager mockStorageManager = new MockStorageManager();

    @InjectMocks
    private ImageStoreService imageStoreService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(imageStoreService, "maxImageSize", 10 * 1024 * 1024);
        ReflectionTestUtils.setField(imageStoreService, "imagePathPrefix", "images");
    }

    @Nested
    class uploadImage {

        @Test
        void 성공() {
            // given
            MockMultipartFile file = new MockMultipartFile(
                    "image",
                    "sample.png",
                    "image/png",
                    new byte[100]
            );
            String expectedStoragePathPrefix = "images/";

            // when
            ImageUploadResponse response = imageStoreService.uploadImage(file);

            // then
            then(mockStorageManager).should()
                    .uploadFile(requestCaptor.capture());
            StorageUploadRequest captureRequest = requestCaptor.getValue();
            assertSoftly(s -> {
                s.assertThat(response.imageUrl()).isNotBlank();
                s.assertThat(captureRequest.storagePath()).startsWith(expectedStoragePathPrefix);
            });
        }
    }

    @Nested
    class validateFile {

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
    class validateImageSize {

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
    class validateImageType {

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

        @ParameterizedTest(name = "originalFilename: {0}, contentType: {1}")
        @CsvSource({
                "sample.svg, image/svg+xml",
                "sample.pdf, application/pdf",
                "sample.text, text/plain",
                "sample.txt, text/plain",
                "sample.json, application/json",
                "sample.mp4, video/mp4",
                "sample.mp3, audio/mpeg",
                "sample.zip, application/zip"
        })
        void 예외_image_아닌_콘텐츠타입(String originalFilename, String contentType) {
            // given
            MockMultipartFile file = new MockMultipartFile(
                    "image",
                    originalFilename,
                    contentType,
                    new byte[10]
            );

            // when & then
            assertThatThrownBy(() -> imageStoreService.uploadImage(file))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageStartingWith("이미지 파일만 업로드할 수 있습니다. 업로드 파일 타입:");
        }
    }

    @Nested
    class generateUniqueFilename {

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

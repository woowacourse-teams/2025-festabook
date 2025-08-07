package com.daedan.festabook.storage.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;

import com.daedan.festabook.global.exception.BusinessException;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class S3StorageServiceTest {

    private static final String BUCKET_NAME = "test-bucket";
    private static final String BASE_PATH = "test";

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private S3Client s3Client;

    @InjectMocks
    private S3StorageService s3StorageService;

    @BeforeEach
    void setUp() {
        s3StorageService = new S3StorageService(s3Client, BASE_PATH, BUCKET_NAME);
    }

    @Nested
    class uploadFile {

        @Test
        void 성공() {
            // given
            String fileName = "test_123.jpg";
            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    fileName,
                    "image/jpeg",
                    "test content".getBytes()
            );

            String mockRegion = "mock-region";
            given(s3Client.serviceClientConfiguration().region().id())
                    .willReturn(mockRegion);
            String expectedFileUrl = String.format(
                    "https://%s.s3.%s.amazonaws.com/%s/test_123.jpg",
                    BUCKET_NAME,
                    mockRegion,
                    BASE_PATH
            );

            // when
            String result = s3StorageService.uploadFile(mockFile, fileName);

            // then
            assertThat(result).isEqualTo(expectedFileUrl);
            then(s3Client).should()
                    .putObject(any(PutObjectRequest.class), any(RequestBody.class));
        }

        @Test
        void 예외_빈_파일_업로드() {
            // given
            String fileName = "test_123.jpg";
            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    fileName,
                    "image/jpeg",
                    new byte[0]
            );

            // when & then
            assertThatThrownBy(() -> s3StorageService.uploadFile(mockFile, fileName))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("파일이 비어 있습니다.");
            then(s3Client).shouldHaveNoInteractions();
        }

        @ParameterizedTest(name = "fileName: {0}")
        @NullAndEmptySource
        void 예외_빈_파일명(String fileName) {
            // given
            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    fileName,
                    "image/jpeg",
                    "test content".getBytes()
            );

            // when & then
            assertThatThrownBy(() -> s3StorageService.uploadFile(mockFile, fileName))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("파일 이름이 비어 있습니다.");
            then(s3Client).shouldHaveNoInteractions();
        }

        @Test
        void 예외_길이_제한_초과_파일명() {
            // given
            int maxFileNameLength = 255;
            String fileName = "a".repeat(maxFileNameLength + 1);
            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    fileName,
                    "image/jpeg",
                    "test content".getBytes()
            );

            // when & then
            assertThatThrownBy(() -> s3StorageService.uploadFile(mockFile, fileName))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("파일명이 너무 깁니다.");
            then(s3Client).shouldHaveNoInteractions();
        }

        @ParameterizedTest(name = "fileName: {0}")
        @ValueSource(strings = {"../test_123.jpg", "..test_123.jpg", "test/123.jpg", "test\\123.jpg"})
        void 예외_사용불가_문자_사용한_파일명(String fileName) {
            // given
            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    fileName,
                    "image/jpeg",
                    "test content".getBytes()
            );

            // when & then
            assertThatThrownBy(() -> s3StorageService.uploadFile(mockFile, fileName))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("허용되지 않는 파일명입니다.");
            then(s3Client).shouldHaveNoInteractions();
        }

        @Test
        void 예외_S3Exception_발생시_BusinessException으로_변환() {
            // given
            String fileName = "test_123.jpg";
            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    fileName,
                    "image/jpeg",
                    "test content".getBytes()
            );

            willThrow(S3Exception.class)
                    .given(s3Client)
                    .putObject(any(PutObjectRequest.class), any(RequestBody.class));

            // when & then
            assertThatThrownBy(() -> s3StorageService.uploadFile(mockFile, fileName))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("S3 업로드 실패");
        }

        @Test
        void 예외_IOException_발생시_BusinessException으로_변환() throws IOException {
            // given
            String fileName = "test_123.jpg";
            MockMultipartFile mockFile = mock(MockMultipartFile.class);
            given(mockFile.isEmpty()).willReturn(false);
            given(mockFile.getSize()).willReturn(1024L);
            given(mockFile.getContentType()).willReturn("image/jpeg");
            willThrow(IOException.class)
                    .given(mockFile)
                    .getBytes();

            // when & then
            assertThatThrownBy(() -> s3StorageService.uploadFile(mockFile, fileName))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("파일 처리 실패");
        }
    }
}

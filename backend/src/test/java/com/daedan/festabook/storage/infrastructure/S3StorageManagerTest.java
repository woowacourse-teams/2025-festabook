package com.daedan.festabook.storage.infrastructure;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.storage.dto.StorageUploadRequest;
import com.daedan.festabook.storage.dto.StorageUploadResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class S3StorageManagerTest {

    private static final String BUCKET_NAME = "test-bucket";
    private static final String BASE_PATH = "test";

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private S3Client s3Client;

    @InjectMocks
    private S3StorageManager s3StorageManager;

    @BeforeEach
    void setUp() {
        s3StorageManager = new S3StorageManager(s3Client, BASE_PATH, BUCKET_NAME);
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
            String expectedS3Key = String.format("%s/%s", BASE_PATH, fileName);

            StorageUploadRequest request = new StorageUploadRequest(mockFile, fileName);

            // when
            StorageUploadResponse result = s3StorageManager.uploadFile(request);

            // then
            assertSoftly(s -> {
                s.assertThat(result.accessUrl()).isEqualTo(expectedFileUrl);
                s.assertThat(result.filePath()).isEqualTo(expectedS3Key);
            });
            then(s3Client).should()
                    .putObject(any(PutObjectRequest.class), any(RequestBody.class));
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
            StorageUploadRequest request = new StorageUploadRequest(mockFile, fileName);

            willThrow(S3Exception.class)
                    .given(s3Client)
                    .putObject(any(PutObjectRequest.class), any(RequestBody.class));

            // when & then
            assertThatThrownBy(() -> s3StorageManager.uploadFile(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("S3 업로드 실패");
        }
    }
}

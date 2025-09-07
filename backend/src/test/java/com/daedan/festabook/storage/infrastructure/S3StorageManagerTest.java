package com.daedan.festabook.storage.infrastructure;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;

import com.daedan.festabook.storage.dto.StorageUploadRequest;
import com.daedan.festabook.storage.dto.StorageUploadRequestFixture;
import com.daedan.festabook.storage.dto.StorageUploadResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.core.exception.SdkClientException;
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
                    new byte[10]
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
            String expectedRelativePath = "/" + fileName;
            String expectedS3Key = String.format("%s/%s", BASE_PATH, fileName);

            StorageUploadRequest request = StorageUploadRequestFixture.create(mockFile, fileName);

            // when
            StorageUploadResponse response = s3StorageManager.uploadFile(request);

            // then
            assertSoftly(s -> {
                s.assertThat(response.accessUrl()).isEqualTo(expectedFileUrl);
                s.assertThat(response.accessRelativePath()).isEqualTo(expectedRelativePath);
                s.assertThat(response.storagePath()).isEqualTo(expectedS3Key);
            });
            then(s3Client).should()
                    .putObject(any(PutObjectRequest.class), any(RequestBody.class));
        }

        @Test
        void 예외_S3Exception_발생시_RuntimeException_변환() {
            // given
            String fileName = "test_123.jpg";
            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    fileName,
                    "image/jpeg",
                    new byte[10]
            );
            StorageUploadRequest request = StorageUploadRequestFixture.create(mockFile, fileName);

            S3Exception s3Exception = mock(S3Exception.class, Answers.RETURNS_DEEP_STUBS);
            String mockErrorCode = "AccessDenied";
            given(s3Exception.awsErrorDetails().errorCode())
                    .willReturn(mockErrorCode);

            String mockErrorMessage = "Access Denied";
            given(s3Exception.awsErrorDetails().errorMessage())
                    .willReturn(mockErrorMessage);

            willThrow(s3Exception)
                    .given(s3Client)
                    .putObject(any(PutObjectRequest.class), any(RequestBody.class));

            // when & then
            assertThatThrownBy(() -> s3StorageManager.uploadFile(request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage(String.format(
                            "S3 업로드 실패 {%s}:{%s}",
                            mockErrorCode,
                            mockErrorMessage
                    ));
        }

        @Test
        void 예외_SdkClientException_발생시_RuntimeException_변환() {
            // given
            String fileName = "test_123.jpg";
            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    fileName,
                    "image/jpeg",
                    new byte[10]
            );
            StorageUploadRequest request = StorageUploadRequestFixture.create(mockFile, fileName);

            SdkClientException sdkClientException = mock(SdkClientException.class, Answers.RETURNS_DEEP_STUBS);
            String mockMessage = "Network error";
            given(sdkClientException.getMessage())
                    .willReturn(mockMessage);

            willThrow(sdkClientException)
                    .given(s3Client)
                    .putObject(any(PutObjectRequest.class), any(RequestBody.class));

            // when & then
            assertThatThrownBy(() -> s3StorageManager.uploadFile(request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage(String.format("S3 업로드 실패 [%s]", mockMessage));
        }
    }
}

package com.daedan.festabook.storage.infrastructure;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;

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
            String expectedS3Key = String.format("%s/%s", BASE_PATH, fileName);

            StorageUploadRequest request = new StorageUploadRequest(mockFile, fileName);

            // when
            StorageUploadResponse result = s3StorageManager.uploadFile(request);

            // then
            assertSoftly(s -> {
                s.assertThat(result.accessUrl()).isEqualTo(expectedFileUrl);
                s.assertThat(result.storagePath()).isEqualTo(expectedS3Key);
            });
            then(s3Client).should()
                    .putObject(any(PutObjectRequest.class), any(RequestBody.class));
        }

        @Test
        void 예외_400_코드_S3Exception_발생시_BusinessException으로_변환() {
            // given
            int errorStatusCode = 400;
            String fileName = "test_123.jpg";
            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    fileName,
                    "image/jpeg",
                    new byte[10]
            );
            StorageUploadRequest request = new StorageUploadRequest(mockFile, fileName);

            S3Exception s3Exception = mock(S3Exception.class);
            given(s3Exception.statusCode())
                    .willReturn(errorStatusCode);

            willThrow(s3Exception)
                    .given(s3Client)
                    .putObject(any(PutObjectRequest.class), any(RequestBody.class));

            // when & then
            assertThatThrownBy(() -> s3StorageManager.uploadFile(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("잘못된 요청입니다. 메타데이터, 요청 파라미터를 확인하세요.");
        }

        @Test
        void 예외_403_코드_S3Exception_발생시_BusinessException으로_변환() {
            // given
            int errorStatusCode = 403;
            String fileName = "test_123.jpg";
            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    fileName,
                    "image/jpeg",
                    new byte[10]
            );
            StorageUploadRequest request = new StorageUploadRequest(mockFile, fileName);

            S3Exception s3Exception = mock(S3Exception.class);
            given(s3Exception.statusCode())
                    .willReturn(errorStatusCode);

            willThrow(s3Exception)
                    .given(s3Client)
                    .putObject(any(PutObjectRequest.class), any(RequestBody.class));

            // when & then
            assertThatThrownBy(() -> s3StorageManager.uploadFile(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("S3 업로드 권한이 없습니다.");
        }

        @Test
        void 예외_404_코드_S3Exception_발생시_BusinessException으로_변환() {
            // given
            int errorStatusCode = 404;
            String fileName = "test_123.jpg";
            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    fileName,
                    "image/jpeg",
                    new byte[10]
            );
            StorageUploadRequest request = new StorageUploadRequest(mockFile, fileName);

            S3Exception s3Exception = mock(S3Exception.class);
            given(s3Exception.statusCode())
                    .willReturn(errorStatusCode);

            willThrow(s3Exception)
                    .given(s3Client)
                    .putObject(any(PutObjectRequest.class), any(RequestBody.class));

            // when & then
            assertThatThrownBy(() -> s3StorageManager.uploadFile(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("S3 버킷을 찾을 수 없습니다.");
        }

        @Test
        void 예외_413_코드_S3Exception_발생시_BusinessException으로_변환() {
            // given
            int errorStatusCode = 413;
            String fileName = "test_123.jpg";
            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    fileName,
                    "image/jpeg",
                    new byte[10]
            );
            StorageUploadRequest request = new StorageUploadRequest(mockFile, fileName);

            S3Exception s3Exception = mock(S3Exception.class);
            given(s3Exception.statusCode())
                    .willReturn(errorStatusCode);

            willThrow(s3Exception)
                    .given(s3Client)
                    .putObject(any(PutObjectRequest.class), any(RequestBody.class));

            // when & then
            assertThatThrownBy(() -> s3StorageManager.uploadFile(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("업로드 하려는 파일이 요청 크기 제한을 초과했습니다.");
        }

        @Test
        void 예외_500_코드_S3Exception_발생시_BusinessException으로_변환() {
            // given
            int errorStatusCode = 500;
            String fileName = "test_123.jpg";
            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    fileName,
                    "image/jpeg",
                    new byte[10]
            );
            StorageUploadRequest request = new StorageUploadRequest(mockFile, fileName);

            S3Exception s3Exception = mock(S3Exception.class);
            given(s3Exception.statusCode())
                    .willReturn(errorStatusCode);

            willThrow(s3Exception)
                    .given(s3Client)
                    .putObject(any(PutObjectRequest.class), any(RequestBody.class));

            // when & then
            assertThatThrownBy(() -> s3StorageManager.uploadFile(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("S3 서버 오류가 발생했습니다. 나중에 다시 시도하세요.");
        }

        @Test
        void 예외_미정의_4XX코드_S3Exception_발생시_BusinessException으로_변환() {
            // given
            int errorStatusCode = 418;
            String fileName = "test_123.jpg";
            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    fileName,
                    "image/jpeg",
                    new byte[10]
            );
            StorageUploadRequest request = new StorageUploadRequest(mockFile, fileName);

            S3Exception s3Exception = mock(S3Exception.class, Answers.RETURNS_DEEP_STUBS);
            given(s3Exception.statusCode())
                    .willReturn(errorStatusCode);
            given(s3Exception.awsErrorDetails().errorMessage())
                    .willReturn("Mock Error Message");

            willThrow(s3Exception)
                    .given(s3Client)
                    .putObject(any(PutObjectRequest.class), any(RequestBody.class));

            // when & then
            assertThatThrownBy(() -> s3StorageManager.uploadFile(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageStartingWith("S3 업로드 실패");
        }

        @Test
        void 예외_SdkClientException_발생시_BusinessException으로_변환() {
            // given
            String fileName = "test_123.jpg";
            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    fileName,
                    "image/jpeg",
                    new byte[10]
            );
            StorageUploadRequest request = new StorageUploadRequest(mockFile, fileName);

            willThrow(SdkClientException.class)
                    .given(s3Client)
                    .putObject(any(PutObjectRequest.class), any(RequestBody.class));

            // when & then
            assertThatThrownBy(() -> s3StorageManager.uploadFile(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageStartingWith("S3 업로드 실패, 네트워크 연결 또는 AWS 자격 증명을 확인하세요.");
        }
    }
}

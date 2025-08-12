package com.daedan.festabook.storage.infrastructure;

import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.storage.domain.StorageManager;
import com.daedan.festabook.storage.dto.StorageUploadRequest;
import com.daedan.festabook.storage.dto.StorageUploadResponse;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriUtils;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Component
@Profile("prod")
public class S3StorageManager implements StorageManager {

    private final S3Client s3Client;
    private final String basePath;
    private final String bucketName;

    public S3StorageManager(
            S3Client s3Client,
            @Value("${cloud.aws.s3.base-path}") String basePath,
            @Value("${cloud.aws.s3.bucket}") String bucketName
    ) {
        this.s3Client = s3Client;
        this.basePath = basePath;
        this.bucketName = bucketName;
    }

    @Override
    public StorageUploadResponse uploadFile(StorageUploadRequest request) {
        try {
            String s3Key = buildS3Key(request.getStoragePath());
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType(request.getContentType())
                    .contentLength(request.getSize())
                    .build();
            RequestBody requestBody = RequestBody.fromBytes(request.getBytes());
            s3Client.putObject(putObjectRequest, requestBody);

            return new StorageUploadResponse(buildFileUrl(s3Key), s3Key);
        } catch (S3Exception e) {
            if (e.statusCode() == 400) {
                throw new BusinessException("잘못된 요청입니다. 메타데이터, 요청 파라미터를 확인하세요.", HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (e.statusCode() == 403) {
                throw new BusinessException("S3 업로드 권한이 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (e.statusCode() == 404) {
                throw new BusinessException("S3 버킷을 찾을 수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (e.statusCode() == 413) {
                throw new BusinessException("파일 크기가 너무 큽니다.", HttpStatus.BAD_REQUEST);
            } else if (e.statusCode() >= 500) {
                throw new BusinessException("S3 서버 오류가 발생했습니다. 나중에 다시 시도하세요.", HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                throw new BusinessException("S3 업로드 실패 " + e.awsErrorDetails().errorMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (SdkClientException e) {
            throw new BusinessException("S3 업로드 실패, 네트워크 연결 또는 AWS 자격 증명을 확인하세요.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String buildS3Key(String fileName) {
        return String.format("%s/%s", basePath, fileName);
    }

    private String buildFileUrl(String key) {
        String encodedKey = UriUtils.encodePath(key, StandardCharsets.UTF_8);
        return String.format(
                "https://%s.s3.%s.amazonaws.com/%s",
                bucketName,
                s3Client.serviceClientConfiguration().region().id(),
                encodedKey
        );
    }
}

package com.daedan.festabook.storage.infrastructure;

import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.storage.domain.StorageService;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
@Profile("prod")
public class S3StorageService implements StorageService {

    private static final int MAX_FILE_NAME_LENGTH = 255;

    private final S3Client s3Client;
    private final String basePath;
    private final String bucketName;

    public S3StorageService(
            S3Client s3Client,
            @Value("${cloud.aws.s3.base-path}") String basePath,
            @Value("${cloud.aws.s3.bucket}") String bucketName
    ) {
        this.s3Client = s3Client;
        this.basePath = basePath;
        this.bucketName = bucketName;
    }

    @Override
    public String uploadFile(MultipartFile file, String fileName) {
        validateFile(file);
        validateFileName(fileName);

        String s3Key = buildS3Key(fileName);
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();
            RequestBody requestBody = RequestBody.fromBytes(file.getBytes());
            s3Client.putObject(putObjectRequest, requestBody);

            return buildFileUrl(s3Key);
        } catch (S3Exception e) {
            throw new BusinessException("S3 업로드 실패", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IOException e) {
            throw new BusinessException("파일 처리 실패", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("파일이 비어 있습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateFileName(String fileName) {
        if (!StringUtils.hasText(fileName)) {
            throw new BusinessException("파일 이름이 비어 있습니다.", HttpStatus.BAD_REQUEST);
        }

        if (fileName.length() > MAX_FILE_NAME_LENGTH) {
            throw new BusinessException("파일명이 너무 깁니다.", HttpStatus.BAD_REQUEST);
        }

        // 경로 순회 공격 방지
        if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            throw new BusinessException("허용되지 않는 파일명입니다.", HttpStatus.BAD_REQUEST);
        }
    }

    private String buildS3Key(String fileName) {
        return String.format("%s/%s", basePath, fileName);
    }

    private String buildFileUrl(String key) {
        String encodedKey = UriUtils.encodePath(key, StandardCharsets.UTF_8);
        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucketName,
                s3Client.serviceClientConfiguration().region().id(),
                encodedKey);
    }
}

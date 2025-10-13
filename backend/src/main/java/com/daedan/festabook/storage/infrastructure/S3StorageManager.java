package com.daedan.festabook.storage.infrastructure;

import com.daedan.festabook.global.logging.Loggable;
import com.daedan.festabook.storage.domain.StorageManager;
import com.daedan.festabook.storage.dto.StorageUploadRequest;
import com.daedan.festabook.storage.dto.StorageUploadResponse;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriUtils;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Loggable
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
        StoreFile storeFile = new StoreFile(request.file(), request.storagePath());

        try {
            String s3Key = buildS3Key(storeFile.getStoragePath());
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType(storeFile.getContentType())
                    .contentLength(storeFile.getSize())
                    .build();
            RequestBody requestBody = RequestBody.fromBytes(storeFile.getBytes());
            s3Client.putObject(putObjectRequest, requestBody);

            StorageUploadResponse response = new StorageUploadResponse(
                    buildFileUrl(s3Key),
                    ensureLeadingSlash(storeFile.getStoragePath()),
                    s3Key
            );
            return response;
        } catch (S3Exception e) {
            throw new RuntimeException(String.format(
                    "S3 업로드 실패 {%s}:{%s}",
                    e.awsErrorDetails().errorCode(),
                    e.awsErrorDetails().errorMessage()
            ));
        } catch (SdkClientException e) {
            throw new RuntimeException(String.format("S3 업로드 실패 [%s]", e.getMessage()));
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

    private String ensureLeadingSlash(String storagePath) {
        if (storagePath.startsWith("/")) {
            return storagePath;
        }
        return String.format("/%s", storagePath);
    }
}

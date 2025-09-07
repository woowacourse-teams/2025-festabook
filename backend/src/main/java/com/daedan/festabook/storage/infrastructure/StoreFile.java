package com.daedan.festabook.storage.infrastructure;

import com.daedan.festabook.global.exception.BusinessException;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public class StoreFile {

    private static final int MAX_FILE_NAME_LENGTH = 255;

    private final MultipartFile file;
    private final String storagePath;

    public StoreFile(
            MultipartFile file,
            String storagePath
    ) {
        validateMultipartFile(file);
        validateStoragePath(storagePath);

        this.file = file;
        this.storagePath = storagePath;
    }

    private void validateMultipartFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("파일은 비어있을 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateStoragePath(String storagePath) {
        if (!StringUtils.hasText(storagePath)) {
            throw new BusinessException("파일 경로는 비어있을 수 없습니다.", HttpStatus.BAD_REQUEST);
        }

        if (storagePath.length() > MAX_FILE_NAME_LENGTH) {
            throw new BusinessException(
                    String.format("파일 경로는 %d자를 초과할 수 없습니다.", MAX_FILE_NAME_LENGTH),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public String getStoragePath() {
        return storagePath;
    }

    public String getContentType() {
        return file.getContentType();
    }

    public long getSize() {
        return file.getSize();
    }

    public byte[] getBytes() {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new BusinessException("MultipartFile 에서 Byte 데이터를 읽기 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

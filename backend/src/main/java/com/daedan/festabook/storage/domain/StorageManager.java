package com.daedan.festabook.storage.domain;

import com.daedan.festabook.storage.dto.StorageUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface StorageManager {

    StorageUploadResponse uploadFile(MultipartFile file, String fileName);
}

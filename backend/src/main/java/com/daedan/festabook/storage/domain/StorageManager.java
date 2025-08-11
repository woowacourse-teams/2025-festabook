package com.daedan.festabook.storage.domain;

import org.springframework.web.multipart.MultipartFile;

public interface StorageManager {

    String uploadFile(MultipartFile file, String fileName);
}

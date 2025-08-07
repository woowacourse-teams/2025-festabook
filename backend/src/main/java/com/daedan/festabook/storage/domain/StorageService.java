package com.daedan.festabook.storage.domain;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    String uploadFile(MultipartFile file, String directory);
}

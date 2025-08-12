package com.daedan.festabook.storage.domain;

import com.daedan.festabook.storage.dto.StorageUploadRequest;
import com.daedan.festabook.storage.dto.StorageUploadResponse;

public interface StorageManager {

    StorageUploadResponse uploadFile(StorageUploadRequest request);
}

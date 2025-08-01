package com.daedan.festabook.image.domain;

import org.springframework.web.multipart.MultipartFile;

public interface ImageManager {

    String upload(MultipartFile image);
}

package com.daedan.festabook.image.infrastructure;

import com.daedan.festabook.image.domain.ImageManager;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class StubImageManager implements ImageManager {

    private static final String STUB_IMAGE_URL = "https://contents.kyobobook.co.kr/sih/fit-in/458x0/pdt/480D221274460.jpg";

    @Override
    public String upload(MultipartFile image) {
        return STUB_IMAGE_URL;
    }
}

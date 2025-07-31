package com.daedan.festabook.image.service;

import com.daedan.festabook.image.domain.ImageManager;
import com.daedan.festabook.image.dto.ImageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageManager imageManager;

    public ImageResponse uploadImage(MultipartFile image) {
        String imageUrl = imageManager.upload(image);
        return ImageResponse.from(imageUrl);
    }
}

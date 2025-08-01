package com.daedan.festabook.image.dto;

public record ImageResponse(
        String imageUrl
) {

    public static ImageResponse from(String imageUrl) {
        return new ImageResponse(imageUrl);
    }
}

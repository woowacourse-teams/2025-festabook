package com.daedan.festabook.presentation.placeList.placeMap

import androidx.annotation.DrawableRes
import com.naver.maps.map.overlay.OverlayImage

/**
 * OverlayImage를 flyweight 패턴으로 관리하기 위해 필요한 객체입니다
 * 초기에 이미지 id를 받아 OverlayImage를 생성합니다
 * 이후 getImage로 저장된 OverlayImage를 반환합니다
 */
class OverlayImageManager(
    @DrawableRes private val resources: List<Int>,
) {
    private val images = mutableMapOf<Int, OverlayImage>()

    init {
        resources.forEach { id ->
            images[id] = OverlayImage.fromResource(id)
        }
    }

    fun getImage(id: Int): OverlayImage? = images[id]
}

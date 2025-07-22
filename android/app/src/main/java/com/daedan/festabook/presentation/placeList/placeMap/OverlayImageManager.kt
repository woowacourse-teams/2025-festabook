package com.daedan.festabook.presentation.placeList.placeMap

import androidx.annotation.DrawableRes
import com.naver.maps.map.overlay.OverlayImage

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

package com.daedan.festabook.presentation.placeList.placeMap

import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import kotlin.math.pow

class MapScrollManager(
    private val map: NaverMap,
) {
    fun cameraScroll(y: Float) {
        val dy =
            y * normalizeZoomWeight()
        val update =
            CameraUpdate
                .toCameraPosition(
                    map.cameraPosition.copy(
                        latitude = map.cameraPosition.target.latitude + dy,
                    ),
                ).animate(CameraAnimation.Easing)
        map.moveCamera(update)
    }

    private fun normalizeZoomWeight() = POSITION_TO_LATITUDE_WEIGHT * zoomLevelScaleWeight(map.cameraPosition.zoom)

    private fun CameraPosition.copy(
        latitude: Double = target.latitude,
        longitude: Double = target.longitude,
        zoom: Double = this.zoom,
    ): CameraPosition =
        CameraPosition(
            LatLng(latitude, longitude),
            zoom,
        )

    companion object {
        private const val POSITION_TO_LATITUDE_WEIGHT = -0.000015
        private const val BASE_ZOOM_LEVEL = 15

        private fun zoomLevelScaleWeight(currentZoomLevel: Double) = 2.0.pow(BASE_ZOOM_LEVEL - currentZoomLevel)
    }
}

package com.daedan.festabook.presentation.placeList.placeMap

import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap

class MapScrollManager(
    private val map: NaverMap,
) {
    fun cameraScroll(y: Float) {
        val dy =
            y * POSITION_TO_LATITUDE_WIGHT
        val update =
            CameraUpdate
                .toCameraPosition(
                    map.cameraPosition.copy(
                        latitude = map.cameraPosition.target.latitude + dy,
                    ),
                ).animate(CameraAnimation.Easing)
        map.moveCamera(update)
    }

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
        private const val POSITION_TO_LATITUDE_WIGHT = -0.000015
    }
}

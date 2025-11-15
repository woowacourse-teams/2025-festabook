package com.daedan.festabook.presentation.placeMap.mapManager.internal

import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import kotlin.math.pow

/**
 * @deprecated
 * @see
 *  이 클래스는 더 이상 사용되지 않으며, 향후 버전에서 제거될 예정입니다.
 *  PlaceListScrollBahavior와 함께 사용되며,
 *  레이아웃의 스크롤 움직임에 따라 카메라를 자연스럽게 이동시키는 클래스입니다
 *  지도 줌 배율에 맞게 가중치가 적용되어 있습니다
 *  자세한 내용은 해당 링크를 참조해주세요
 * "https://github.com/woowacourse-teams/2025-festabook/pull/174"
 */
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

package com.daedan.festabook.presentation.common

import com.daedan.festabook.presentation.placeList.model.InitialMapSettingUiModel
import com.daedan.festabook.presentation.placeList.model.toLatLng
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap

private const val POSITION_TO_LATITUDE_WIGHT = -0.000015

fun NaverMap.setUp(initialMapSettingUiModel: InitialMapSettingUiModel) {
    mapType = NaverMap.MapType.Basic
    isIndoorEnabled = true
    uiSettings.isZoomControlEnabled = false

    val cameraUpdate1 =
        CameraUpdate
            .scrollTo(
                initialMapSettingUiModel.initialCenter.toLatLng(),
            )

    val cameraUpdate2 =
        CameraUpdate.zoomTo(
            initialMapSettingUiModel.zoom.toDouble(),
        )
    moveCamera(cameraUpdate1)
    moveCamera(cameraUpdate2)
}

fun NaverMap.setContentPaddingBottom(height: Int) {
    setContentPadding(
        0,
        0,
        0,
        height,
        true,
    )
}

fun NaverMap.cameraScroll(y: Float) {
    val dy = y * POSITION_TO_LATITUDE_WIGHT
    val update =
        CameraUpdate
            .toCameraPosition(
                cameraPosition.copy(
                    latitude = cameraPosition.target.latitude + dy,
                ),
            ).animate(CameraAnimation.Easing)
    moveCamera(update)
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

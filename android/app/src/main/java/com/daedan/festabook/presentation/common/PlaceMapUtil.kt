package com.daedan.festabook.presentation.common

import androidx.core.graphics.toColorInt
import com.daedan.festabook.R
import com.daedan.festabook.presentation.placeList.model.CoordinateUiModel
import com.daedan.festabook.presentation.placeList.model.InitialMapSettingUiModel
import com.daedan.festabook.presentation.placeList.model.PlaceCategory
import com.daedan.festabook.presentation.placeList.model.PlaceCoordinateUiModel
import com.daedan.festabook.presentation.placeList.model.toLatLng
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PolygonOverlay

fun NaverMap.setUp(initialMapSettingUiModel: InitialMapSettingUiModel) {
    mapType = NaverMap.MapType.Basic
    isIndoorEnabled = true
    uiSettings.isZoomControlEnabled = false
    uiSettings.isScaleBarEnabled = false

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
    setInitialPolygon(initialMapSettingUiModel.border)
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
    val dy =
        y * POSITION_TO_LATITUDE_WIGHT
    val update =
        CameraUpdate
            .toCameraPosition(
                cameraPosition.copy(
                    latitude = cameraPosition.target.latitude + dy,
                ),
            ).animate(CameraAnimation.Easing)
    moveCamera(update)
}

fun NaverMap.setLogoMarginBottom(height: Int) {
    uiSettings.setLogoMargin(
        16,
        0,
        0,
        height,
    )
}

private fun NaverMap.setInitialPolygon(border: List<CoordinateUiModel>) {
    val polygon = PolygonOverlay()
    polygon.coords = EDGE_COORS
    polygon.holes =
        listOf(
            border.map {
                it.toLatLng()
            },
        )
    polygon.color = OVERLAY_COLOR_INT.toColorInt()
    polygon.outlineWidth = OVERLAY_OUTLINE_STROKE_WIDTH
    polygon.map = this
}

fun NaverMap.setPlaceLocation(coordinates: List<PlaceCoordinateUiModel>) {
    coordinates.forEach { place ->
        val marker = Marker()
        marker.position = place.coordinate.toLatLng()
        when (place.category) {
            PlaceCategory.BOOTH ->
                marker.icon = OverlayImage.fromResource(R.drawable.ic_booth)
            PlaceCategory.FOOD_TRUCK ->
                marker.icon = OverlayImage.fromResource(R.drawable.ic_food_truck)
            PlaceCategory.TOILET ->
                marker.icon = OverlayImage.fromResource(R.drawable.ic_toilet)
            PlaceCategory.BAR ->
                marker.icon = OverlayImage.fromResource(R.drawable.ic_bar)
            PlaceCategory.TRASH_CAN ->
                marker.icon = OverlayImage.fromResource(R.drawable.ic_trash)
            PlaceCategory.SMOKING_AREA ->
                marker.icon = OverlayImage.fromResource(R.drawable.ic_smoking_area)
        }
        marker.map = this
    }
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

private const val POSITION_TO_LATITUDE_WIGHT = -0.000015
private const val OVERLAY_COLOR_INT = "#4D000000"
private const val OVERLAY_OUTLINE_STROKE_WIDTH = 4
private val EDGE_COORS =
    listOf(
        LatLng(39.2163345, 123.5125660),
        LatLng(39.2163345, 130.5440844),
        LatLng(32.8709533, 130.5440844),
        LatLng(32.8709533, 123.5125660),
    )
const val LOGO_MARGIN_TOP_PX = 75

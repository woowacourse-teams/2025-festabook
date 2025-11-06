package com.daedan.festabook.presentation.placeMap.mapManager

import com.daedan.festabook.presentation.placeMap.OnCameraChangeListener
import com.daedan.festabook.presentation.placeMap.model.InitialMapSettingUiModel
import com.daedan.festabook.presentation.placeMap.model.toLatLng
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation.Easing
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import kotlin.math.pow

class MapCameraManager(
    private val map: NaverMap,
    private val settingUiModel: InitialMapSettingUiModel,
) {
    private val initialCenter = settingUiModel.initialCenter.toLatLng()

    private var onCameraChangeListener: NaverMap.OnCameraChangeListener? = null

    private val maxLength =
        settingUiModel.border.maxOf {
            settingUiModel.initialCenter.toLatLng().distanceTo(it.toLatLng())
        }

    fun moveToPosition(position: LatLng = settingUiModel.initialCenter.toLatLng()) {
        val initialCenterCoordinate =
            CameraUpdate
                .scrollTo(
                    position,
                ).animate(Easing)
        map.moveCamera(initialCenterCoordinate)
    }

    fun setupBackToInitialPosition(onCameraChangeListener: OnCameraChangeListener) {
        this.onCameraChangeListener =
            NaverMap.OnCameraChangeListener { _, _ ->
                onCameraChangeListener.onCameraChanged(
                    isExceededMaxLength(),
                )
            }
        this.onCameraChangeListener?.let {
            map.addOnCameraChangeListener(it)
        }
    }

    fun setCameraInitialPosition() {
        val initialCenterCoordinate =
            CameraUpdate
                .scrollTo(
                    settingUiModel.initialCenter.toLatLng(),
                )
        val initialZoomLevelCoordinate =
            CameraUpdate
                .zoomTo(
                    settingUiModel.zoom.toDouble(),
                )
        map.moveCamera(initialZoomLevelCoordinate)
        map.moveCamera(initialCenterCoordinate)
    }

    fun clearListener() {
        onCameraChangeListener?.let { callback ->
            map.removeOnCameraChangeListener(callback)
            onCameraChangeListener = null
        }
    }

    private fun isExceededMaxLength(): Boolean {
        val currentPosition = map.cameraPosition.target
        val zoomWeight = map.cameraPosition.zoom.zoomWeight()
        return currentPosition.distanceTo(initialCenter) >
            (maxLength * zoomWeight).coerceAtLeast(
                maxLength,
            )
    }

    private fun Double.zoomWeight() =
        2.0.pow(
            DEFAULT_ZOOM_LEVEL - this,
        )

    companion object {
        private const val DEFAULT_ZOOM_LEVEL = 15
    }
}

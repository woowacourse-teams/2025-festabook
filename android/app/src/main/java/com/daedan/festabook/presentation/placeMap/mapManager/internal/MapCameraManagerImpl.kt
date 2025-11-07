package com.daedan.festabook.presentation.placeMap.mapManager.internal

import com.daedan.festabook.di.mapManager.PlaceMapScope
import com.daedan.festabook.presentation.placeMap.OnCameraChangeListener
import com.daedan.festabook.presentation.placeMap.mapManager.MapCameraManager
import com.daedan.festabook.presentation.placeMap.model.InitialMapSettingUiModel
import com.daedan.festabook.presentation.placeMap.model.toLatLng
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlin.math.pow

@Inject
@ContributesBinding(PlaceMapScope::class)
@SingleIn(PlaceMapScope::class)
class MapCameraManagerImpl(
    private val map: NaverMap,
    private val settingUiModel: InitialMapSettingUiModel,
) : MapCameraManager {
    private val initialCenter = settingUiModel.initialCenter.toLatLng()

    private var onCameraChangeListener: NaverMap.OnCameraChangeListener? = null

    private val maxLength =
        settingUiModel.border.maxOf {
            settingUiModel.initialCenter.toLatLng().distanceTo(it.toLatLng())
        }

    override fun moveToPosition(position: LatLng) {
        val initialCenterCoordinate =
            CameraUpdate
                .scrollTo(
                    position,
                ).animate(CameraAnimation.Easing)
        map.moveCamera(initialCenterCoordinate)
    }

    // 초기 위치로 이동합니다.
    override fun moveToPosition() {
        moveToPosition(settingUiModel.initialCenter.toLatLng())
    }

    override fun setupBackToInitialPosition(onCameraChangeListener: OnCameraChangeListener) {
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

    override fun setCameraInitialPosition() {
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

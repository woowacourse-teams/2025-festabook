package com.daedan.festabook.presentation.placeList.placeMap

import android.content.Context
import androidx.core.content.ContextCompat
import com.daedan.festabook.BuildConfig
import com.daedan.festabook.R
import com.daedan.festabook.presentation.common.toPx
import com.daedan.festabook.presentation.placeList.model.CoordinateUiModel
import com.daedan.festabook.presentation.placeList.model.InitialMapSettingUiModel
import com.daedan.festabook.presentation.placeList.model.PlaceCategoryUiModel
import com.daedan.festabook.presentation.placeList.model.PlaceCoordinateUiModel
import com.daedan.festabook.presentation.placeList.model.iconResources
import com.daedan.festabook.presentation.placeList.model.setIcon
import com.daedan.festabook.presentation.placeList.model.toLatLng
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation.Easing
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.PolygonOverlay
import kotlin.math.pow

class MapManager(
    private val map: NaverMap,
    private val initialPadding: Int,
    private val settingUiModel: InitialMapSettingUiModel,
) {
    var markers: List<Marker> = emptyList()
        private set

    private val overlayImageManager =
        OverlayImageManager(
            PlaceCategoryUiModel.iconResources + listOf(R.drawable.ic_cluster_marker),
        )

    private val context = map.context
    private val maxLength =
        settingUiModel.border.maxOf {
            settingUiModel.initialCenter.toLatLng().distanceTo(it.toLatLng())
        }

    private val initialCenter = settingUiModel.initialCenter.toLatLng()
    private var onCameraChangeListener: NaverMap.OnCameraChangeListener? = null

    fun setPlaceLocation(coordinates: List<PlaceCoordinateUiModel>) {
        markers =
            coordinates.mapIndexed { idx, place ->
                Marker().generate(place)
            }
    }

    fun filterPlace(categories: List<PlaceCategoryUiModel>) {
        clearFilter()
        markers.forEach { marker ->
            marker.isVisible = (marker.tag as? PlaceCategoryUiModel) in categories
        }
    }

    fun clearFilter() {
        markers.forEach {
            it.isVisible = true
        }
    }

    fun setupMap() {
        map.apply {
            isIndoorEnabled = true
            symbolScale = SYMBOL_SIZE_WEIGHT
            uiSettings.isZoomControlEnabled = false
            uiSettings.isScaleBarEnabled = false
            customStyleId = BuildConfig.NAVER_MAP_STYLE_ID
            setCameraInitialPosition()
            setInitialPolygon(settingUiModel.border)
            setContentPaddingBottom(initialPadding)
            setLogoMarginBottom()
        }
    }

    fun setupBackToInitialPosition(onCameraChangeListener: OnCameraChangeListener) {
        this.onCameraChangeListener =
            object : NaverMap.OnCameraChangeListener {
                override fun onCameraChange(
                    reason: Int,
                    animated: Boolean,
                ) {
                    onCameraChangeListener.onCameraChanged(isExceededMaxLength())
                }
            }
        this.onCameraChangeListener?.let {
            map.addOnCameraChangeListener(it)
        }
    }

    fun moveToInitialPosition() {
        val initialCenterCoordinate =
            CameraUpdate
                .scrollTo(
                    settingUiModel.initialCenter.toLatLng(),
                ).animate(Easing)
        map.moveCamera(initialCenterCoordinate)
    }

    fun isExceededMaxLength(): Boolean {
        val currentPosition = map.cameraPosition.target
        val zoomWeight = map.cameraPosition.zoom.zoomWeight()
        return currentPosition.distanceTo(initialCenter) > maxLength * zoomWeight
    }

    fun clearMapManager() {
        onCameraChangeListener?.let { callback ->
            map.removeOnCameraChangeListener(callback)
            onCameraChangeListener = null
        }
    }

    private fun setContentPaddingBottom(height: Int) {
        map.setContentPadding(
            0,
            0,
            0,
            height,
            true,
        )
    }

    private fun setLogoMarginBottom() {
        map.uiSettings.setLogoMargin(
            16.toPx(context),
            0,
            0,
            context.getRootPixel() - 410.toPx(context),
        )
    }

    private fun setCameraInitialPosition() {
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

    // 생성자로 입력받은 초기 위치 경계를 설정합니다
    private fun NaverMap.setInitialPolygon(border: List<CoordinateUiModel>) {
        PolygonOverlay().apply {
            coords = EDGE_COORS
            holes =
                listOf(
                    border.map {
                        it.toLatLng()
                    },
                )
            val overlayColor = ContextCompat.getColor(context, R.color.black400_alpha30)
            color = overlayColor
            outlineWidth = OVERLAY_OUTLINE_STROKE_WIDTH
            map = this@MapManager.map
        }
    }

    private fun Context.getRootPixel() = context.resources.displayMetrics.heightPixels

    private fun Marker.generate(place: PlaceCoordinateUiModel): Marker {
        width = Marker.SIZE_AUTO
        height = Marker.SIZE_AUTO
        position = place.coordinate.toLatLng()
        map = this@MapManager.map
        overlayImageManager.setIcon(this, place.category)
        tag = place.category
        return this
    }

    private fun Double.zoomWeight() =
        2.0.pow(
            DEFAULT_ZOOM_LEVEL - this,
        )

    companion object {
        private const val OVERLAY_OUTLINE_STROKE_WIDTH = 4
        private const val SYMBOL_SIZE_WEIGHT = 0.8f
        private const val DEFAULT_ZOOM_LEVEL = 15

        // 대한민국 전체를 덮는 오버레이 좌표입니다
        private val EDGE_COORS =
            listOf(
                LatLng(39.2163345, 123.5125660),
                LatLng(39.2163345, 130.5440844),
                LatLng(32.8709533, 130.5440844),
                LatLng(32.8709533, 123.5125660),
            )
    }
}

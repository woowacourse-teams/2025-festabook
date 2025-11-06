package com.daedan.festabook.presentation.placeMap.mapManager

import androidx.core.content.ContextCompat
import com.daedan.festabook.BuildConfig
import com.daedan.festabook.R
import com.daedan.festabook.domain.model.TimeTag
import com.daedan.festabook.presentation.common.toPx
import com.daedan.festabook.presentation.placeMap.OnCameraChangeListener
import com.daedan.festabook.presentation.placeMap.model.CoordinateUiModel
import com.daedan.festabook.presentation.placeMap.model.InitialMapSettingUiModel
import com.daedan.festabook.presentation.placeMap.model.PlaceCategoryUiModel
import com.daedan.festabook.presentation.placeMap.model.PlaceCoordinateUiModel
import com.daedan.festabook.presentation.placeMap.model.getNormalIcon
import com.daedan.festabook.presentation.placeMap.model.iconResources
import com.daedan.festabook.presentation.placeMap.model.toLatLng
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.PolygonOverlay
import timber.log.Timber

class MapManager(
    private val map: NaverMap,
    private val initialPadding: Int,
    private val mapClickListener: MapClickListener,
    private val settingUiModel: InitialMapSettingUiModel,
) {
    private val markers: MutableList<Marker> = mutableListOf()
    private val markerCameraManager =
        MapCameraManager(
            map,
            settingUiModel,
        )
    private val markerSelectionManager =
        MapMarkerSelectionManager(
            markers,
            markerCameraManager,
        )

    private val filterManager =
        MapFilterManager(
            markers,
            markerSelectionManager,
        )

    private val overlayImageManager =
        OverlayImageManager(
            PlaceCategoryUiModel.iconResources,
        )

    private val context = map.context

    fun setPlaceLocation(coordinates: List<PlaceCoordinateUiModel>) {
        markers.addAll(
            coordinates.map { place ->
                Marker().generate(place)
            },
        )
    }

    fun filterMarkersByCategories(categories: List<PlaceCategoryUiModel>) {
        filterManager.filterMarkersByCategories(categories)
    }

    fun filterMarkersByTimeTag(selectedTimeTagId: Long?) {
        filterManager.filterMarkersByTimeTag(selectedTimeTagId)
    }

    fun clearFilter() {
        filterManager.clearFilter()
    }

    fun setupMap() {
        map.apply {
            isIndoorEnabled = true
            symbolScale = SYMBOL_SIZE_WEIGHT
            uiSettings.isZoomControlEnabled = false
            uiSettings.isScaleBarEnabled = false
            customStyleId = BuildConfig.NAVER_MAP_STYLE_ID
            markerCameraManager.setCameraInitialPosition()
            setInitialPolygon(settingUiModel.border)
            setContentPaddingBottom(initialPadding)
            setLogoMarginBottom()

            setOnMapClickListener { _, latLng ->
                println("지도 클릭됨! 위치: $latLng")
                Timber.d("지도 클릭: $latLng")
                markerSelectionManager.unselectMarker()
                mapClickListener.onMapClickListener()
            }
        }
    }

    fun unselectMarker() {
        markerSelectionManager.unselectMarker()
    }

    fun setupBackToInitialPosition(onCameraChangeListener: OnCameraChangeListener) {
        markerCameraManager.setupBackToInitialPosition(onCameraChangeListener)
    }

    fun moveToPosition(position: LatLng = settingUiModel.initialCenter.toLatLng()) {
        markerCameraManager.moveToPosition(position)
    }

    fun clearMapManager() {
        markerCameraManager.clearListener()
    }

    fun selectMarker(placeId: Long) {
        markerSelectionManager.selectMarker(placeId)
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
            Int.MAX_VALUE,
        )
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

    private fun Marker.generate(place: PlaceCoordinateUiModel): Marker {
        width = Marker.SIZE_AUTO
        height = Marker.SIZE_AUTO
        position = place.coordinate.toLatLng()
        map = this@MapManager.map
        overlayImageManager.getNormalIcon(place.category)?.let {
            icon = it
        }
        tag = place
        captionText = place.title
        isHideCollidedCaptions = true
        isVisible = false
        captionMinZoom = PRIMARY_PLACE_ZOOM_LEVEL

        setOnClickListener {
            mapClickListener.onMarkerListener(place.placeId, place.category)
        }
        return this
    }

    companion object {
        private const val OVERLAY_OUTLINE_STROKE_WIDTH = 4
        private const val SYMBOL_SIZE_WEIGHT = 0.8f
        private const val PRIMARY_PLACE_ZOOM_LEVEL = 16.0

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

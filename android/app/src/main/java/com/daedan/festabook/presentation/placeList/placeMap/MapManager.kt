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
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.PolygonOverlay
import timber.log.Timber

class MapManager(
    private val map: NaverMap,
    private val initialPadding: Int,
    private val mapClickListener: MapClickListener,
) {
    var markers: List<Marker> = emptyList()
        private set

    private val overlayImageManager =
        OverlayImageManager(
            PlaceCategoryUiModel.iconResources + listOf(R.drawable.ic_cluster_marker),
        )

    private val context = map.context

    private var selectedMarker: Marker? = null

    fun setPlaceLocation(coordinates: List<PlaceCoordinateUiModel>) {
        markers =
            coordinates.mapIndexed { idx, place ->
                Marker().generate(place)
            }
    }

    fun filterPlace(categories: List<PlaceCategoryUiModel>) {
        markers.forEach { marker ->
            val place = marker.tag as? PlaceCoordinateUiModel
            val isSelectedMarker = marker == selectedMarker

            // 필터링된 마커이거나 선택된 마커인 경우에만 보이게 처리
            marker.isVisible = place?.category in categories || isSelectedMarker

            // 선택된 마커는 크기를 유지하고, 필터링되지 않은 마커는 원래 크기로 되돌림
            if (isSelectedMarker) {
                setSize(marker, isSelected = true)
            } else {
                setSize(marker, isSelected = false)
            }
        }
    }

    fun clearFilter() {
        markers.forEach { marker ->
            marker.isVisible = true
            val isSelectedMarker = marker == selectedMarker

            // 선택된 마커는 크기를 유지하고, 나머지는 원래 크기로 복원
            setSize(marker, isSelectedMarker)
        }
    }

    fun setupMap(settingUiModel: InitialMapSettingUiModel) {
        map.apply {
            isIndoorEnabled = true
            symbolScale = SYMBOL_SIZE_WEIGHT
            uiSettings.isZoomControlEnabled = false
            uiSettings.isScaleBarEnabled = false
            customStyleId = BuildConfig.NAVER_MAP_STYLE_ID
            moveToInitialPosition(settingUiModel)
            setInitialPolygon(settingUiModel.border)
            setContentPaddingBottom(initialPadding)
            setLogoMarginBottom()

            setOnMapClickListener { point, latLng ->
                println("지도 클릭됨! 위치: $latLng")
                Timber.d("지도 클릭: $latLng")
                unselectMarker()
                mapClickListener.onMapClickListener()
            }
        }
    }

    fun unselectMarker() {
        selectedMarker?.let { prevMarker ->
            setSize(prevMarker, isSelected = false)
            selectedMarker = null
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
            context.getCenterPixel() - 120.toPx(context),
        )
    }

    private fun NaverMap.moveToInitialPosition(settingUiModel: InitialMapSettingUiModel) {
        val initialCenterCoordinate =
            CameraUpdate
                .scrollTo(
                    settingUiModel.initialCenter.toLatLng(),
                )

        val initialZoomLevelCoordinate =
            CameraUpdate.zoomTo(
                settingUiModel.zoom.toDouble(),
            )
        moveCamera(initialCenterCoordinate)
        moveCamera(initialZoomLevelCoordinate)
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

    private fun Context.getCenterPixel() = context.resources.displayMetrics.heightPixels / 2

    private fun Marker.generate(place: PlaceCoordinateUiModel): Marker {
        width = Marker.SIZE_AUTO
        height = Marker.SIZE_AUTO
        position = place.coordinate.toLatLng()
        map = this@MapManager.map

        // 마커 초기 아이콘 설정
        overlayImageManager.setIcon(this, place.category)
        tag = place

        setOnClickListener {
            val clickedPlace = it.tag as PlaceCoordinateUiModel

            // 이전에 선택된 마커가 있다면 크기를 원래대로 되돌림
            selectedMarker?.let { prevMarker ->
                setSize(prevMarker, isSelected = false)
            }

            // 현재 클릭된 마커의 크기를 크게 변경
            setSize(this, isSelected = true)

            // 현재 마커를 `selectedMarker`에 저장
            selectedMarker = this

            // ViewModel에 마커 클릭 이벤트를 전달
            mapClickListener.onMarkerListener(clickedPlace.placeId, clickedPlace.category)
            true
        }
        return this
    }

    private fun setSize(
        marker: Marker,
        isSelected: Boolean = false,
    ) {
        val density = map.context.resources.displayMetrics.density

        // 선택 상태에 따라 마커 크기 변경
        marker.width =
            if (isSelected) {
                (SELECTED_MARKER_SIZE * density).toInt()
            } else {
                (ORIGINAL_MARKER_SIZE * density).toInt()
            }
        marker.height = marker.width
    }

    companion object {
        private const val OVERLAY_OUTLINE_STROKE_WIDTH = 4
        private const val SYMBOL_SIZE_WEIGHT = 0.8f
        private const val SELECTED_MARKER_SIZE = 50
        private const val ORIGINAL_MARKER_SIZE = 34

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

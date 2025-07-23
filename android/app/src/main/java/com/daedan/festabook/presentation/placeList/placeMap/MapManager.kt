package com.daedan.festabook.presentation.placeList.placeMap

import androidx.core.graphics.toColorInt
import com.daedan.festabook.BuildConfig
import com.daedan.festabook.R
import com.daedan.festabook.presentation.placeList.model.CoordinateUiModel
import com.daedan.festabook.presentation.placeList.model.InitialMapSettingUiModel
import com.daedan.festabook.presentation.placeList.model.PlaceCategory
import com.daedan.festabook.presentation.placeList.model.PlaceCoordinateUiModel
import com.daedan.festabook.presentation.placeList.model.iconResources
import com.daedan.festabook.presentation.placeList.model.setIcon
import com.daedan.festabook.presentation.placeList.model.toLatLng
import com.daedan.festabook.presentation.placeList.placeMap.ClusterManager.DSLHelper.put
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.PolygonOverlay

class MapManager(
    private val map: NaverMap,
    private val initialPadding: Int,
    private val settingUiModel: InitialMapSettingUiModel,
) {
    private val overlayImageManager =
        OverlayImageManager(
            PlaceCategory.iconResources + listOf(R.drawable.ic_cluster_marker),
        )

    private val clusterManager =
        ClusterManager(
            map,
            overlayImageManager,
        )

    init {
        setupMap()
    }

    fun setPlaceLocation(coordinates: List<PlaceCoordinateUiModel>) {
        clusterManager.buildCluster {
            coordinates.forEachIndexed { idx, place ->
                Marker().generate(place)
                put(idx, place)
            }
        }
    }

    private fun Marker.generate(place: PlaceCoordinateUiModel) {
        width = Marker.SIZE_AUTO
        height = Marker.SIZE_AUTO
        position = place.coordinate.toLatLng()
        minZoom = CLUSTER_ZOOM_THRESHOLD
        map = this@MapManager.map
        overlayImageManager.setIcon(this, place.category)
    }

    private fun setupMap() {
        map.apply {
            isIndoorEnabled = true
            customStyleId = BuildConfig.NAVER_MAP_STYLE_ID
            uiSettings.isZoomControlEnabled = false
            uiSettings.isScaleBarEnabled = false
            moveToInitialPosition()
            setInitialPolygon(settingUiModel.border)
            setContentPaddingBottom(initialPadding)
            setLogoMarginBottom(initialPadding - LOGO_MARGIN_TOP_PX)
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

    private fun setLogoMarginBottom(height: Int) {
        map.uiSettings.setLogoMargin(
            16,
            0,
            0,
            height,
        )
    }

    // 생성자로 입력받은 초기 위치로 카메라를 이동합니다
    private fun NaverMap.moveToInitialPosition() {
        val cameraUpdate1 =
            CameraUpdate
                .scrollTo(
                    settingUiModel.initialCenter.toLatLng(),
                )

        val cameraUpdate2 =
            CameraUpdate.zoomTo(
                settingUiModel.zoom.toDouble(),
            )
        moveCamera(cameraUpdate1)
        moveCamera(cameraUpdate2)
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
            color = OVERLAY_COLOR_INT.toColorInt()
            outlineWidth = OVERLAY_OUTLINE_STROKE_WIDTH
            map = this@MapManager.map
        }
    }

    companion object {
        // 아이템 마커와 클러스터링 마커가 전환되는 줌 레벨의 경계.
        // 이 값보다 줌 레벨이 높거나 같아지면 (즉, 지도를 확대할수록)
        // 개별 아이템 마커가 지도에 표시되기 시작합니다.
        private const val CLUSTER_ZOOM_THRESHOLD = 17.0
        private const val OVERLAY_COLOR_INT = "#4D000000"
        private const val OVERLAY_OUTLINE_STROKE_WIDTH = 4
        private const val LOGO_MARGIN_TOP_PX = 75

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

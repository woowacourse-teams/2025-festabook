package com.daedan.festabook.presentation.placeList.placeMap

import androidx.core.content.ContextCompat
import com.daedan.festabook.R
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

class MapManager(
    private val map: NaverMap,
    private val initialPadding: Int,
) {
    private val overlayImageManager =
        OverlayImageManager(
            PlaceCategoryUiModel.iconResources + listOf(R.drawable.ic_cluster_marker),
        )

    private val clusterManager =
        ClusterManager(
            map,
            overlayImageManager,
        )

    private fun setPlaceLocation(coordinates: List<PlaceCoordinateUiModel>) {
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

    fun setupMap(settingUiModel: InitialMapSettingUiModel) {
        map.apply {
            isIndoorEnabled = true
            symbolScale = SYMBOL_SIZE_WEIGHT
            uiSettings.isZoomControlEnabled = false
            uiSettings.isScaleBarEnabled = false
            moveToInitialPosition(settingUiModel)
            setInitialPolygon(settingUiModel.border)
            setContentPaddingBottom(initialPadding)
            setLogoMarginBottom(initialPadding - LOGO_MARGIN_TOP_PX)
            setPlaceLocation(settingUiModel.placeCoordinates)
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

    companion object {
        // 아이템 마커와 클러스터링 마커가 전환되는 줌 레벨의 경계.
        // 이 값보다 줌 레벨이 높거나 같아지면 (즉, 지도를 확대할수록)
        // 개별 아이템 마커가 지도에 표시되기 시작합니다.
        private const val CLUSTER_ZOOM_THRESHOLD = 17.0
        private const val OVERLAY_OUTLINE_STROKE_WIDTH = 4
        private const val LOGO_MARGIN_TOP_PX = 75
        private const val SYMBOL_SIZE_WEIGHT = 0.8f

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

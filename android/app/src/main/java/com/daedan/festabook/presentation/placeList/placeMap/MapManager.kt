package com.daedan.festabook.presentation.placeList.placeMap

import androidx.core.graphics.toColorInt
import com.daedan.festabook.presentation.placeList.model.CoordinateUiModel
import com.daedan.festabook.presentation.placeList.model.InitialMapSettingUiModel
import com.daedan.festabook.presentation.placeList.model.PlaceCategory
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
    private val settingUiModel: InitialMapSettingUiModel,
) {
    private val overlayImageManager =
        OverlayImageManager(
            PlaceCategory.iconResources,
        )

    private val clusterManager =
        ClusterManager(
            map,
            overlayImageManager,
        )

    init {
        map.apply {
            mapType = NaverMap.MapType.Basic
            isIndoorEnabled = true
            uiSettings.isZoomControlEnabled = false
            uiSettings.isScaleBarEnabled = false
            moveToInitialPosition()
            setInitialPolygon(settingUiModel.border)
            setContentPaddingBottom(initialPadding)
            setLogoMarginBottom(initialPadding - LOGO_MARGIN_TOP_PX)
        }
    }

    fun setPlaceLocation(coordinates: List<PlaceCoordinateUiModel>) {
        clusterManager.buildCluster {
            coordinates.forEachIndexed { idx, place ->
                Marker().apply {
                    overlayImageManager.setIcon(this, place.category)
                    position = place.coordinate.toLatLng()
                    minZoom = CLUSTER_CHANGE_TRIGGER_ZOOM
                    map = this@MapManager.map
                    put(position, idx, place)
                }
            }
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
        private const val CLUSTER_CHANGE_TRIGGER_ZOOM = 16.0
        private const val OVERLAY_COLOR_INT = "#4D000000"
        private const val OVERLAY_OUTLINE_STROKE_WIDTH = 4
        private const val LOGO_MARGIN_TOP_PX = 75

        private val EDGE_COORS =
            listOf(
                LatLng(39.2163345, 123.5125660),
                LatLng(39.2163345, 130.5440844),
                LatLng(32.8709533, 130.5440844),
                LatLng(32.8709533, 123.5125660),
            )
    }
}

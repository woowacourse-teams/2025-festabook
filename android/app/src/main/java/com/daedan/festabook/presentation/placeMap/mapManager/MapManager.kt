package com.daedan.festabook.presentation.placeMap.mapManager

import androidx.core.content.ContextCompat
import com.daedan.festabook.BuildConfig
import com.daedan.festabook.R
import com.daedan.festabook.presentation.common.toPx
import com.daedan.festabook.presentation.placeMap.MapClickListener
import com.daedan.festabook.presentation.placeMap.model.CoordinateUiModel
import com.daedan.festabook.presentation.placeMap.model.InitialMapSettingUiModel
import com.daedan.festabook.presentation.placeMap.model.toLatLng
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.PolygonOverlay
import dev.zacsweers.metro.Inject
import timber.log.Timber

@Inject
class MapManager(
    private val map: NaverMap,
    private val initialPadding: Int,
    private val mapClickListener: MapClickListener,
    private val settingUiModel: InitialMapSettingUiModel,
    private val cameraManager: MapCameraManager,
    private val filterManager: MapFilterManager,
    private val markerManager: MapMarkerManager,
) : MapCameraManager by cameraManager,
    MapMarkerManager by markerManager,
    MapFilterManager by filterManager {
    private val context = map.context

    init {
        map.apply {
            isIndoorEnabled = true
            symbolScale = SYMBOL_SIZE_WEIGHT
            uiSettings.isZoomControlEnabled = false
            uiSettings.isScaleBarEnabled = false
            customStyleId = BuildConfig.NAVER_MAP_STYLE_ID
            cameraManager.setCameraInitialPosition()
            setInitialPolygon(settingUiModel.border)
            setContentPaddingBottom(initialPadding)
            setLogoMarginBottom()

            setOnMapClickListener { _, latLng ->
                Timber.d("지도 클릭: $latLng")
                markerManager.unselectMarker()
                mapClickListener.onMapClickListener()
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

    companion object {
        private const val OVERLAY_OUTLINE_STROKE_WIDTH = 4
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

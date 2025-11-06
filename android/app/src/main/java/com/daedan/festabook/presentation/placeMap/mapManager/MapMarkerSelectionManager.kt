package com.daedan.festabook.presentation.placeMap.mapManager

import com.daedan.festabook.presentation.placeMap.model.PlaceCategoryUiModel
import com.daedan.festabook.presentation.placeMap.model.PlaceCoordinateUiModel
import com.daedan.festabook.presentation.placeMap.model.getNormalIcon
import com.daedan.festabook.presentation.placeMap.model.getSelectedIcon
import com.daedan.festabook.presentation.placeMap.model.iconResources
import com.naver.maps.map.overlay.Marker

class MapMarkerSelectionManager(
    private var markers: List<Marker>,
    private val mapCameraManager: MapCameraManager,
) {
    private var selectedMarker: Marker? = null
    private val overlayImageManager =
        OverlayImageManager(
            PlaceCategoryUiModel.iconResources,
        )

    fun selectMarker(placeId: Long) {
        markers
            .find {
                val placeCoordinate = it.tag as? PlaceCoordinateUiModel
                placeCoordinate?.placeId == placeId
            }?.let {
                onMarkerClick(it)
            }
    }

    fun unselectMarker() {
        selectedMarker?.let { prevMarker ->
            setMarkerIcon(prevMarker, isSelected = false)
            selectedMarker = null
        }
    }

    fun updateMarkerIcon(
        isSelectedMarker: Boolean,
        marker: Marker,
    ) {
        if (isSelectedMarker) {
            setMarkerIcon(marker, isSelected = true)
        } else {
            setMarkerIcon(marker, isSelected = false)
        }
    }

    private fun onMarkerClick(marker: Marker): Boolean {
        selectedMarker?.let {
            setMarkerIcon(it, isSelected = false)
        }
        selectedMarker = marker
        setMarkerIcon(marker, isSelected = true)
        mapCameraManager.moveToPosition(marker.position)
        return true
    }

    fun setMarkerIcon(
        marker: Marker,
        isSelected: Boolean = false,
    ) {
        val category = (marker.tag as? PlaceCoordinateUiModel)?.category ?: return
        if (isSelected) {
            overlayImageManager.getSelectedIcon(category)?.let {
                marker.icon = it
            }
            marker.isForceShowCaption = true
            marker.zIndex = Int.MAX_VALUE
            marker.captionMinZoom = 0.0
        } else {
            overlayImageManager.getNormalIcon(category)?.let {
                marker.icon = it
            }
            marker.isForceShowCaption = false
            marker.zIndex = 0
            marker.captionMinZoom = PRIMARY_PLACE_ZOOM_LEVEL
        }
    }

    companion object {
        private const val PRIMARY_PLACE_ZOOM_LEVEL = 16.0
    }
}

package com.daedan.festabook.presentation.placeMap.mapManager

import com.daedan.festabook.domain.model.TimeTag
import com.daedan.festabook.presentation.placeMap.model.PlaceCategoryUiModel
import com.daedan.festabook.presentation.placeMap.model.PlaceCoordinateUiModel
import com.naver.maps.map.overlay.Marker

class MapFilterManager(
    private val markers: List<Marker>,
    private val markerSelectionManager: MapMarkerSelectionManager,
) {
    private var selectedMarker: Marker? = null

    private var selectedTimeTagId: Long? = null

    fun filterMarkersByCategories(categories: List<PlaceCategoryUiModel>) {
        markers.forEach { marker ->
            val place = marker.tag as? PlaceCoordinateUiModel ?: return@forEach
            val isSelectedMarker = marker == selectedMarker

            // 필터링된 마커이거나 선택된 마커인 경우에만 보이게 처리
            marker.isVisible =
                place.category in categories &&
                place.timeTagIds.contains(selectedTimeTagId) ||
                isSelectedMarker

            // 선택된 마커는 크기를 유지하고, 필터링되지 않은 마커는 원래 크기로 되돌림
            markerSelectionManager.updateMarkerIcon(isSelectedMarker, marker)
        }
    }

    fun filterMarkersByTimeTag(selectedTimeTagId: Long?) {
        if (selectedTimeTagId == TimeTag.EMTPY_TIME_TAG_ID) {
            markers.forEach { it.isVisible = true }
            return
        }
        markers.forEach { marker ->
            val place = marker.tag as? PlaceCoordinateUiModel ?: return@forEach
            val isSelectedMarker = marker == selectedMarker

            marker.isVisible = place.timeTagIds.contains(selectedTimeTagId) || isSelectedMarker

            // 선택된 마커는 크기를 유지하고, 필터링되지 않은 마커는 원래 크기로 되돌림
            markerSelectionManager.updateMarkerIcon(isSelectedMarker, marker)
        }
        this.selectedTimeTagId = selectedTimeTagId
    }

    fun clearFilter() {
        markers.forEach { marker ->
            val place = marker.tag as? PlaceCoordinateUiModel ?: return@forEach
            marker.isVisible = place.timeTagIds.contains(selectedTimeTagId)

            val isSelectedMarker = marker == selectedMarker

            // 선택된 마커는 크기를 유지하고, 나머지는 원래 크기로 복원
            markerSelectionManager.setMarkerIcon(marker, isSelectedMarker)
        }
    }
}

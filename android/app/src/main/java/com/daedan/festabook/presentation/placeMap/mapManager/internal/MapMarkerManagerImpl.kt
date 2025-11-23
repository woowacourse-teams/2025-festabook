package com.daedan.festabook.presentation.placeMap.mapManager.internal

import com.daedan.festabook.di.mapManager.PlaceMapScope
import com.daedan.festabook.presentation.placeMap.MapClickListener
import com.daedan.festabook.presentation.placeMap.mapManager.MapCameraManager
import com.daedan.festabook.presentation.placeMap.mapManager.MapMarkerManager
import com.daedan.festabook.presentation.placeMap.model.PlaceCoordinateUiModel
import com.daedan.festabook.presentation.placeMap.model.getNormalIcon
import com.daedan.festabook.presentation.placeMap.model.getSelectedIcon
import com.daedan.festabook.presentation.placeMap.model.toLatLng
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@Inject
@ContributesBinding(PlaceMapScope::class)
@SingleIn(PlaceMapScope::class)
class MapMarkerManagerImpl(
    private val map: NaverMap,
    private val overlayImageManager: OverlayImageManager,
    private val cameraManager: MapCameraManager,
    private val mapClickListener: MapClickListener,
    private val markers: MutableList<Marker>,
) : MapMarkerManager {
    private var selectedMarker: Marker? = null

    override fun setMarkerIcon(
        marker: Marker,
        isSelected: Boolean,
    ) {
        val category = (marker.tag as? PlaceCoordinateUiModel)?.category ?: return
        marker.isForceShowCaption = isSelected
        marker.zIndex = if (isSelected) Int.MAX_VALUE else 0
        marker.captionMinZoom = if (isSelected) 0.0 else PRIMARY_PLACE_ZOOM_LEVEL
        val iconProvider =
            if (isSelected) overlayImageManager::getSelectedIcon else overlayImageManager::getNormalIcon

        iconProvider.invoke(category)?.let {
            marker.icon = it
        }
    }

    override fun setupMarker(coordinates: List<PlaceCoordinateUiModel>) {
        markers.addAll(
            coordinates.map { place ->
                Marker().generate(place)
            },
        )
    }

    override fun selectMarker(placeId: Long) {
        markers
            .find {
                val placeCoordinate = it.tag as? PlaceCoordinateUiModel
                placeCoordinate?.placeId == placeId
            }?.let {
                onMarkerClick(it)
            }
    }

    override fun unselectMarker() {
        selectedMarker?.let { prevMarker ->
            setMarkerIcon(prevMarker, isSelected = false)
            selectedMarker = null
        }
    }

    private fun onMarkerClick(marker: Marker): Boolean {
        selectedMarker?.let {
            setMarkerIcon(it, isSelected = false)
        }
        selectedMarker = marker
        setMarkerIcon(marker, isSelected = true)
        cameraManager.moveToPosition(marker.position)
        return true
    }

    private fun Marker.generate(place: PlaceCoordinateUiModel): Marker {
        width = Marker.SIZE_AUTO
        height = Marker.SIZE_AUTO
        position = place.coordinate.toLatLng()
        map = this@MapMarkerManagerImpl.map
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
        private const val PRIMARY_PLACE_ZOOM_LEVEL = 16.0
    }
}

package com.daedan.festabook.presentation.placeList.placeMap

import com.daedan.festabook.presentation.placeList.model.PlaceCategory
import com.daedan.festabook.presentation.placeList.model.PlaceCoordinateUiModel
import com.daedan.festabook.presentation.placeList.model.setIcon
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.naver.maps.map.clustering.ClusterMarkerInfo
import com.naver.maps.map.clustering.Clusterer
import com.naver.maps.map.clustering.DefaultClusterMarkerUpdater
import com.naver.maps.map.clustering.DefaultLeafMarkerUpdater
import com.naver.maps.map.clustering.LeafMarkerInfo
import com.naver.maps.map.overlay.Marker

class ClusterManager(
    private val map: NaverMap,
    private val overlayImageManager: OverlayImageManager,
) {
    private val clusterMarkerUpdater by lazy {
        object : DefaultClusterMarkerUpdater() {
            override fun updateClusterMarker(
                info: ClusterMarkerInfo,
                marker: Marker,
            ) {
                marker.maxZoom = CLUSTER_CHANGE_TRIGGER_ZOOM
                super.updateClusterMarker(info, marker)
            }
        }
    }

    private val leafMarkerUpdater by lazy {
        object : DefaultLeafMarkerUpdater() {
            override fun updateLeafMarker(
                info: LeafMarkerInfo,
                marker: Marker,
            ) {
                val category = info.tag as PlaceCategory
                overlayImageManager.setIcon(marker, category)
            }
        }
    }

    private val cluster: Clusterer<ClusteringMarkerKey> =
        Clusterer
            .Builder<ClusteringMarkerKey>()
            .clusterMarkerUpdater(clusterMarkerUpdater)
            .leafMarkerUpdater(leafMarkerUpdater)
            .animate(false)
            .build()

    fun buildCluster(block: DSLHelper.() -> Unit) {
        block(DSLHelper)
        cluster.addAll(DSLHelper.itemKeyMap)
        cluster.map = map
    }

    object DSLHelper {
        val itemKeyMap = mutableMapOf<ClusteringMarkerKey, PlaceCategory>()

        fun put(
            position: LatLng,
            idx: Int,
            place: PlaceCoordinateUiModel,
        ) {
            itemKeyMap[ClusteringMarkerKey(idx, position)] = place.category
        }
    }

    companion object {
        private const val CLUSTER_CHANGE_TRIGGER_ZOOM = 16.0
    }
}

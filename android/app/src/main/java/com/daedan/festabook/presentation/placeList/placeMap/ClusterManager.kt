package com.daedan.festabook.presentation.placeList.placeMap

import com.daedan.festabook.R
import com.daedan.festabook.presentation.common.toPx
import com.daedan.festabook.presentation.placeList.model.PlaceCategory
import com.daedan.festabook.presentation.placeList.model.PlaceCoordinateUiModel
import com.daedan.festabook.presentation.placeList.model.setIcon
import com.daedan.festabook.presentation.placeList.model.toLatLng
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
    private val context = map.context
    private val clusterImage = overlayImageManager.getImage(R.drawable.ic_cluster_marker)

    // 클러스터링 마커를 표시할 때 호출되는 익명 객체입니다
    private val clusterMarkerUpdater by lazy {
        object : DefaultClusterMarkerUpdater() {
            override fun updateClusterMarker(
                info: ClusterMarkerInfo,
                marker: Marker,
            ) {
                super.updateClusterMarker(info, marker)
                marker.maxZoom = CLUSTER_ZOOM_THRESHOLD
                marker.setSize(info)

                clusterImage?.let {
                    marker.icon = it
                }
            }
        }
    }

    // 클러스터링 마커가 1개일 때 호출되는 익명 객체입니다
    private val leafMarkerUpdater by lazy {
        object : DefaultLeafMarkerUpdater() {
            override fun updateLeafMarker(
                info: LeafMarkerInfo,
                marker: Marker,
            ) {
                super.updateLeafMarker(info, marker)
                val category = info.tag as PlaceCategory
                marker.width = Marker.SIZE_AUTO
                marker.height = Marker.SIZE_AUTO
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
            .screenDistance(DEFAULT_CLUSTER_SCREEN_DISTANCE)
            .build()

    fun buildCluster(block: DSLHelper.() -> Unit) {
        block(DSLHelper)
        cluster.addAll(DSLHelper.itemKeyMap)
        cluster.map = map
    }

    /**
     * 클러스터링 마커의 크기를 3단계로 나누어 관리합니다,
     * 현재 값은 HIGH_COUNT_CLUSTER_THRESHOLD가 10이고, LOW_COUNT_CLUSTER_THRESHOLD가 5 입니다
     * 10 초과이면 large, 10이하 5 초과이면 medium, 5 이하이면 small로 표시합니다
     */

    private fun Marker.setSize(info: ClusterMarkerInfo) {
        when (info.size) {
            in 0 until LOW_COUNT_CLUSTER_THRESHOLD -> {
                width = SMALL_CLUSTER_MARKER_SIZE.toPx(context)
                height = SMALL_CLUSTER_MARKER_SIZE.toPx(context)
            }
            in LOW_COUNT_CLUSTER_THRESHOLD until HIGH_COUNT_CLUSTER_THRESHOLD -> {
                width = Marker.SIZE_AUTO
                height = Marker.SIZE_AUTO
            }
            else -> {
                width = LARGE_CLUSTER_MARKER_SIZE.toPx(context)
                height = LARGE_CLUSTER_MARKER_SIZE.toPx(context)
            }
        }
    }

    object DSLHelper {
        val itemKeyMap = mutableMapOf<ClusteringMarkerKey, PlaceCategory>()

        fun put(
            idx: Int,
            place: PlaceCoordinateUiModel,
        ) {
            val position = place.coordinate.toLatLng()
            itemKeyMap[ClusteringMarkerKey(idx, position)] = place.category
        }
    }

    companion object {
        private const val CLUSTER_ZOOM_THRESHOLD = 17.0
        private const val DEFAULT_CLUSTER_SCREEN_DISTANCE = 30.0
        private const val LARGE_CLUSTER_MARKER_SIZE = 48
        private const val SMALL_CLUSTER_MARKER_SIZE = 30
        private const val HIGH_COUNT_CLUSTER_THRESHOLD = 10
        private const val LOW_COUNT_CLUSTER_THRESHOLD = 5
    }
}

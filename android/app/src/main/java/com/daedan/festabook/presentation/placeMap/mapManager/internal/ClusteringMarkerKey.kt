package com.daedan.festabook.presentation.placeMap.mapManager.internal

import com.naver.maps.geometry.LatLng
import com.naver.maps.map.clustering.ClusteringKey

data class ClusteringMarkerKey(
    val id: Int,
    private val position: LatLng,
) : ClusteringKey {
    override fun getPosition() = position
}

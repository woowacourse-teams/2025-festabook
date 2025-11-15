package com.daedan.festabook.di.mapManager

import com.daedan.festabook.presentation.placeMap.MapClickListener
import com.daedan.festabook.presentation.placeMap.mapManager.MapManager
import com.daedan.festabook.presentation.placeMap.model.InitialMapSettingUiModel
import com.naver.maps.map.NaverMap
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides

@DependencyGraph(PlaceMapScope::class)
interface MapManagerGraph {
    val mapManager: MapManager

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(
            @Provides map: NaverMap,
            @Provides settingUiModel: InitialMapSettingUiModel,
            @Provides mapClickListener: MapClickListener,
            @Provides initialPadding: Int,
        ): MapManagerGraph
    }
}

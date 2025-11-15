package com.daedan.festabook.di.mapManager

import com.daedan.festabook.presentation.placeMap.MapClickListener
import com.daedan.festabook.presentation.placeMap.MapClickListenerImpl
import com.daedan.festabook.presentation.placeMap.PlaceMapViewModel
import com.daedan.festabook.presentation.placeMap.mapManager.internal.OverlayImageManager
import com.daedan.festabook.presentation.placeMap.model.PlaceCategoryUiModel
import com.daedan.festabook.presentation.placeMap.model.iconResources
import com.naver.maps.map.overlay.Marker
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@BindingContainer
@ContributesTo(PlaceMapScope::class)
object MapManagerBindings {
    @Provides
    @SingleIn(PlaceMapScope::class)
    fun provideMarkers(): MutableList<Marker> = mutableListOf()

    @Provides
    @SingleIn(PlaceMapScope::class)
    fun provideOverlayImageManager(): OverlayImageManager = OverlayImageManager(PlaceCategoryUiModel.iconResources)

    @Provides
    @SingleIn(PlaceMapScope::class)
    fun provideMapClickListener(viewModel: PlaceMapViewModel): MapClickListener = MapClickListenerImpl(viewModel)
}

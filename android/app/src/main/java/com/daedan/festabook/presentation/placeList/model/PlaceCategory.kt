package com.daedan.festabook.presentation.placeList.model

import com.daedan.festabook.R
import com.daedan.festabook.presentation.placeList.placeMap.OverlayImageManager
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage

enum class PlaceCategory {
    FOOD_TRUCK,
    BOOTH,
    BAR,
    TRASH_CAN,
    TOILET,
    SMOKING_AREA,
    ;

    companion object
}

val PlaceCategory.Companion.iconResources: List<Int>
    get() =
        listOf(
            R.drawable.ic_booth,
            R.drawable.ic_food_truck,
            R.drawable.ic_toilet,
            R.drawable.ic_bar,
            R.drawable.ic_trash,
            R.drawable.ic_smoking_area,
        )

fun OverlayImageManager.getIcon(category: PlaceCategory): OverlayImage? =
    when (category) {
        PlaceCategory.BOOTH -> getImage(R.drawable.ic_booth)
        PlaceCategory.FOOD_TRUCK -> getImage(R.drawable.ic_food_truck)
        PlaceCategory.TOILET -> getImage(R.drawable.ic_toilet)
        PlaceCategory.BAR -> getImage(R.drawable.ic_bar)
        PlaceCategory.TRASH_CAN -> getImage(R.drawable.ic_trash)
        PlaceCategory.SMOKING_AREA -> getImage(R.drawable.ic_smoking_area)
    }

fun OverlayImageManager.setIcon(
    marker: Marker,
    category: PlaceCategory,
) {
    getIcon(category)?.let {
        marker.icon = it
    }
}

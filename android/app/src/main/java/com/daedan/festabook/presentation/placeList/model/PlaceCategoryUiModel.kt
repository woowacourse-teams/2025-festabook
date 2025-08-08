package com.daedan.festabook.presentation.placeList.model

import com.daedan.festabook.R
import com.daedan.festabook.domain.model.PlaceCategory
import com.daedan.festabook.presentation.placeList.placeMap.OverlayImageManager
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage

enum class PlaceCategoryUiModel {
    FOOD_TRUCK,
    BOOTH,
    BAR,
    TRASH_CAN,
    TOILET,
    SMOKING_AREA,
    ;

    companion object {
        val SECONDARY_CATEGORIES =
            listOf(
                TRASH_CAN,
                TOILET,
                SMOKING_AREA,
            )
    }
}

val PlaceCategoryUiModel.Companion.iconResources: List<Int>
    get() =
        listOf(
            R.drawable.ic_food_truck,
            R.drawable.ic_booth,
            R.drawable.ic_bar,
            R.drawable.ic_trash,
            R.drawable.ic_toilet,
            R.drawable.ic_smoking_area,
        )

fun OverlayImageManager.getIcon(category: PlaceCategoryUiModel): OverlayImage? =
    when (category) {
        PlaceCategoryUiModel.BOOTH -> getImage(R.drawable.ic_booth)
        PlaceCategoryUiModel.FOOD_TRUCK -> getImage(R.drawable.ic_food_truck)
        PlaceCategoryUiModel.TOILET -> getImage(R.drawable.ic_toilet)
        PlaceCategoryUiModel.BAR -> getImage(R.drawable.ic_bar)
        PlaceCategoryUiModel.TRASH_CAN -> getImage(R.drawable.ic_trash)
        PlaceCategoryUiModel.SMOKING_AREA -> getImage(R.drawable.ic_smoking_area)
    }

fun OverlayImageManager.setIcon(
    marker: Marker,
    category: PlaceCategoryUiModel,
) {
    getIcon(category)?.let {
        marker.icon = it
    }
}

fun PlaceCategory.toUiModel() =
    when (this) {
        PlaceCategory.BOOTH -> PlaceCategoryUiModel.BOOTH
        PlaceCategory.FOOD_TRUCK -> PlaceCategoryUiModel.FOOD_TRUCK
        PlaceCategory.TOILET -> PlaceCategoryUiModel.TOILET
        PlaceCategory.BAR -> PlaceCategoryUiModel.BAR
        PlaceCategory.TRASH_CAN -> PlaceCategoryUiModel.TRASH_CAN
        PlaceCategory.SMOKING_AREA -> PlaceCategoryUiModel.SMOKING_AREA
    }

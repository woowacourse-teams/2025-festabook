package com.daedan.festabook.presentation.placeList.model

import com.daedan.festabook.R
import com.daedan.festabook.domain.model.PlaceCategory
import com.daedan.festabook.presentation.placeList.placeMap.OverlayImageManager
import com.naver.maps.map.overlay.OverlayImage

enum class PlaceCategoryUiModel {
    FOOD_TRUCK,
    BOOTH,
    BAR,
    TRASH_CAN,
    TOILET,
    SMOKING_AREA,
    PRIMARY,
    PARKING,
    STAGE,
    ;

    companion object {
        val SECONDARY_CATEGORIES =
            listOf(
                TRASH_CAN,
                TOILET,
                SMOKING_AREA,
                PARKING,
                PRIMARY,
                STAGE,
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
            R.drawable.ic_primary,
            R.drawable.ic_parking,
            R.drawable.ic_stage,
            R.drawable.ic_food_truck_selected,
            R.drawable.ic_booth_selected,
            R.drawable.ic_bar_selected,
            R.drawable.ic_trash_selected,
            R.drawable.ic_toilet_selected,
            R.drawable.ic_smoking_area_selected,
            R.drawable.ic_primary_selected,
            R.drawable.ic_parking_selected,
            R.drawable.ic_stage_selected,
        )

fun OverlayImageManager.getNormalIcon(category: PlaceCategoryUiModel): OverlayImage? =
    when (category) {
        PlaceCategoryUiModel.BOOTH -> getImage(R.drawable.ic_booth)
        PlaceCategoryUiModel.FOOD_TRUCK -> getImage(R.drawable.ic_food_truck)
        PlaceCategoryUiModel.TOILET -> getImage(R.drawable.ic_toilet)
        PlaceCategoryUiModel.BAR -> getImage(R.drawable.ic_bar)
        PlaceCategoryUiModel.TRASH_CAN -> getImage(R.drawable.ic_trash)
        PlaceCategoryUiModel.SMOKING_AREA -> getImage(R.drawable.ic_smoking_area)
        PlaceCategoryUiModel.PRIMARY -> getImage(R.drawable.ic_primary)
        PlaceCategoryUiModel.PARKING -> getImage(R.drawable.ic_parking)
        PlaceCategoryUiModel.STAGE -> getImage(R.drawable.ic_stage)
    }

fun OverlayImageManager.getSelectedIcon(category: PlaceCategoryUiModel): OverlayImage? =
    when (category) {
        PlaceCategoryUiModel.BOOTH -> getImage(R.drawable.ic_booth_selected)
        PlaceCategoryUiModel.FOOD_TRUCK -> getImage(R.drawable.ic_food_truck_selected)
        PlaceCategoryUiModel.TOILET -> getImage(R.drawable.ic_toilet_selected)
        PlaceCategoryUiModel.BAR -> getImage(R.drawable.ic_bar_selected)
        PlaceCategoryUiModel.TRASH_CAN -> getImage(R.drawable.ic_trash_selected)
        PlaceCategoryUiModel.SMOKING_AREA -> getImage(R.drawable.ic_smoking_area_selected)
        PlaceCategoryUiModel.PRIMARY -> getImage(R.drawable.ic_primary_selected)
        PlaceCategoryUiModel.PARKING -> getImage(R.drawable.ic_parking_selected)
        PlaceCategoryUiModel.STAGE -> getImage(R.drawable.ic_stage_selected)
    }

fun PlaceCategoryUiModel.getIconId() =
    when (this) {
        PlaceCategoryUiModel.BOOTH -> R.drawable.ic_map_category_booth
        PlaceCategoryUiModel.FOOD_TRUCK -> R.drawable.ic_map_category_food_truck
        PlaceCategoryUiModel.TOILET -> R.drawable.ic_map_category_toilet
        PlaceCategoryUiModel.BAR -> R.drawable.ic_map_category_bar
        PlaceCategoryUiModel.TRASH_CAN -> R.drawable.ic_map_category_trash
        PlaceCategoryUiModel.SMOKING_AREA -> R.drawable.ic_map_category_smoking
        PlaceCategoryUiModel.PRIMARY -> R.drawable.ic_map_category_primary
        PlaceCategoryUiModel.PARKING -> R.drawable.ic_map_category_parking
        PlaceCategoryUiModel.STAGE -> R.drawable.ic_map_category_stage
    }

fun PlaceCategoryUiModel.getTextId() =
    when (this) {
        PlaceCategoryUiModel.BOOTH -> R.string.map_category_booth
        PlaceCategoryUiModel.FOOD_TRUCK -> R.string.map_category_food_truck
        PlaceCategoryUiModel.TOILET -> R.string.map_category_toilet
        PlaceCategoryUiModel.BAR -> R.string.map_category_bar
        PlaceCategoryUiModel.TRASH_CAN -> R.string.map_category_trash
        PlaceCategoryUiModel.SMOKING_AREA -> R.string.map_category_smoking_area
        PlaceCategoryUiModel.PRIMARY -> R.string.map_category_primary
        PlaceCategoryUiModel.PARKING -> R.string.map_category_parking
        PlaceCategoryUiModel.STAGE -> R.string.map_category_stage
    }

fun PlaceCategory.toUiModel() =
    when (this) {
        PlaceCategory.BOOTH -> PlaceCategoryUiModel.BOOTH
        PlaceCategory.FOOD_TRUCK -> PlaceCategoryUiModel.FOOD_TRUCK
        PlaceCategory.TOILET -> PlaceCategoryUiModel.TOILET
        PlaceCategory.BAR -> PlaceCategoryUiModel.BAR
        PlaceCategory.TRASH_CAN -> PlaceCategoryUiModel.TRASH_CAN
        PlaceCategory.SMOKING_AREA -> PlaceCategoryUiModel.SMOKING_AREA
        PlaceCategory.PARKING -> PlaceCategoryUiModel.PARKING
        PlaceCategory.PRIMARY -> PlaceCategoryUiModel.PRIMARY
        PlaceCategory.STAGE -> PlaceCategoryUiModel.STAGE
    }

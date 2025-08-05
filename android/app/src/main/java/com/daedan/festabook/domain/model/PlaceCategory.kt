package com.daedan.festabook.domain.model

enum class PlaceCategory {
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

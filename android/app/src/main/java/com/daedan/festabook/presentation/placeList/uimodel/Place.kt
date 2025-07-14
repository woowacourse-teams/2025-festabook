package com.daedan.festabook.presentation.placeList.uimodel

data class Place(
    val id: Int,
    val imageUrl: String,
    val category: PlaceCategory,
    val title: String,
    val description: String,
    val location: String
)
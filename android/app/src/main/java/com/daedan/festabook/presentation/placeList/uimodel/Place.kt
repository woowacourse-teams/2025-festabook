package com.daedan.festabook.presentation.placeList.uimodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Place(
    val id: Long,
    val imageUrl: String,
    val category: PlaceCategory,
    val title: String,
    val description: String,
    val location: String,
) : Parcelable

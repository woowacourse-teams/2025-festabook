package com.daedan.festabook.presentation.placeList.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaceUiModel(
    val id: Long,
    val imageUrl: String,
    val category: PlaceCategory,
    val title: String,
    val description: String,
    val location: String,
    val isBookmarked: Boolean = false,
) : Parcelable

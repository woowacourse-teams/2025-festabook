package com.daedan.festabook.presentation.placeList.logging

import com.daedan.festabook.domain.model.PlaceCategory
import com.daedan.festabook.logging.model.BaseLogData
import com.daedan.festabook.presentation.placeList.model.PlaceCategoryUiModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlacePreviewClick(
    override val baseLogData: BaseLogData.CommonLogData,
    val placeName: String,
    val timeTag: String,
    val category: String,
) : BaseLogData
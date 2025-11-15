package com.daedan.festabook.presentation.placeMap.logging

import com.daedan.festabook.logging.model.BaseLogData
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlacePreviewClick(
    override val baseLogData: BaseLogData.CommonLogData,
    val placeName: String,
    val timeTag: String,
    val category: String,
) : BaseLogData

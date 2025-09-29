package com.daedan.festabook.presentation.placeList.logging

import com.daedan.festabook.logging.model.BaseLogData
import com.daedan.festabook.presentation.placeList.model.PlaceCategoryUiModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaceMarkerClick(
    override val baseLogData: BaseLogData.CommonLogData,
    val placeId: Long,
    val timeTagName: String,
    val category: String
) : BaseLogData
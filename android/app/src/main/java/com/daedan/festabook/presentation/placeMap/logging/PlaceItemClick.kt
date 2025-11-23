package com.daedan.festabook.presentation.placeMap.logging

import com.daedan.festabook.logging.model.BaseLogData
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaceItemClick(
    override val baseLogData: BaseLogData.CommonLogData,
    val placeId: Long,
    val timeTagName: String,
    val category: String,
) : BaseLogData

package com.daedan.festabook.presentation.placeDetail.logging

import com.daedan.festabook.logging.model.BaseLogData
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaceDetailImageSwipe(
    override val baseLogData: BaseLogData.CommonLogData,
    val startIndex: Int,
) : BaseLogData
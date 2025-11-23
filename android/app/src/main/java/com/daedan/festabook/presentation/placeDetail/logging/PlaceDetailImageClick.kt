package com.daedan.festabook.presentation.placeDetail.logging

import com.daedan.festabook.logging.model.BaseLogData
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaceDetailImageClick(
    override val baseLogData: BaseLogData.CommonLogData,
    val index: Int
) : BaseLogData
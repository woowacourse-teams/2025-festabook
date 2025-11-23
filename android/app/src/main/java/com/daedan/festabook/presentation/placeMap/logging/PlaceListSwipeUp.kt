package com.daedan.festabook.presentation.placeMap.logging

import com.daedan.festabook.logging.model.BaseLogData
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaceListSwipeUp(
    override val baseLogData: BaseLogData.CommonLogData,
) : BaseLogData

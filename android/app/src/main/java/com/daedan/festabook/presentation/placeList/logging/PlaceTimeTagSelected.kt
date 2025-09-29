package com.daedan.festabook.presentation.placeList.logging

import com.daedan.festabook.logging.model.BaseLogData
import com.daedan.festabook.logging.model.LogData
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaceTimeTagSelected(
    override val baseLogData: BaseLogData.CommonLogData,
    val timeTagName: String
) : BaseLogData
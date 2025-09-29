package com.daedan.festabook.presentation.placeList.logging

import com.daedan.festabook.logging.model.BaseLogData
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaceBackToSchoolClick(
    override val baseLogData: BaseLogData.CommonLogData
) : BaseLogData
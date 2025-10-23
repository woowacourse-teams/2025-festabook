package com.daedan.festabook.presentation.placeDetail.logging

import com.daedan.festabook.domain.model.PlaceCategory
import com.daedan.festabook.logging.model.BaseLogData
import com.daedan.festabook.presentation.placeList.model.PlaceCategoryUiModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaceDetailImageClick(
    override val baseLogData: BaseLogData.CommonLogData,
    val index: Int
) : BaseLogData
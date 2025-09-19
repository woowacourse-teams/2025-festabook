package com.daedan.festabook.logging

import kotlinx.parcelize.Parcelize

@Parcelize
data class PosterTouchLogData(
    override val baseLogData: BaseLogData.CommonLogData,
    val url: String,
) : BaseLogData

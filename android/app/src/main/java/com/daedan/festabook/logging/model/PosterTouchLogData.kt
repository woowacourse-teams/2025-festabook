package com.daedan.festabook.logging.model

import kotlinx.parcelize.Parcelize

@Parcelize
data class PosterTouchLogData(
    override val baseLogData: CommonLogData,
    val url: String,
) : BaseLogData
package com.daedan.festabook.presentation.explore.model

import com.daedan.festabook.domain.model.University

data class SearchResultUiModel(
    val festivalId: Long,
    val universityName: String,
    val festivalName: String,
)

fun University.toUiModel(): SearchResultUiModel =
    SearchResultUiModel(
        festivalId = festivalId,
        universityName = universityName,
        festivalName = festivalName.replace("\n", " "),
    )

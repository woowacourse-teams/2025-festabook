package com.daedan.festabook.domain.model

import java.time.LocalDate

data class Festival(
    val festivalName: String,
    val festivalImages: List<Poster>,
    val startDate: LocalDate,
    val endDate: LocalDate,
)

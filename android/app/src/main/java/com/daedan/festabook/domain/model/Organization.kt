package com.daedan.festabook.domain.model

data class Organization(
    val id: Long,
    val universityName: String,
    val festivalImages: List<Poster>,
    val festivalName: String,
    val startDate: String,
    val endDate: String,
)

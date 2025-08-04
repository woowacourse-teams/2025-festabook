package com.daedan.festabook.domain.model

data class Organization(
    val id: Long,
    val universityName: String,
    val festival: Festival,
)

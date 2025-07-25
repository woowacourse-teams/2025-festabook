package com.daedan.festabook.domain.model

data class OrganizationGeography(
    val zoom: Int,
    val initialCenter: Coordinate,
    val polygonHoleBoundary: List<Coordinate>,
)

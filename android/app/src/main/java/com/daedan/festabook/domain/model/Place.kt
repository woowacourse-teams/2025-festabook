package com.daedan.festabook.domain.model

data class Place(
    val id: Long,
    val imageUrl: String?,
    val category: PlaceCategory,
    val title: String?,
    val description: String?,
    val location: String?,
) {
    init {
        title?.let {
            require(it.length <= 20)
        }
        description?.let {
            require(it.length <= 100)
        }
        location?.let {
            require(it.length <= 100)
        }
    }
}

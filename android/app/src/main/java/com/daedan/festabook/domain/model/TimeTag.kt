package com.daedan.festabook.domain.model

data class TimeTag(
    val timeTagId: Long,
    val name: String,
) {
    companion object {
        const val EMTPY_TIME_TAG_ID = -1L
        val EMPTY = TimeTag(EMTPY_TIME_TAG_ID, "")
    }
}

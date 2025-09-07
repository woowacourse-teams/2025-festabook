package com.daedan.festabook.presentation.explore.adapter

import com.daedan.festabook.domain.model.University

fun interface OnUniversityClickListener {
    fun onUniversityClick(university: University)
}

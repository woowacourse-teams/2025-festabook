package com.daedan.festabook.presentation.placeMap

import com.daedan.festabook.domain.model.TimeTag

interface OnTimeTagSelectedListener {
    fun onTimeTagSelected(item: TimeTag)

    fun onNothingSelected()
}

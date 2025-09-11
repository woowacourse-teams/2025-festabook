package com.daedan.festabook.presentation.home

import com.daedan.festabook.domain.model.LineupItem
import java.time.LocalDateTime

data class LineUpItemGroupUiModel(
    private val map: Map<LocalDateTime, List<LineupItemUiModel>>,
) {
    fun getLineupItems(date: LocalDateTime): List<LineupItemUiModel> = map[date] ?: emptyList()
}

fun Map<LocalDateTime, List<LineupItem>>.toUiModel(): LineUpItemGroupUiModel =
    LineUpItemGroupUiModel(
        map =
            this.mapValues { (_, lineupItems) ->
                lineupItems.map { it.toUiModel() }
            },
    )

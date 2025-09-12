package com.daedan.festabook.presentation.home

import com.daedan.festabook.domain.model.LineupItem
import java.time.LocalDate
import java.time.LocalDateTime

data class LineUpItemOfDayUiModel(
    val id: Long,
    val date: LocalDate,
    val isDDay: Boolean,
    val lineupItems: List<LineupItemUiModel>,
)

data class LineUpItemGroupUiModel(
    private val map: Map<LocalDate, List<LineupItemUiModel>>,
    private val today: LocalDate = LocalDate.now(),
) {
    fun getLineupItems(): List<LineUpItemOfDayUiModel> =
        map.entries.map {
            LineUpItemOfDayUiModel(
                id = it.hashCode().toLong(),
                date = it.key,
                isDDay = it.key == today,
                lineupItems = it.value,
            )
        }
}

fun Map<LocalDate, List<LineupItem>>.toUiModel(): LineUpItemGroupUiModel =
    LineUpItemGroupUiModel(
        map =
            this.mapValues { (_, lineupItems) ->
                lineupItems.map { it.toUiModel() }
            },
    )

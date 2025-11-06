package com.daedan.festabook.presentation.placeMap.mapManager

import com.daedan.festabook.presentation.placeMap.model.PlaceCategoryUiModel

/**
 * 지도상의 마커들을 카테고리 또는 시간 태그를 기준으로 필터링하는 기능을 정의합니다.
 */
interface MapFilterManager {
    /**
     * 주어진 카테고리 목록을 기준으로 마커를 필터링합니다.
     * 필터링된 마커와 현재 선택된 마커만 지도에 표시됩니다.
     *
     * @param categories 필터링에 사용할 장소 카테고리 목록입니다.
     */
    fun filterMarkersByCategories(categories: List<PlaceCategoryUiModel>)

    /**
     * 주어진 시간 태그 ID를 기준으로 마커를 필터링합니다.
     * TimeTag.EMTPY_TIME_TAG_ID가 전달되면 모든 필터를 해제합니다.
     *
     * @param selectedTimeTagId 필터링에 사용할 시간 태그의 ID입니다. null 또는 특정 ID가 될 수 있습니다.
     */
    fun filterMarkersByTimeTag(selectedTimeTagId: Long?)

    /**
     * 모든 필터링 조건을 해제하고 마커를 초기 상태로 복원합니다.
     */
    fun clearFilter()
}

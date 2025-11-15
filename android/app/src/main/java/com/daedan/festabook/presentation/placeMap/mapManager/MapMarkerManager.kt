package com.daedan.festabook.presentation.placeMap.mapManager

import com.daedan.festabook.presentation.placeMap.model.PlaceCoordinateUiModel
import com.naver.maps.map.overlay.Marker

/**
 * 지도상의 마커 선택 및 선택 해제를 관리하는 인터페이스입니다.
 * 마커의 아이콘 및 상태 업데이트를 처리합니다.
 */
interface MapMarkerManager {
    /**
     * 주어진 마커의 아이콘 및 캡션 관련 속성을 선택 상태에 따라 설정합니다.
     *
     * @param marker 아이콘을 설정할 마커 객체입니다.
     * @param isSelected 마커를 선택된 상태로 표시할지 여부입니다 (기본값: false).
     */
    fun setMarkerIcon(
        marker: Marker,
        isSelected: Boolean = false,
    )

    /**
     * 입력한 좌표값에 마커를 생성합니다.
     *
     * @param coordinates 좌표값 리스트입니다.
     */
    fun setupMarker(coordinates: List<PlaceCoordinateUiModel>)

    /**
     * 주어진 placeId와 일치하는 마커를 찾아 선택합니다.
     * 마커가 선택되면 아이콘이 변경되고, 지도 카메라가 해당 위치로 이동합니다.
     *
     * @param placeId 선택할 장소의 고유 ID입니다.
     */
    fun selectMarker(placeId: Long)

    /**
     * 현재 선택된 마커를 선택 해제합니다.
     * 마커가 선택 해제되면 아이콘이 원래대로 돌아가고 상태가 초기화됩니다.
     */
    fun unselectMarker()
}

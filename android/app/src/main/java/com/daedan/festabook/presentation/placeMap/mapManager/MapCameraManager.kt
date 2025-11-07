package com.daedan.festabook.presentation.placeMap.mapManager

import com.daedan.festabook.presentation.placeMap.OnCameraChangeListener
import com.naver.maps.geometry.LatLng

interface MapCameraManager {
    /**
     * 지정된 위치로 카메라를 이동시키거나, 위치가 지정되지 않은 경우 초기 중심으로 이동시킵니다.
     * @param position 카메라를 이동시킬 목표 LatLng (기본값: 초기 중심 좌표)
     */
    fun moveToPosition(position: LatLng)

    /**
     * 서버에서 받아온 초기 위치로 카메라를 이동합니다
     */
    fun moveToPosition()

    /**
     * 카메라 변경 리스너를 설정하고, 카메라 위치가 초기 범위를 벗어났는지 여부를 콜백으로 전달합니다.
     * @param onCameraChangeListener 카메라 변경 시 호출될 콜백 인터페이스
     */
    fun setupBackToInitialPosition(onCameraChangeListener: OnCameraChangeListener)

    /**
     * 카메라 위치와 줌 레벨을 초기 설정값으로 복원합니다.
     */
    fun setCameraInitialPosition()
}

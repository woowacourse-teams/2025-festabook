import { placeCategories } from '../constants/categories';

/**
 * booth 관련 유틸리티 함수들
 */

/**
 * 카테고리가 메인 플레이스인지 확인
 * @param {string} category - 플레이스 카테고리
 * @returns {boolean} - 메인 플레이스 여부
 */
export const isMainPlace = (category) => {
    return !['SMOKING', 'TRASH_CAN'].includes(category);
};

/**
 * 기본값이 null인 경우 기본값을 반환
 * @param {string} defaultValue - 기본값
 * @param {*} nullableValue - null일 수 있는 값
 * @returns {string} - 기본값 또는 원래 값
 */
export const getDefaultValueIfNull = (defaultValue, nullableValue) => {
    return nullableValue === null ? defaultValue : nullableValue;
};

/**
 * booth 객체에 기본값 적용
 * @param {Object} booth - booth 객체
 * @returns {Object} - 기본값이 적용된 booth 객체
 */
export const defaultBooth = (booth) => {
    return {
        id: booth.id,
        category: booth.category,
        placeImages: booth.placeImages,
        placeAnnouncements: booth.placeAnnouncements,
        // 흡연구역, 쓰레기통은 title을 category명으로 세팅
        title: ['SMOKING', 'TRASH_CAN'].includes(booth.category)
            ? placeCategories[booth.category]
            : getDefaultValueIfNull('플레이스 이름을 지정하여 주십시오.', booth.title),
        description: getDefaultValueIfNull('플레이스 설명이 아직 없습니다.', booth.description),
        startTime: getDefaultValueIfNull('00:00', booth.startTime),
        endTime: getDefaultValueIfNull('00:00', booth.endTime),
        location: getDefaultValueIfNull('미지정', booth.location),
        host: getDefaultValueIfNull('미지정', booth.host),
    };
}; 
import React, { useState, useEffect } from 'react';
import { useModal } from '../hooks/useModal';
import { placeCategories } from '../data/categories';
import { placeAPI, timeTagAPI } from '../utils/api';
import { PlacePageSkeleton } from '../components/common/Skeleton';
import { getCategoryIcon } from '../components/icons/CategoryIcons';
import Modal from '../components/common/Modal';
import MapSelector from '../components/map/MapSelector';


// 메인 플레이스 카드 (이미지 포함)
const MainPlaceCard = ({ place, onEdit, onDelete, onImageManage, onCoordinateEdit, showToast }) => {
    const [isHovered, setIsHovered] = useState(false);
    const [imageLoading, setImageLoading] = useState(true);

    // 카테고리별 색상 매핑 (배경만 연하게, 글씨는 검정색)
    const getCategoryColor = (category) => {
        const colorMap = {
            'BOOTH': 'bg-blue-50 text-gray-900 border-blue-100',
            'FOOD_TRUCK': 'bg-green-50 text-gray-900 border-green-100',
            'BAR': 'bg-orange-50 text-gray-900 border-orange-100',
        };
        return colorMap[category] || 'bg-gray-50 text-gray-900 border-gray-100';
    };

    const mainImage = place.images && place.images.length > 0 ? place.images[0] : null;
    const timeTags = place.timeTags || [];

    return (
        <div
            className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden transition-all duration-300 hover:shadow-lg hover:scale-[1.02] group ring-2 ring-gray-100"
            onMouseEnter={() => setIsHovered(true)}
            onMouseLeave={() => setIsHovered(false)}
        >
            {/* 이미지 섹션 */}
            <div className="relative h-48 bg-gradient-to-br from-gray-100 to-gray-200">
                {mainImage ? (
                    <>
                        {imageLoading && (
                            <div className="absolute inset-0 bg-gray-200 animate-pulse"></div>
                        )}
                        <img
                            src={mainImage}
                            alt={place.title}
                            className={`w-full h-full object-cover transition-opacity duration-300 ${imageLoading ? 'opacity-0' : 'opacity-100'}`}
                            onLoad={() => setImageLoading(false)}
                        />
                        {place.images.length > 1 && (
                            <div className="absolute top-2 right-2 bg-black bg-opacity-75 text-white text-xs px-2 py-1 rounded-full">
                                +{place.images.length - 1}
                            </div>
                        )}
                    </>
                ) : (
                    <div className="flex items-center justify-center h-full">
                        <div className="text-center text-gray-400">
                            <i className="fas fa-image text-3xl mb-2"></i>
                            <p className="text-sm">이미지 없음</p>
                        </div>
                    </div>
                )}
                {/* 호버 시 액션 버튼 */}
                <div className={`absolute inset-0 bg-black bg-opacity-50 flex items-center justify-center transition-opacity duration-300 ${isHovered ? 'opacity-100' : 'opacity-0'}`}>
                    <div className="flex space-x-2">
                        <button
                            onClick={(e) => {
                                e.stopPropagation();
                                onImageManage(place);
                            }}
                            className="bg-gray-100 bg-opacity-90 hover:bg-gray-200 text-gray-800 px-3 py-2 rounded-lg text-sm font-semibold transition-all duration-200 hover:scale-105"
                            title="이미지 관리"
                        >
                            <i className="fas fa-images mr-1"></i>
                            이미지
                        </button>
                        <button
                            onClick={(e) => {
                                e.stopPropagation();
                                onCoordinateEdit(place);
                            }}
                            className="bg-indigo-50 bg-opacity-90 hover:bg-indigo-100 text-indigo-700 px-3 py-2 rounded-lg text-sm font-semibold transition-all duration-200 hover:scale-105"
                            title="좌표 수정"
                        >
                            <i className="fas fa-map-marker-alt mr-1"></i>
                            좌표
                        </button>
                        <button
                            onClick={(e) => {
                                e.stopPropagation();
                                onEdit(place);
                            }}
                            className="bg-blue-50 bg-opacity-90 hover:bg-blue-100  text-blue-600 px-3 py-2 rounded-lg text-sm font-semibold transition-all duration-200 hover:scale-105"
                            title="수정"
                        >
                            <i className="fas fa-edit mr-1"></i>
                            수정
                        </button>
                        <button
                            onClick={(e) => {
                                e.stopPropagation();
                                onDelete(place);
                            }}
                            className="bg-red-50 bg-opacity-90 hover:bg-red-100 text-red-600 px-3 py-2 rounded-lg text-sm font-semibold transition-all duration-200 hover:scale-105"
                            title="삭제"
                        >
                            <i className="fas fa-trash mr-1"></i>
                            삭제
                        </button>
                    </div>
                </div>
            </div>

            {/* 콘텐츠 섹션 */}
            <div className="p-4">
                <div className='grid grid-cols-4 gap-1 w-11/12 mb-1'>
                    {timeTags.map(tag => (
                        <div key={tag.timeTagId} className="flex items-center shrink-0">
                            <div className="inline-flex items-center px-1 font-extrabold bg-yellow-200 border-yellow-300 py-1 rounded-lg text-[9px] border">
                                {tag.name}
                            </div>
                        </div>
                    ))}
                </div>


                <div className="flex items-start justify-between mb-3">
                    <div className="flex-1 min-w-0">
                        <h3 className="font-bold text-lg text-gray-900 truncate" title={place.title}>
                            {place.title}
                        </h3>
                        <div className={`inline-flex items-center px-2 py-1 rounded-full text-xs font-semibold border mt-1 ${getCategoryColor(place.category)}`}>
                            <div className="mr-1 flex items-center justify-center">
                                {getCategoryIcon(place.category, 14)}
                            </div>
                            {placeCategories[place.category]}
                        </div>
                    </div>
                </div>

                {/* 상세 정보 */}
                <div className="space-y-2 text-sm text-gray-600">
                    <div className="flex items-center">
                        <i className="fas fa-map-marker-alt w-4 mr-2 text-gray-400"></i>
                        <span className="truncate" title={place.location}>{place.location}</span>
                    </div>
                    <div className="flex items-center">
                        <i className="fas fa-user-friends w-4 mr-2 text-gray-400"></i>
                        <span className="truncate" title={place.host}>{place.host}</span>
                    </div>
                    <div className="flex items-center">
                        <i className="fas fa-clock w-4 mr-2 text-gray-400"></i>
                        <span>{place.startTime} - {place.endTime}</span>
                    </div>
                </div>

                {/* 설명 */}
                {place.description && place.description !== '플레이스 설명이 아직 없습니다.' && (
                    <div className="mt-3 pt-3 border-t border-gray-100">
                        <p className="text-sm text-gray-700 line-clamp-2" title={place.description}>
                            {place.description}
                        </p>
                    </div>
                )}
            </div>
        </div>
    );
};

// 기타 플레이스 카드 (이미지 없음, 간단한 형태)
const OtherPlaceCard = ({ place, onEdit, onDelete, onCoordinateEdit, showToast }) => {
    const [isHovered, setIsHovered] = useState(false);

    // 카테고리별 색상 매핑 (배경만 연하게, 글씨는 검정색)
    const getCategoryColor = (category) => {
        const colorMap = {
            'STAGE': 'bg-purple-50 text-gray-900 border-purple-100',
            'PHOTO_BOOTH': 'bg-pink-50 text-gray-900 border-pink-100',
            'PRIMARY': 'bg-indigo-50 text-gray-900 border-indigo-100',
            'SMOKING': 'bg-gray-100 text-gray-900 border-gray-200',
            'TRASH_CAN': 'bg-gray-100 text-gray-900 border-gray-200',
            'TOILET': 'bg-gray-100 text-gray-900 border-gray-200',
            'PARKING': 'bg-gray-100 text-gray-900 border-gray-200',
            'EXTRA': 'bg-gray-100 text-gray-900 border-gray-200',
        };
        return colorMap[category] || 'bg-gray-100 text-gray-900 border-gray-200';
    };

    return (
        <div
            className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden transition-all duration-300 hover:shadow-lg hover:scale-[1.02] group"
            onMouseEnter={() => setIsHovered(true)}
            onMouseLeave={() => setIsHovered(false)}
        >
            {/* 콘텐츠 섹션 */}
            <div className="p-4">
                <div className="flex items-center justify-between mb-1">
                    <div className="flex-1 min-w-0">
                        <h3 className="font-bold text-lg text-gray-900 truncate" title={place.title}>
                            {place.title}
                        </h3>
                        <div className={`inline-flex items-center px-2 py-1 rounded-full text-xs font-semibold border mt-2 ${getCategoryColor(place.category)}`}>
                            <div className="mr-1 flex items-center justify-center">
                                {getCategoryIcon(place.category, 14)}
                            </div>
                            {placeCategories[place.category]}
                        </div>
                    </div>

                    {/* 호버 시 액션 버튼 */}
                    <div className={`flex space-x-2 transition-opacity duration-300 ${isHovered ? 'opacity-100' : 'opacity-0'}`}>
                        <button
                            onClick={(e) => {
                                e.stopPropagation();
                                onCoordinateEdit(place);
                            }}
                            className="bg-indigo-50 hover:bg-indigo-100 text-indigo-700 px-3 py-2 rounded-lg text-sm font-semibold transition-all duration-200"
                            title="좌표 수정"
                        >
                            <i className="fas fa-map-marker-alt"></i>
                        </button>
                        <button
                            onClick={(e) => {
                                e.stopPropagation();
                                onEdit(place);
                            }}
                            className="bg-blue-50 hover:bg-blue-100 text-blue-600 px-3 py-2 rounded-lg text-sm font-semibold transition-all duration-200"
                            title="수정"
                        >
                            <i className="fas fa-edit"></i>
                        </button>
                        <button
                            onClick={(e) => {
                                e.stopPropagation();
                                onDelete(place);
                            }}
                            className="bg-red-50 hover:bg-red-100 text-red-600 px-3 py-2 rounded-lg text-sm font-semibold transition-all duration-200"
                            title="삭제"
                        >
                            <i className="fas fa-trash"></i>
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

const PlacePage = () => {
    const { openModal, showToast } = useModal();
    const [places, setPlaces] = useState([]);
    const [loading, setLoading] = useState(false);
    const [viewMode, setViewMode] = useState('all'); // 'all', 'main', 'other'
    const [searchTerm, setSearchTerm] = useState('');
    const [timeTags, setTimeTags] = useState([]);
    const [selectedTimeTags, setSelectedTimeTags] = useState([]); // 선택된 시간 태그 ID 배열
    const [coordModalOpen, setCoordModalOpen] = useState(false);
    const [selectedPlace, setSelectedPlace] = useState(null);

    // defaultBooth 메서드
    const getDefaultValueIfNull = (defaultValue, nullableValue) => nullableValue === null ? defaultValue : nullableValue;
    const defaultBooth = (booth) => ({
        placeId: booth.placeId,
        category: booth.category,
        placeImages: booth.placeImages || [],
        title: getDefaultValueIfNull('플레이스 이름을 지정하여 주십시오.', booth.title),
        description: getDefaultValueIfNull('플레이스 설명이 아직 없습니다.', booth.description),
        startTime: getDefaultValueIfNull('00:00', booth.startTime),
        endTime: getDefaultValueIfNull('00:00', booth.endTime),
        location: getDefaultValueIfNull('미지정', booth.location),
        host: getDefaultValueIfNull('미지정', booth.host),
        images: (booth.placeImages || []).map(img => img.imageUrl),
        timeTags: booth.timeTags || [], // 시간 태그 배열 (객체 배열)
    });

    // 시간 태그 목록 불러오기
    useEffect(() => {
        const fetchTimeTags = async () => {
            try {
                const timeTagData = await timeTagAPI.getTimeTags();
                setTimeTags(timeTagData);
            } catch (error) {
                console.error('Failed to fetch time tags:', error);
                showToast('네트워크 문제로 시간 태그를 불러오지 못했습니다. 시간 태그 필터링이 동작하지 않을 수 있습니다.');
            }
        };

        fetchTimeTags();
    }, [showToast]);

    // 시간 태그 체크박스 처리
    const handleTimeTagFilterChange = (tagId, isChecked) => {
        if (isChecked) {
            setSelectedTimeTags(prev => [...prev, tagId]);
        } else {
            setSelectedTimeTags(prev => prev.filter(id => id !== tagId));
        }
    };

    // 시간 태그 추가
    const handleTimeTagAdd = async (data) => {
        if (!data.name || !data.name.trim()) {
            showToast('시간 태그 이름을 입력해주세요.');
            return;
        }
        try {
            await timeTagAPI.createTimeTag({ name: data.name.trim() });
            const timeTagData = await timeTagAPI.getTimeTags();
            setTimeTags(timeTagData);
            showToast('새 시간 태그가 추가되었습니다.');
        } catch (error) {
            showToast(error.message || '시간 태그 추가에 실패했습니다.');
        }
    };

    // 시간 태그 수정
    const handleTimeTagEdit = async (timeTagId, data) => {
        if (!data.name || !data.name.trim()) {
            showToast('시간 태그 이름을 입력해주세요.');
            return;
        }
        try {
            await timeTagAPI.updateTimeTag(timeTagId, { name: data.name.trim() });
            const timeTagData = await timeTagAPI.getTimeTags();
            setTimeTags(timeTagData);
            showToast('시간 태그가 수정되었습니다.');
        } catch (error) {
            showToast(error.message || '시간 태그 수정에 실패했습니다.');
        }
    };

    // 시간 태그 삭제
    const handleTimeTagDelete = async (timeTagId) => {
        try {
            await timeTagAPI.deleteTimeTag(timeTagId);
            const timeTagData = await timeTagAPI.getTimeTags();
            setTimeTags(timeTagData);
            // 삭제된 태그가 선택되어 있다면 선택에서 제거
            setSelectedTimeTags(prev => prev.filter(id => id !== timeTagId));
            showToast('시간 태그가 삭제되었습니다.');
        } catch (error) {
            showToast(error.message || '시간 태그 삭제에 실패했습니다.');
        }
    };

    // 시간 태그 삭제 확인 모달
    const openTimeTagDeleteModal = (tag) => {
        openModal('confirm', {
            title: '시간 태그 삭제 확인',
            message: `'${tag.name}' 시간 태그를 정말 삭제하시겠습니까?`,
            onConfirm: () => handleTimeTagDelete(tag.timeTagId)
        });
    };

    // 플레이스 목록 불러오기
    useEffect(() => {
        const fetchPlaces = async () => {
            try {
                setLoading(true);
                const placesData = await placeAPI.getPlaces();
                setPlaces(placesData.map(defaultBooth));
            } catch (error) {
                showToast('플레이스 목록을 불러오지 못했습니다.');
                console.error('Failed to fetch places:', error);
            } finally {
                setLoading(false);
            }
        };

        fetchPlaces();
    }, []);

    // 플레이스 생성
    const handleCreate = async (data) => {
        if (!data.category) { showToast('카테고리는 필수 항목입니다.'); return; }
        if (!data.title) { showToast('플레이스 이름은 필수 항목입니다.'); return; }
        try {
            setLoading(true);
            await placeAPI.createPlace({
                placeCategory: data.category,
                title: data.title
            });
            const placesData = await placeAPI.getPlaces();
            setPlaces(placesData.map(defaultBooth));
            showToast('새 플레이스가 추가되었습니다.');
        } catch {
            showToast('플레이스 생성에 실패했습니다.');
        } finally {
            setLoading(false);
        }
    };

    // 플레이스 삭제
    const handleDelete = async (placeId) => {
        try {
            setLoading(true);
            await placeAPI.deletePlace(placeId);
            const placesData = await placeAPI.getPlaces();
            setPlaces(placesData.map(defaultBooth));
            showToast('성공적으로 플레이스가 삭제되었습니다.');
        } catch {
            showToast('플레이스 삭제에 실패했습니다.');
        } finally {
            setLoading(false);
        }
    };

    const openDeleteModal = (place) => {
        const isMain = !['SMOKING', 'TRASH_CAN', 'TOILET', 'PARKING', 'PRIMARY', 'STAGE', 'PHOTO_BOOTH', 'EXTRA'].includes(place.category);

        openModal('confirm', {
            title: '플레이스 삭제 확인',
            message: (
                <>
                    '{place.title}' 플레이스를 정말 삭제하시겠습니까?
                    {isMain && (
                        <>
                            <br />
                            <div className="font-bold text-red-500 text-xs">
                                플레이스의 이미지, 세부 정보도 모두 삭제됩니다.
                            </div>
                        </>
                    )}
                </>
            ),
            onConfirm: () => handleDelete(place.placeId)
        });
    };

    // 플레이스 수정
    const handleSave = async (data) => {
        try {
            setLoading(true);
            const placesData = await placeAPI.getPlaces();
            setPlaces(placesData.map(defaultBooth));
            showToast('플레이스 정보가 수정되었습니다.');
        } catch (error) {
            showToast('플레이스 정보 업데이트에 실패했습니다.');
            console.error('Failed to refresh places after update:', error);
        } finally {
            setLoading(false);
        }
    };

    // 이미지 관리
    const handleImageUpdate = async (data) => {
        try {
            const placesData = await placeAPI.getPlaces();
            setPlaces(placesData.map(defaultBooth));
            showToast('플레이스 이미지가 수정되었습니다.');
        } catch (error) {
            console.error('Failed to refresh places after image update:', error);
            setPlaces(prev => prev.map(prevPlace => {
                if (prevPlace.placeId !== data.placeId) return prevPlace;
                return {
                    ...prevPlace,
                    placeImages: data.placeImages || [],
                    images: (data.placeImages || []).map(img => img.imageUrl),
                };
            }));
            showToast('플레이스 이미지가 수정되었습니다.');
        }
    };

    // 필터링된 플레이스 목록
    const filteredPlaces = places.filter(place => {
        const matchesSearch = (place.title || '').toLowerCase().includes(searchTerm.toLowerCase()) ||
            placeCategories[place.category].toLowerCase().includes(searchTerm.toLowerCase()) ||
            (place.host || '').toLowerCase().includes(searchTerm.toLowerCase());

        // 시간 태그 필터링 (선택된 태그가 없거나, 선택된 태그 중 하나라도 플레이스에 포함되어 있으면 통과)
        let matchesTimeTag = true;
        if (selectedTimeTags.length > 0) {
            const selectedTagNames = selectedTimeTags.map(tagId => {
                const tag = timeTags.find(t => t.timeTagId === tagId);
                return tag ? tag.name : null;
            }).filter(name => name !== null);

            matchesTimeTag = selectedTagNames.some(tagName => {
                if (!place.timeTags) return false;

                // timeTags가 문자열 배열인지 객체 배열인지 확인
                const placeTagNames = place.timeTags.map(tag =>
                    typeof tag === 'string' ? tag : tag.name
                );

                return placeTagNames.includes(tagName);
            });
        }

        if (viewMode === 'main') {
            return matchesSearch && matchesTimeTag && !['SMOKING', 'TRASH_CAN', 'TOILET', 'PARKING', 'PRIMARY', 'STAGE', 'PHOTO_BOOTH', 'EXTRA'].includes(place.category);
        } else if (viewMode === 'other') {
            return matchesSearch && matchesTimeTag && ['SMOKING', 'TRASH_CAN', 'TOILET', 'PARKING', 'PRIMARY', 'STAGE', 'PHOTO_BOOTH', 'EXTRA'].includes(place.category);
        }
        return matchesSearch && matchesTimeTag;
    });

    // 카테고리별 그룹화
    const groupedPlaces = filteredPlaces.reduce((groups, place) => {
        const category = place.category;
        if (!groups[category]) {
            groups[category] = [];
        }
        groups[category].push(place);
        return groups;
    }, {});

    // 한국어 조사 자동 판단 함수
    const getKoreanParticle = (word, particle) => {
        if (!word) return '';

        const lastChar = word.charAt(word.length - 1);
        const hasFinalConsonant = (lastChar.charCodeAt(0) - 44032) % 28 !== 0;

        if (particle === '이/가') {
            return hasFinalConsonant ? '이' : '가';
        } else if (particle === '을/를') {
            return hasFinalConsonant ? '을' : '를';
        }
        return '';
    };

    // 카테고리 순서 정의 (원하는 순서대로)
    const categoryOrder = [
        'BOOTH', 'FOOD_TRUCK', 'BAR', 'STAGE', 'PHOTO_BOOTH', 'PRIMARY',
        'SMOKING', 'TRASH_CAN', 'TOILET', 'PARKING', 'EXTRA'
    ];

    // 순서대로 카테고리 정렬 (빈 카테고리도 포함)
    const sortedGroupedPlaces = categoryOrder
        .filter(category => {
            // 필터링된 플레이스가 있는 카테고리만 포함
            if (viewMode === 'main') {
                return !['SMOKING', 'TRASH_CAN', 'TOILET', 'PARKING', 'PRIMARY', 'STAGE', 'PHOTO_BOOTH', 'EXTRA'].includes(category);
            } else if (viewMode === 'other') {
                return ['SMOKING', 'TRASH_CAN', 'TOILET', 'PARKING', 'PRIMARY', 'STAGE', 'PHOTO_BOOTH', 'EXTRA'].includes(category);
            }
            return true; // 전체 보기일 때는 모든 카테고리
        })
        .reduce((sorted, category) => {
            sorted[category] = groupedPlaces[category] || [];
            return sorted;
        }, {});

    return (
        <div className="min-h-screen bg-gray-50">
            {/* 헤더 */}
            <div className="bg-white shadow-sm border-b border-gray-200 mb-6">
                <div className="px-6 py-4">
                    <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-4">
                        <div>
                            <h1 className="text-3xl font-bold text-gray-900">플레이스 관리</h1>
                        </div>

                        <div className="flex items-center gap-3">
                            <button
                                onClick={() => openModal('booth', { onSave: handleCreate })}
                                className="bg-gradient-to-r from-black to-black hover:from-gray-700 hover:to-gray-800 text-white font-semibold py-3 px-6 rounded-lg flex items-center transition-all duration-200 hover:scale-105 shadow-lg"
                            >
                                <i className="fas fa-plus mr-2"></i>
                                새 플레이스 추가
                            </button>
                        </div>
                    </div>
                </div>

                {/* 필터 및 검색 */}
                <div className="px-6 pb-4">
                    <div className="space-y-4">
                        {/* 첫 번째 행: 검색창과 뷰 모드 필터 */}
                        <div className="flex flex-col sm:flex-row gap-4">
                            {/* 검색 */}
                            <div className="flex-1">
                                <div className="relative">
                                    <i className="fas fa-search absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400"></i>
                                    <input
                                        type="text"
                                        placeholder="플레이스 이름이나 카테고리, 운영 주체로 검색..."
                                        value={searchTerm}
                                        onChange={(e) => setSearchTerm(e.target.value)}
                                        className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                    />
                                </div>
                            </div>

                            {/* 뷰 모드 필터 */}
                            <div className="flex bg-gray-100 rounded-lg p-1">
                                <button
                                    onClick={() => setViewMode('all')}
                                    className={`px-4 py-2 rounded-md text-sm font-medium transition-all duration-200 ${viewMode === 'all'
                                        ? 'bg-white text-gray-800 shadow-sm'
                                        : 'text-gray-600 hover:text-gray-900'
                                        }`}
                                >
                                    전체 ({places.length})
                                </button>
                                <button
                                    onClick={() => setViewMode('main')}
                                    className={`px-4 py-2 rounded-md text-sm font-medium transition-all duration-200 ${viewMode === 'main'
                                        ? 'bg-white text-gray-800 shadow-sm'
                                        : 'text-gray-600 hover:text-gray-900'
                                        }`}
                                >
                                    메인 ({places.filter(p => !['SMOKING', 'TRASH_CAN', 'TOILET', 'PARKING', 'PRIMARY', 'STAGE', 'PHOTO_BOOTH', 'EXTRA'].includes(p.category)).length})
                                </button>
                                <button
                                    onClick={() => setViewMode('other')}
                                    className={`px-4 py-2 rounded-md text-sm font-medium transition-all duration-200 ${viewMode === 'other'
                                        ? 'bg-white text-gray-800 shadow-sm'
                                        : 'text-gray-600 hover:text-gray-900'
                                        }`}
                                >
                                    기타 ({places.filter(p => ['SMOKING', 'TRASH_CAN', 'TOILET', 'PARKING', 'PRIMARY', 'STAGE', 'PHOTO_BOOTH', 'EXTRA'].includes(p.category)).length})
                                </button>
                            </div>
                        </div>

                        {/* 두 번째 행: 시간 태그 필터 */}
                        <div className="bg-gray-50 border border-gray-200 rounded-lg p-4">
                            <div className="flex flex-col sm:flex-row sm:items-center gap-4">
                                <div className="flex-1">
                                    <div className="flex items-center gap-3 mb-3">
                                        <h3 className="text-sm font-semibold text-gray-700">시간 태그 필터</h3>
                                        <div className="flex items-center gap-2">
                                            <button
                                                onClick={() => openModal('timeTagAdd', { onSave: handleTimeTagAdd, showToast })}
                                                className="bg-blue-600 hover:bg-blue-700 text-white font-medium py-1 px-3 rounded-md transition-colors duration-200 text-xs"
                                                title="새 시간 태그 추가"
                                            >
                                                <i className="fas fa-plus mr-1"></i>
                                                추가
                                            </button>
                                        </div>
                                    </div>
                                    {timeTags.length > 0 ? (
                                        <div className="flex flex-wrap gap-2">
                                            {timeTags.map(tag => (
                                                <div key={tag.timeTagId} className="group relative">
                                                    <label className="inline-flex items-center space-x-2 cursor-pointer bg-white border border-gray-300 rounded-full px-3 py-1 hover:bg-gray-50 transition-colors duration-150">
                                                        <input
                                                            type="checkbox"
                                                            checked={selectedTimeTags.includes(tag.timeTagId)}
                                                            onChange={(e) => handleTimeTagFilterChange(tag.timeTagId, e.target.checked)}
                                                            className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
                                                        />
                                                        <span className="text-sm text-gray-700 font-medium">{tag.name}</span>
                                                    </label>
                                                    {/* 수정/삭제 버튼 - hover 시 표시 */}
                                                    <div className="absolute -top-1 -right-1 flex space-x-1 opacity-0 group-hover:opacity-100 transition-opacity duration-200">
                                                        <button
                                                            onClick={(e) => {
                                                                e.stopPropagation();
                                                                openModal('timeTagEdit', { timeTag: tag, onSave: handleTimeTagEdit, showToast });
                                                            }}
                                                            className="bg-blue-500 hover:bg-blue-600 text-white rounded-full w-5 h-5 flex items-center justify-center text-xs transition-colors duration-200"
                                                            title="수정"
                                                        >
                                                            <i className="fas fa-edit"></i>
                                                        </button>
                                                        <button
                                                            onClick={(e) => {
                                                                e.stopPropagation();
                                                                openTimeTagDeleteModal(tag);
                                                            }}
                                                            className="bg-red-500 hover:bg-red-600 text-white rounded-full w-5 h-5 flex items-center justify-center text-xs transition-colors duration-200"
                                                            title="삭제"
                                                        >
                                                            <i className="fas fa-trash"></i>
                                                        </button>
                                                    </div>
                                                </div>
                                            ))}
                                        </div>
                                    ) : (
                                        <div className="text-sm text-gray-500">
                                            아직 시간 태그가 없습니다. 새 시간 태그를 추가해보세요.
                                        </div>
                                    )}
                                    {selectedTimeTags.length > 0 && (
                                        <div className="mt-2 text-xs text-gray-500">
                                            {selectedTimeTags.length}개 태그 선택됨
                                        </div>
                                    )}
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* 콘텐츠 */}
            <div className="px-6">
                {loading ? (
                    <PlacePageSkeleton />
                ) : (
                    <div>
                        {filteredPlaces.length === 0 ? (
                            <div className="text-center py-12">
                                <h3 className="text-xl font-semibold text-gray-900 mb-2">
                                    {searchTerm ? '검색 결과가 없습니다' : '등록된 플레이스가 없습니다'}
                                </h3>
                                <p className="text-gray-600 mb-6">
                                    {searchTerm ? '다른 검색어를 시도해보세요' : '첫 번째 플레이스를 추가해보세요'}
                                </p>
                                {!searchTerm && (
                                    <button
                                        onClick={() => openModal('booth', { onSave: handleCreate })}
                                        className="bg-blue-600 hover:bg-blue-700 text-white font-semibold py-2 px-6 rounded-lg transition-colors"
                                    >
                                        첫 번째 플레이스 추가
                                    </button>
                                )}
                            </div>
                        ) : (
                            <div className="space-y-8">
                                {/* 카테고리별 그룹 표시 */}
                                {Object.entries(sortedGroupedPlaces).map(([category, categoryPlaces]) => (
                                    <div key={category}>
                                        <div className="flex items-center mb-4">
                                            <h2 className="text-xl font-bold text-gray-900 flex items-center">
                                                <div className="mr-2 flex items-center justify-center">
                                                    {getCategoryIcon(category, 24, "text-blue-600")}
                                                </div>
                                                {placeCategories[category]}
                                            </h2>
                                            <span className="ml-3 bg-gray-200 text-gray-800 text-sm font-semibold px-2 py-1 rounded-full">
                                                {categoryPlaces.length}
                                            </span>
                                            <button
                                                onClick={() => openModal('booth', { onSave: handleCreate })}
                                                className="bg-gradient-to-r ml-5 from-black to-black hover:from-gray-700 hover:to-gray-800 text-white font-semibold py-1.5 px-3 rounded-lg flex items-center transition-all duration-200 hover:scale-105 shadow-lg"
                                            >
                                                <i className="fas fa-plus mr-2"></i>
                                                새 플레이스 추가
                                            </button>
                                        </div>


                                        {categoryPlaces.length > 0 ? (
                                            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
                                                {categoryPlaces.map(place => {
                                                    const isMainPlace = !['SMOKING', 'TRASH_CAN', 'TOILET', 'PARKING', 'PRIMARY', 'STAGE', 'PHOTO_BOOTH', 'EXTRA'].includes(place.category);

                                                    return isMainPlace ? (
                                                        <MainPlaceCard
                                                            key={place.placeId}
                                                            place={place}
                                                            onEdit={(place) => openModal('placeEdit', { place, onSave: handleSave })}
                                                            onDelete={openDeleteModal}
                                                            onImageManage={(place) => openModal('placeImages', { place, onUpdate: handleImageUpdate })}
                                                            onCoordinateEdit={(place) => { setSelectedPlace(place); setCoordModalOpen(true); }}
                                                            showToast={showToast}
                                                        />
                                                    ) : (
                                                        <OtherPlaceCard
                                                            key={place.placeId}
                                                            place={place}
                                                            onEdit={(place) => openModal('otherPlaceEdit', { place, onSave: handleSave })}
                                                            onDelete={openDeleteModal}
                                                            onCoordinateEdit={(place) => { setSelectedPlace(place); setCoordModalOpen(true); }}
                                                            showToast={showToast}
                                                        />
                                                    );
                                                })}
                                            </div>
                                        ) : (
                                            <div className="text-center py-12 bg-gray-50 rounded-lg border-2 border-dashed border-gray-300">
                                                <i className="fas fa-plus-circle text-4xl text-gray-400 mb-4"></i>
                                                <p className="text-gray-500 text-lg font-medium mb-2">{placeCategories[category]}{getKoreanParticle(placeCategories[category], '이/가')} 없습니다</p>
                                                <p className="text-gray-400 text-sm mb-4">새로운 {placeCategories[category]}{getKoreanParticle(placeCategories[category], '을/를')} 추가해보세요</p>
                                                <button
                                                    onClick={() => openModal('place', {
                                                        onSave: handleCreate,
                                                        initialData: { category: category }
                                                    })}
                                                    className="bg-black hover:bg-gray-700 text-white font-semibold py-2 px-4 rounded-lg transition-colors"
                                                >
                                                    <i className="fas fa-plus mr-2"></i>
                                                    {placeCategories[category]} 추가
                                                </button>
                                            </div>
                                        )}
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
                )}
            </div>
            {coordModalOpen && selectedPlace && (
                <Modal isOpen={coordModalOpen} onClose={() => setCoordModalOpen(false)} maxWidth="max-w-2xl">
                    <h3 className="text-xl font-bold mb-4">{selectedPlace.title} 좌표 설정</h3>
                    <MapSelector
                        placeId={selectedPlace.placeId}
                        onSaved={async () => {
                            setCoordModalOpen(false);
                            try {
                                const placesData = await placeAPI.getPlaces();
                                setPlaces(placesData.map(defaultBooth));
                                showToast('플레이스 좌표가 저장되었습니다.');
                            } catch {
                                showToast('플레이스 좌표 저장 후 목록 갱신에 실패했습니다.');
                            }
                        }}
                    />
                </Modal>
            )}
        </div>
    );
};

export default PlacePage;
import React, { useState, useMemo } from 'react';
import Modal from '../common/Modal';
import { placeCategories } from '../../data/categories';
import { getCategoryIcon } from '../icons/CategoryIcons';

const MAX_SELECTION = 200;

const PlaceCloneModal = ({ places, onClone, onClose, showToast }) => {
    const [selectedPlaces, setSelectedPlaces] = useState([]);
    const [isCloning, setIsCloning] = useState(false);
    const [searchTerm, setSearchTerm] = useState('');
    const [collapsedCategories, setCollapsedCategories] = useState({});

    const filteredPlaces = useMemo(() => {
        const keyword = searchTerm.trim().toLowerCase();
        if (!keyword) return places;
        return places.filter(p => (p.title || '').toLowerCase().includes(keyword));
    }, [places, searchTerm]);

    // 카테고리별로 플레이스 그룹화 (검색 필터 적용 후)
    const groupedPlaces = useMemo(() => {
        return filteredPlaces.reduce((groups, place) => {
            const category = place.category;
            if (!groups[category]) {
                groups[category] = [];
            }
            groups[category].push(place);
            return groups;
        }, {});
    }, [filteredPlaces]);

    // 카테고리 순서 정의
    const categoryOrder = ['BOOTH', 'FOOD_TRUCK', 'BAR', 'SMOKING', 'TRASH_CAN', 'TOILET', 'PARKING', 'PRIMARY', 'STAGE', 'PHOTO_BOOTH', 'EXTRA'];

    const handlePlaceSelect = (placeId) => {
        setSelectedPlaces(prev => {
            if (prev.includes(placeId)) {
                return prev.filter(id => id !== placeId);
            }
            if (prev.length >= MAX_SELECTION) {
                showToast(`최대 ${MAX_SELECTION}개까지 선택할 수 있습니다.`);
                return prev;
            }
            return [...prev, placeId];
        });
    };

    const handleSelectAll = (categoryPlaces) => {
        const categoryPlaceIds = categoryPlaces.map(place => place.placeId);
        const allSelected = categoryPlaceIds.every(id => selectedPlaces.includes(id));
        
        if (allSelected) {
            // 모두 선택된 상태면 해당 카테고리 모두 해제
            setSelectedPlaces(prev => prev.filter(id => !categoryPlaceIds.includes(id)));
        } else {
            // 일부만 선택된 상태면 해당 카테고리 모두 선택
            setSelectedPlaces(prev => {
                const newSelection = [...prev];
                for (const id of categoryPlaceIds) {
                    if (!newSelection.includes(id)) {
                        if (newSelection.length >= MAX_SELECTION) {
                            showToast(`최대 ${MAX_SELECTION}개까지 선택할 수 있습니다.`);
                            break;
                        }
                        newSelection.push(id);
                    }
                }
                return newSelection;
            });
        }
    };

    const handleClone = async () => {
        if (selectedPlaces.length === 0) {
            showToast('복제할 플레이스를 선택해주세요.');
            return;
        }

        setIsCloning(true);
        try {
            await onClone(selectedPlaces);
            showToast(`${selectedPlaces.length}개의 플레이스가 성공적으로 복제되었습니다.`);
            onClose();
        } catch (error) {
            showToast(error.message || '플레이스 복제에 실패했습니다.');
        } finally {
            setIsCloning(false);
        }
    };

    const getCategoryColor = (category) => {
        const colorMap = {
            'BOOTH': 'bg-blue-50 text-gray-900 border-blue-100',
            'FOOD_TRUCK': 'bg-green-50 text-gray-900 border-green-100',
            'BAR': 'bg-orange-50 text-gray-900 border-orange-100',
        };
        return colorMap[category] || 'bg-gray-50 text-gray-900 border-gray-100';
    };

    return (
        <Modal isOpen={true} onClose={onClose} maxWidth="max-w-5xl">
            <div className="p-6">
                <div className="flex items-center justify-between mb-6">
                    <h2 className="text-2xl font-bold text-gray-900">플레이스 복제</h2>
                </div>

                <div className="mb-6">
                    <p className="text-gray-600 mb-4">
                        복제할 플레이스를 선택하세요. 선택한 플레이스들이 동일한 내용으로 새로 생성됩니다.
                    </p>
                    <div className="flex flex-col gap-3">
                        <div className="flex items-center gap-3">
                            <div className="relative flex-1">
                                <i className="fas fa-search absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"></i>
                                <input
                                    type="text"
                                    value={searchTerm}
                                    onChange={(e) => setSearchTerm(e.target.value)}
                                    placeholder="플레이스 이름 검색"
                                    className="w-full pl-10 pr-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                                />
                            </div>
                            <div className="bg-blue-50 border border-blue-200 rounded-lg px-3 py-2">
                                <div className="flex items-center">
                                    <i className="fas fa-info-circle text-blue-600 mr-2"></i>
                                    <span className="text-blue-800 text-sm font-medium">
                                        선택됨: {selectedPlaces.length} / {MAX_SELECTION}
                                    </span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div className="h-[32rem] overflow-y-auto space-y-6">
                    {categoryOrder.map(category => {
                        const categoryPlaces = groupedPlaces[category];
                        if (!categoryPlaces || categoryPlaces.length === 0) return null;

                        const categoryPlaceIds = categoryPlaces.map(place => place.placeId);
                        const selectedCount = categoryPlaceIds.filter(id => selectedPlaces.includes(id)).length;
                        const isAllSelected = selectedCount === categoryPlaceIds.length;
                        const isPartiallySelected = selectedCount > 0 && selectedCount < categoryPlaceIds.length;
                        const isCollapsed = !!collapsedCategories[category];

                        return (
                            <div key={category} className="border border-gray-200 rounded-lg overflow-hidden">
                                <div className={`${getCategoryColor(category)} px-4 py-3 border-b border-gray-200`}>
                                    <div className="flex items-center justify-between">
                                        <div className="flex items-center">
                                            <div className="mr-3 flex items-center justify-center">
                                                {getCategoryIcon(category, 20, "text-gray-700")}
                                            </div>
                                            <h3 className="font-semibold text-lg">
                                                {placeCategories[category]}
                                            </h3>
                                            <span className="ml-2 bg-gray-200 text-gray-800 text-sm font-medium px-2 py-1 rounded-full">
                                                {categoryPlaces.length}개
                                            </span>
                                        </div>
                                        <div className="flex items-center gap-2">
                                            <button
                                                onClick={() => setCollapsedCategories(prev => ({ ...prev, [category]: !prev[category] }))}
                                                className={`px-3 py-1 rounded-lg text-sm font-medium transition-colors ${
                                                    isCollapsed ? 'bg-gray-100 text-gray-600 hover:bg-gray-200' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
                                                }`}
                                            >
                                                {isCollapsed ? '펼치기' : '접기'}
                                            </button>
                                            <button
                                                onClick={() => handleSelectAll(categoryPlaces)}
                                                className={`px-3 py-1 rounded-lg text-sm font-medium transition-colors ${
                                                    isAllSelected
                                                        ? 'bg-blue-600 text-white'
                                                        : isPartiallySelected
                                                        ? 'bg-blue-100 text-blue-700'
                                                        : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
                                                }`}
                                            >
                                                {isAllSelected ? '전체 해제' : '전체 선택'}
                                            </button>
                                        </div>
                                    </div>
                                </div>
                                {!isCollapsed && (
                                    <div className="bg-white p-3">
                                        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-3">
                                            {categoryPlaces.map(place => {
                                                const isSelected = selectedPlaces.includes(place.placeId);
                                                const mainImage = place.images && place.images.length > 0 ? place.images[0] : null;
                                                
                                                return (
                                                    <div
                                                        key={place.placeId}
                                                        className={`relative rounded-lg border ${isSelected ? 'border-blue-500 ring-2 ring-blue-200 bg-blue-50' : 'border-gray-200 bg-white'} hover:shadow transition cursor-pointer`}
                                                        onClick={() => handlePlaceSelect(place.placeId)}
                                                    >
                                                        <div className="absolute top-2 left-2">
                                                            <input
                                                                type="checkbox"
                                                                checked={isSelected}
                                                                onChange={() => handlePlaceSelect(place.placeId)}
                                                                className="w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 rounded focus:ring-blue-500"
                                                            />
                                                        </div>
                                                        {mainImage ? (
                                                            <img src={mainImage} alt={place.title} className="w-full h-24 object-cover rounded-t-lg" />
                                                        ) : (
                                                            <div className="w-full h-24 bg-gray-100 rounded-t-lg flex items-center justify-center text-gray-400">
                                                                <i className="far fa-image"></i>
                                                            </div>
                                                        )}
                                                        <div className="p-3">
                                                            <div className="font-medium text-gray-900 truncate" title={place.title}>{place.title}</div>
                                                            {place.location && (
                                                                <div className="text-xs text-gray-500 mt-1 truncate" title={place.location}>
                                                                    <i className="fas fa-map-marker-alt mr-1"></i>
                                                                    {place.location}
                                                                </div>
                                                            )}
                                                        </div>
                                                    </div>
                                                );
                                            })}
                                        </div>
                                    </div>
                                )}
                            </div>
                        );
                    })}
                </div>

                <div className="flex justify-end gap-3 mt-6 pt-6 border-t border-gray-200">
                    <button
                        onClick={handleClone}
                        disabled={selectedPlaces.length === 0 || isCloning}
                        className={`px-6 py-2 rounded-lg transition-colors flex items-center ${
                            selectedPlaces.length === 0 || isCloning
                                ? 'bg-gray-300 text-gray-500 cursor-not-allowed'
                                : 'bg-blue-600 text-white hover:bg-blue-700'
                        }`}
                    >
                        {isCloning ? (
                            <>
                                <i className="fas fa-spinner fa-spin mr-2"></i>
                                복제 중...
                            </>
                        ) : (
                            <>
                                <i className="fas fa-copy mr-2"></i>
                                선택한 플레이스 복제 ({selectedPlaces.length}개)
                            </>
                        )}
                    </button>
                </div>
            </div>
        </Modal>
    );
};

export default PlaceCloneModal;

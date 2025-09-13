import React, { useState, useEffect } from 'react';
import { useData } from '../hooks/useData';
import { useModal } from '../hooks/useModal';

function formatDate(dateString) {
    const d = new Date(dateString);
    const yyyy = d.getFullYear();
    const mm = String(d.getMonth() + 1).padStart(2, '0');
    const dd = String(d.getDate()).padStart(2, '0');
    const hh = String(d.getHours()).padStart(2, '0');
    const min = String(d.getMinutes()).padStart(2, '0');
    return `${yyyy}-${mm}-${dd} ${hh}:${min}`;
}

const LostItemPage = () => {
    const { lostItems, toggleLostItemStatus, addLostItem, updateLostItem, deleteLostItem, fetchLostItems } = useData();
    const { openModal, showToast } = useModal();
    const [selectedImage, setSelectedImage] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    
    const sortedItems = [...lostItems].sort((a,b) => {
        // pickupStatus가 PENDING인 항목을 먼저 표시
        if (a.pickupStatus === 'PENDING' && b.pickupStatus !== 'PENDING') return -1;
        if (a.pickupStatus !== 'PENDING' && b.pickupStatus === 'PENDING') return 1;
        
        // 같은 상태 내에서는 날짜 내림차순 정렬 (최신 날짜가 위로)
        return new Date(b.createdAt) - new Date(a.createdAt);
    });

    // 초기 로딩 관리
    useEffect(() => {
        if (lostItems && lostItems.length >= 0) {
            setIsLoading(false);
        }
    }, [lostItems]);

    const handleImageClick = (imageUrl) => {
        setSelectedImage(imageUrl);
    };

    const handleCloseDetail = () => {
        setSelectedImage(null);
    };

    const handleSave = async (id, data) => {
        try {
            if (id) {
                // 수정
                await updateLostItem(id, data, showToast);
            } else {
                // 추가
                await addLostItem(data, showToast);
            }
        } catch (error) {
            console.error('Save operation failed:', error);
        }
    };

    return (
        <div>
            <div className="flex justify-between items-center mb-6">
                <h2 className="text-3xl font-bold">분실물 관리</h2>
                <div className="flex gap-2">
                    <button 
                        onClick={() => openModal('lostItem', { onSave: (data) => handleSave(null, data) })} 
                        className="bg-gray-800 hover:bg-gray-900 text-white font-bold py-2 px-4 rounded-lg flex items-center"
                    >
                        <i className="fas fa-plus mr-2"></i> 분실물 등록
                    </button>
                </div>
            </div>
            
            {isLoading ? (
                <div className="col-span-full text-center py-12">
                    <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-gray-900 mb-4"></div>
                    <p className="text-gray-500">분실물 목록을 불러오는 중...</p>
                </div>
            ) : (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 2xl:grid-cols-5 gap-6">
                    {sortedItems.length > 0 ? (
                        sortedItems.map(item => (
                            <div key={item.id} className="bg-white rounded-lg shadow-sm border border-gray-200 p-5 flex flex-col">

                                {/* 정보 컨테이너 */}
                                <div className="px-0.5">
                                    {/* 이미지 */}
                                    <div className="flex justify-center mb-4">
                                        <div
                                            className="relative group cursor-pointer"
                                            onClick={() => handleImageClick(item.imageUrl)}
                                            >
                                            <img
                                                src={item.imageUrl}
                                                onError={(e) => {
                                                e.target.onerror = null;
                                                e.target.src = 'https://placehold.co/200x200/e0e0e0/757575?text=Error';
                                                }}
                                                className="w-57 h-57 object-cover rounded-lg transition-opacity duration-200 select-none"
                                                alt="분실물 이미지"
                                                draggable={false}
                                            />

                                            {/* 어두운 오버레이 */}
                                            <div className="absolute inset-0 bg-black/0 group-hover:bg-black/50 transition-all duration-200 rounded-lg pointer-events-none" />

                                            {/* 텍스트 오버레이 */}
                                            <div className="absolute inset-0 flex items-center justify-center pointer-events-none">
                                                <span className="text-white text-sm font-medium opacity-0 group-hover:opacity-100 transition-opacity duration-200">
                                                클릭하여 상세보기
                                                </span>
                                            </div>
                                        </div>
                                    </div>

                                    {/* 상태 배지 */}
                                    <div className="flex justify-start mb-3">
                                        <span className={`text-sm font-medium px-2.5 py-0.5 rounded-full ${
                                            item.pickupStatus === 'PENDING' 
                                                ? 'bg-yellow-100 text-yellow-800 border border-yellow-200' 
                                                : 'bg-green-100 text-green-800 border border-green-200'
                                        }`}>
                                            {item.pickupStatus === 'PENDING' ? '보관중' : '수령완료'}
                                        </span>
                                    </div>
                                    
                                    {/* 장소 */}
                                    <div className="text-start mb-2">
                                        <p className="text-gray-500 text-base font-medium">
                                            <i className="fas fa-map-marker-alt mr-2 text-gray-400"></i>
                                            {item.storageLocation}
                                        </p>
                                    </div>
                                    
                                    {/* 생성일 */}
                                    <div className="text-start mb-3">
                                        <p className="text-gray-500 text-base font-medium">
                                            <i className="fas fa-calendar mr-2 text-gray-400"></i>
                                            {formatDate(item.createdAt)}
                                        </p>
                                    </div>
                                    
                                    {/* 액션 버튼들 */}
                                    <div className="flex flex-col space-y-2 mt-auto">
                                        <div className="flex justify-start space-x-2">
                                            <button 
                                                onClick={() => openModal('lostItem', { item, onSave: (data) => handleSave(item.id, data) })} 
                                                className="text-blue-600 hover:text-blue-800 font-bold"
                                            >
                                                수정
                                            </button>
                                            <button 
                                                onClick={() => {
                                                    openModal('confirm', {
                                                        title: '분실물 삭제 확인',
                                                        message: `이 분실물을 정말 삭제하시겠습니까?`,
                                                        onConfirm: async () => {
                                                            try {
                                                                await deleteLostItem(item.id, showToast);
                                                            } catch (error) {
                                                                console.error('Delete failed:', error);
                                                            }
                                                        }
                                                    });
                                                }} 
                                                className="text-red-600 hover:text-red-800 font-bold"
                                            >
                                                삭제
                                            </button>
                                        </div>
                                        <button 
                                            onClick={async () => {
                                                try {
                                                    await toggleLostItemStatus(item.id, showToast);
                                                } catch (error) {
                                                    console.error('Status toggle failed:', error);
                                                }
                                            }} 
                                            className={`text-white text-sm py-2 px-0 rounded-md font-bold transition-colors w-full ${
                                                item.pickupStatus === 'PENDING' 
                                                    ? 'bg-blue-500 hover:bg-blue-600 shadow-sm' 
                                                    : 'bg-gray-500 hover:bg-gray-600 shadow-sm'
                                            }`}
                                        >
                                            {item.pickupStatus === 'PENDING' ? '완료 처리' : '보관 처리'}
                                        </button>
                                    </div>
                                </div>
                            </div>
                        ))
                    ) : (
                        <div className="col-span-full text-center py-12">
                            <i className="fas fa-search text-4xl text-gray-400 mb-4"></i>
                            <p className="text-gray-500 mb-4">등록된 분실물이 없습니다</p>
                            <button
                                onClick={() => openModal('lostItem', { onSave: (data) => handleSave(null, data) })}
                                className="bg-gray-800 hover:bg-gray-900 text-white px-4 py-2 rounded-lg transition-colors"
                            >
                                첫 번째 분실물 등록
                            </button>
                        </div>
                    )}
                </div>
            )}
            
            {/* 이미지 상세 보기 오버레이 */}
            {selectedImage && (
                <div 
                    className="fixed inset-0 flex items-center justify-center z-50 pointer-events-none"
                >
                    <div 
                        className="relative pointer-events-auto"
                        onClick={(e) => e.stopPropagation()}
                    >
                        <img
                            src={selectedImage}
                            alt="상세 이미지"
                            className="rounded-lg shadow-2xl select-none"
                            style={{
                                maxWidth: '100vw',
                                maxHeight: '95vh',
                                width: 'auto',
                                height: 'auto',
                                display: 'block'
                            }}
                            draggable={false}
                        />
                        {/* 닫기 버튼 */}
                        <button 
                            onClick={handleCloseDetail}
                            className="absolute top-4 right-4 bg-black bg-opacity-60 text-white p-2 rounded-full hover:bg-opacity-80 transition-colors"
                        >
                            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                            </svg>
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default LostItemPage;
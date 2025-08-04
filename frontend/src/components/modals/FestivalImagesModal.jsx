import React, { useState, useEffect } from 'react';
import Modal from '../common/Modal';
import AddImageModal from './AddImageModal';

const FestivalImagesModal = ({ isOpen, onClose, organization, showToast, openModal }) => {
    const [selectedImage, setSelectedImage] = useState(null);
    const [isReorderMode, setIsReorderMode] = useState(false);
    const [isDeleteMode, setIsDeleteMode] = useState(false);
    const [images, setImages] = useState([]);
    const [draggedItem, setDraggedItem] = useState(null);
    const [imagesToDelete, setImagesToDelete] = useState([]);
    const [showAddImageModal, setShowAddImageModal] = useState(false);

    // ESC 키 이벤트 리스너
    useEffect(() => {
        const handleEscKey = (event) => {
            if (event.key === 'Escape' && isOpen) {
                if (selectedImage) {
                    handleCloseDetail();
                } else if (isReorderMode || isDeleteMode) {
                    handleCancelMode();
                } else {
                    onClose();
                }
            }
        };

        if (isOpen) {
            document.addEventListener('keydown', handleEscKey);
        }

        return () => {
            document.removeEventListener('keydown', handleEscKey);
        };
    }, [selectedImage, isReorderMode, isDeleteMode, onClose, isOpen]);

    // 이미지 데이터 초기화 및 모달 상태 관리
    useEffect(() => {
        if (isOpen) {
            if (organization?.festivalImages) {
                setImages([...organization.festivalImages]);
            }
        } else {
            // 모달이 닫힐 때 모든 상태 초기화
            setSelectedImage(null);
            setIsReorderMode(false);
            setIsDeleteMode(false);
            setDraggedItem(null);
            setImagesToDelete([]);
            setShowAddImageModal(false);
        }
    }, [organization, isOpen]);

    const handleImageClick = (image) => {
        if (!isReorderMode && !isDeleteMode) {
            setSelectedImage(image);
        }
    };

    const handleCloseDetail = () => {
        setSelectedImage(null);
    };

    // 드래그 앤 드롭 함수들
    const handleDragStart = (e, image) => {
        if (isReorderMode) {
            setDraggedItem(image);
            e.dataTransfer.effectAllowed = 'move';
        }
    };

    const handleDragOver = (e) => {
        if (isReorderMode) {
            e.preventDefault();
            e.dataTransfer.dropEffect = 'move';
        }
    };

    const handleDrop = (e, targetImage) => {
        if (isReorderMode && draggedItem && draggedItem.id !== targetImage.id) {
            e.preventDefault();
            
            const draggedIndex = images.findIndex(img => img.id === draggedItem.id);
            const targetIndex = images.findIndex(img => img.id === targetImage.id);
            
            const newImages = [...images];
            const [removed] = newImages.splice(draggedIndex, 1);
            newImages.splice(targetIndex, 0, removed);
            
            setImages(newImages);
            setDraggedItem(null);
        }
    };

    const handleDragEnd = () => {
        setDraggedItem(null);
    };

    // 삭제 모드 함수들
    const handleDeleteToggle = (imageId) => {
        if (isDeleteMode) {
            setImagesToDelete(prev => 
                prev.includes(imageId) 
                    ? prev.filter(id => id !== imageId)
                    : [...prev, imageId]
            );
        }
    };

    const handleSaveOrder = async () => {
        try {
            // TODO: API 호출로 순서 저장
            showToast('이미지 순서가 저장되었습니다.');
            setIsReorderMode(false);
        } catch (error) {
            showToast('순서 저장에 실패했습니다.');
        }
    };

    const handleSaveDeletions = async () => {
        try {
            // TODO: API 호출로 삭제된 이미지들 처리
            const newImages = images.filter(img => !imagesToDelete.includes(img.id));
            setImages(newImages);
            setImagesToDelete([]);
            setIsDeleteMode(false);
            showToast('선택된 이미지들이 삭제되었습니다.');
        } catch (error) {
            showToast('삭제에 실패했습니다.');
        }
    };

    const handleCancelMode = () => {
        setIsReorderMode(false);
        setIsDeleteMode(false);
        setDraggedItem(null);
        setImagesToDelete([]);
        // 원본 데이터로 복원
        if (organization?.festivalImages) {
            setImages([...organization.festivalImages]);
        }
    };

    const handleAddImageClick = () => {
        setShowAddImageModal(true);
    };

    return (
        <Modal isOpen={isOpen} onClose={onClose} maxWidth="max-w-7xl">
            <div className="p-6">
                <div className="flex justify-between items-center mb-6">
                    <h2 className="text-2xl font-bold text-gray-900">축제 이미지 관리</h2>
                    <div className="flex space-x-2">
                        {!isReorderMode && !isDeleteMode && (
                            <>
                                <button 
                                    onClick={() => setIsReorderMode(true)}
                                    className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors"
                                >
                                    순서 변경
                                </button>
                                <button 
                                    onClick={() => setIsDeleteMode(true)}
                                    className="bg-red-600 text-white px-4 py-2 rounded-lg hover:bg-red-700 transition-colors"
                                >
                                    삭제
                                </button>
                                <button 
                                    onClick={handleAddImageClick}
                                    className="bg-black text-white px-4 py-2 rounded-lg hover:bg-gray-800 transition-colors"
                                >
                                    새 이미지 추가
                                </button>
                            </>
                        )}
                        {(isReorderMode || isDeleteMode) && (
                            <>
                                <button 
                                    onClick={isReorderMode ? handleSaveOrder : handleSaveDeletions}
                                    className="bg-green-600 text-white px-4 py-2 rounded-lg hover:bg-green-700 transition-colors"
                                >
                                    {isReorderMode ? '순서 저장' : '삭제 완료'}
                                </button>
                                <button 
                                    onClick={handleCancelMode}
                                    className="bg-gray-500 text-white px-4 py-2 rounded-lg hover:bg-gray-600 transition-colors"
                                >
                                    취소
                                </button>
                            </>
                        )}
                    </div>
                </div>

                {(isReorderMode || isDeleteMode) && (
                    <div className="mb-4 p-3 bg-yellow-50 border border-yellow-200 rounded-lg">
                        <p className="text-yellow-800 text-sm">
                            {isReorderMode 
                                ? '드래그하여 이미지 순서를 변경하세요. 변경사항을 저장하려면 "순서 저장" 버튼을 클릭하세요.'
                                : '삭제할 이미지들을 선택하세요. 선택 완료 후 "삭제 완료" 버튼을 클릭하세요.'
                            }
                        </p>
                    </div>
                )}

                {images && images.length > 0 ? (
                    <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4 max-h-[600px] overflow-y-auto">
                        {images.map((image, index) => (
                            <div
                                key={image.id}
                                className={`relative group cursor-pointer ${
                                    isReorderMode ? 'cursor-move' : ''
                                } ${
                                    draggedItem?.id === image.id ? 'opacity-50' : ''
                                }`}
                                onClick={() => isDeleteMode ? handleDeleteToggle(image.id) : handleImageClick(image)}
                                draggable={isReorderMode}
                                onDragStart={(e) => handleDragStart(e, image)}
                                onDragOver={(e) => handleDragOver(e)}
                                onDrop={(e) => handleDrop(e, image)}
                                onDragEnd={handleDragEnd}
                            >
                                <div className="aspect-[3/4] bg-gray-200 rounded-lg overflow-hidden transition-colors">
                                    <img
                                        src={image.imageUrl}
                                        alt={`축제 이미지 ${index + 1}`}
                                        className="w-full h-full object-cover pointer-events-none"
                                    />
                                    {isDeleteMode && imagesToDelete.includes(image.id) && (
                                        <div className="absolute inset-0 border-4 border-red-500 rounded-lg pointer-events-none"></div>
                                    )}
                                </div>
                                <div className="absolute top-2 right-2 bg-black bg-opacity-75 text-white text-xs px-2 py-1 rounded">
                                    {index + 1}
                                </div>
                                {isDeleteMode && imagesToDelete.includes(image.id) && (
                                    <div className="absolute top-2 left-2 bg-red-500 text-white p-1 rounded-full">
                                        <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                                            <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
                                        </svg>
                                    </div>
                                )}
                                {!isReorderMode && !isDeleteMode && (
                                    <div className="absolute inset-0 bg-black/0 group-hover:bg-black/50 transition-all duration-200 rounded-lg pointer-events-none">
                                        <div className="absolute inset-0 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity duration-200 pointer-events-auto">
                                            <span className="text-white text-sm font-medium">클릭하여 상세보기</span>
                                        </div>
                                    </div>
                                )}
                            </div>
                        ))}
                    </div>
                ) : (
                    <div className="text-center py-12">
                        <svg className="w-16 h-16 text-gray-400 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                        </svg>
                        <p className="text-gray-500 mb-4">축제 이미지가 없습니다</p>
                        <button 
                            onClick={handleAddImageClick}
                            className="bg-black text-white px-4 py-2 rounded-lg hover:bg-gray-800 transition-colors"
                        >
                            첫 번째 이미지 추가
                        </button>
                    </div>
                )}

                {/* 이미지 상세 보기 오버레이 */}
                {selectedImage && (
                    <div 
                        className="fixed inset-0 bg-black bg-opacity-80 flex items-center justify-center z-50"
                        onClick={handleCloseDetail}
                    >
                        <div 
                            className="relative"
                            onClick={(e) => e.stopPropagation()}
                        >
                            <img
                                src={selectedImage.imageUrl}
                                alt="상세 이미지"
                                className="rounded-lg shadow-2xl"
                                style={{
                                    maxWidth: '100vw',
                                    maxHeight: '95vh',
                                    width: 'auto',
                                    height: 'auto',
                                    display: 'block'
                                }}
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

                <div className="flex justify-end mt-6">
                    <button
                        onClick={onClose}
                        className="bg-gray-300 text-gray-700 px-4 py-2 rounded-lg hover:bg-gray-400 transition-colors"
                    >
                        닫기
                    </button>
                </div>
            </div>

            {/* 새 이미지 추가 모달 (중첩) */}
            {showAddImageModal && (
            <AddImageModal
                key="add-image-modal"
                isOpen={true}
                onClose={() => {
                    setShowAddImageModal(false);
                    // 상태 초기화
                    setSelectedImage(null);
                    setIsReorderMode(false);
                    setIsDeleteMode(false);
                    setDraggedItem(null);
                    setImagesToDelete([]);
                }}
                showToast={showToast}
            />
            )}

        </Modal>
    );
};

export default FestivalImagesModal; 
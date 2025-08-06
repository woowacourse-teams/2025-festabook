import React, { useState, useEffect } from 'react';
import Modal from '../common/Modal';
import AddImageModal from './AddImageModal';
import api from '../../utils/api';

const FestivalImagesModal = ({ isOpen, onClose, festival, showToast, openModal, onUpdate }) => {
    const [selectedImage, setSelectedImage] = useState(null);
    const [isReorderMode, setIsReorderMode] = useState(false);
    const [isDeleteMode, setIsDeleteMode] = useState(false);
    const [images, setImages] = useState([]);
    const [draggedItem, setDraggedItem] = useState(null);
    const [selectedImageToDelete, setSelectedImageToDelete] = useState(null);
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

    // 이미지 데이터 정규화 함수
    const normalizeImageData = (imageData) => {
        return imageData.map(image => ({
            ...image,
            id: image.festivalImageId || image.id,
            festivalImageId: image.festivalImageId || image.id
        }));
    };

    // 이미지 데이터 초기화 및 모달 상태 관리
    useEffect(() => {
        if (isOpen) {
            if (festival?.festivalImages) {
                console.log('Original festival images:', festival.festivalImages);
                const normalizedImages = normalizeImageData(festival.festivalImages);
                console.log('Normalized images:', normalizedImages);
                setImages(normalizedImages);
            }
        } else {
            // 모달이 닫힐 때 모든 상태 초기화
            setSelectedImage(null);
            setIsReorderMode(false);
            setIsDeleteMode(false);
            setDraggedItem(null);
            setSelectedImageToDelete(null);
            setShowAddImageModal(false);
        }
    }, [festival, isOpen]);

    const handleImageClick = (image) => {
        if (!isReorderMode && !isDeleteMode) {
            setSelectedImage(image);
        }
    };

    const handleCloseDetail = () => {
        setSelectedImage(null);
    };

    // 드래그 앤 드롭 함수들 (HTML5 기반)
    const handleDragStart = (e, image) => {
        if (isReorderMode) {
            setDraggedItem(image);
            e.dataTransfer.effectAllowed = 'move';
            e.dataTransfer.setData('text/html', '');
        }
    };

    const handleDragOver = (e) => {
        if (isReorderMode) {
            e.preventDefault();
            e.dataTransfer.dropEffect = 'move';
        }
    };

    const handleDrop = (e, targetImage) => {
        e.preventDefault();
        
        if (isReorderMode && draggedItem) {
            const draggedId = draggedItem?.festivalImageId || draggedItem?.id;
            const targetId = targetImage?.festivalImageId || targetImage?.id;
            
            if (draggedId !== targetId) {
                const draggedIndex = images.findIndex(img => (img.festivalImageId || img.id) === draggedId);
                const targetIndex = images.findIndex(img => (img.festivalImageId || img.id) === targetId);
                
                if (draggedIndex !== -1 && targetIndex !== -1) {
                    const newImages = [...images];
                    const [removed] = newImages.splice(draggedIndex, 1);
                    newImages.splice(targetIndex, 0, removed);
                    setImages(newImages);
                }
            }
            
            setDraggedItem(null);
        }
    };

    const handleDragEnd = () => {
        setDraggedItem(null);
    };

    // 삭제 모드 함수들
    const handleDeleteToggle = (imageId, event) => {
        if (isDeleteMode) {
            event.preventDefault();
            event.stopPropagation();
            
            setSelectedImageToDelete(prev => {
                const newSelection = prev === imageId ? null : imageId;
                return newSelection;
            });
        }
    };

    const handleSaveOrder = async () => {
        try {
            const sequences = images.map((image, index) => ({
                festivalImageId: image.festivalImageId || image.id,
                sequence: index
            }));

            const response = await api.patch('/festivals/images/sequences', sequences);
            console.log('Sequence update response:', response.data);
            
            // 상태 업데이트
            if (onUpdate && response.data) {
                const normalizedResponseImages = normalizeImageData(response.data);
                console.log('Normalized sequence response images:', normalizedResponseImages);
                onUpdate(prev => ({
                    ...prev,
                    festivalImages: normalizedResponseImages
                }));
                setImages(normalizedResponseImages);
            }
            
            showToast('이미지 순서가 저장되었습니다.');
            setIsReorderMode(false);
        } catch (error) {
            showToast('순서 저장에 실패했습니다.');
        }
    };

    const handleSaveDeletions = async () => {
        if (!selectedImageToDelete) {
            showToast('삭제할 이미지를 선택해주세요.');
            return;
        }

        try {
            // 선택된 이미지 삭제 - festivalImageId 사용
            const imageToDelete = images.find(img => img.id === selectedImageToDelete);
            const festivalImageId = imageToDelete?.festivalImageId || imageToDelete?.id || selectedImageToDelete;
            
            await api.delete(`/festivals/images/${festivalImageId}`);
            
            const newImages = images.filter(img => (img.festivalImageId || img.id) !== selectedImageToDelete);
            setImages(newImages);
            
            // 상태 업데이트
            if (onUpdate) {
                onUpdate(prev => ({
                    ...prev,
                    festivalImages: newImages
                }));
            }
            
            setSelectedImageToDelete(null);
            setIsDeleteMode(false);
            showToast('이미지가 삭제되었습니다.');
        } catch (error) {
            showToast('삭제에 실패했습니다.');
        }
    };

    const handleCancelMode = () => {
        setIsReorderMode(false);
        setIsDeleteMode(false);
        setDraggedItem(null);
        setSelectedImageToDelete(null);
        // 원본 데이터로 복원
        if (festival?.festivalImages) {
            setImages([...festival.festivalImages]);
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
                                : '삭제할 이미지를 하나 선택하세요. 선택 완료 후 "삭제 완료" 버튼을 클릭하세요.'
                            }
                        </p>
                    </div>
                )}

                {images && images.length > 0 ? (
                    <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4 max-h-[600px] overflow-y-auto">
                        {images.map((image, index) => {
                            const imageId = image.festivalImageId || image.id || index;
                            return (
                            <div
                                key={imageId}
                                draggable={isReorderMode}
                                className={`relative group cursor-pointer ${
                                    isReorderMode ? 'cursor-move' : ''
                                } ${
                                    draggedItem?.festivalImageId === image.festivalImageId || draggedItem?.id === image.id ? 'opacity-100' : ''
                                }`}
                                onClick={(e) => {
                                    if (isDeleteMode) {
                                        handleDeleteToggle(imageId, e);
                                    } else if (!isReorderMode) {
                                        handleImageClick(image);
                                    }
                                }}
                                onDragStart={(e) => handleDragStart(e, image)}
                                onDragOver={handleDragOver}
                                onDrop={(e) => handleDrop(e, image)}
                                onDragEnd={handleDragEnd}
                            >
                                <div className="aspect-[3/4] bg-gray-200 rounded-lg overflow-hidden transition-colors">
                                    <img
                                        src={image.imageUrl}
                                        alt={`축제 이미지 ${index + 1}`}
                                        className="w-full h-full object-cover pointer-events-none"
                                    />
                                    {isDeleteMode && selectedImageToDelete === imageId && (
                                        <div className="absolute inset-0 border-4 border-red-500 rounded-lg pointer-events-none"></div>
                                    )}
                                </div>
                                <div className="absolute top-2 right-2 bg-black bg-opacity-75 text-white text-xs px-2 py-1 rounded">
                                    {index + 1}
                                </div>
                                {isDeleteMode && selectedImageToDelete === imageId && (
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
                            );
                        })}
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
                    setSelectedImageToDelete(null);
                }}
                showToast={showToast}
                onImageAdded={(newImage) => {
                    console.log('New image received:', newImage);
                    const normalizedNewImage = {
                        ...newImage,
                        id: newImage.festivalImageId || newImage.id,
                        festivalImageId: newImage.festivalImageId || newImage.id
                    };
                    console.log('Normalized new image:', normalizedNewImage);
                    const updatedImages = [...images, normalizedNewImage];
                    setImages(updatedImages);
                    if (onUpdate) {
                        onUpdate(prev => ({
                            ...prev,
                            festivalImages: updatedImages
                        }));
                    }
                    setShowAddImageModal(false);
                }}
            />
            )}

        </Modal>
    );
};

export default FestivalImagesModal; 
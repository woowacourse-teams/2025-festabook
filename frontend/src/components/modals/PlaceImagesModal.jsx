import React, { useState, useEffect } from 'react';
import Modal from '../common/Modal';
import AddImageModal from './AddImageModal';
import { placeAPI } from '../../utils/api';

const PlaceImagesModal = ({ place, onUpdate, onClose }) => {
    const [isReorderMode, setIsReorderMode] = useState(false);
    const [isDeleteMode, setIsDeleteMode] = useState(false);
    const [images, setImages] = useState([]);
    const [draggedItem, setDraggedItem] = useState(null);
    const [selectedImageToDelete, setSelectedImageToDelete] = useState(null);
    const [showAddImageModal, setShowAddImageModal] = useState(false);
    const [selectedImage, setSelectedImage] = useState(null);

    // ESC 키 이벤트 리스너
    useEffect(() => {
        const handleEscKey = (event) => {
            if (event.key === 'Escape') {
                if (selectedImage) {
                    handleCloseDetail();
                } else if (isReorderMode || isDeleteMode) {
                    handleCancelMode();
                } else {
                    onClose();
                }
            }
        };

        document.addEventListener('keydown', handleEscKey);
        return () => {
            document.removeEventListener('keydown', handleEscKey);
        };
    }, [selectedImage, isReorderMode, isDeleteMode, onClose]);

    // 이미지 데이터 초기화
    useEffect(() => {
        if (place?.placeImages) {
            console.log('Original place images:', place.placeImages);
            const normalizedImages = place.placeImages.map((image, index) => {
                // 실제 서버 ID를 우선적으로 사용
                const imageId = image.id || image.placeImageId;
                if (!imageId) {
                    console.warn('Image without ID found:', image);
                }
                return {
                    ...image,
                    id: imageId,
                    imageUrl: image.imageUrl,
                    sequence: image.sequence || index + 1
                };
            });
            console.log('Normalized images:', normalizedImages);
            console.log('Image IDs:', normalizedImages.map(img => ({ id: img.id, sequence: img.sequence })));
            setImages(normalizedImages);
        } else {
            setImages([]);
        }
    }, [place?.placeImages]); // place.placeImages가 변경될 때만 실행



    // 드래그 앤 드롭 함수들
    const handleDragStart = (e, image) => {
        if (isReorderMode) {
            setDraggedItem(image);
            e.dataTransfer.effectAllowed = 'move';
            e.dataTransfer.setData('text/html', '');
            
            // 드래그 미리보기 이미지 생성
            const dragImage = new Image();
            dragImage.src = image.imageUrl;
            dragImage.style.borderRadius = '8px';
            dragImage.style.objectFit = 'cover';
            
            // 이미지가 로드된 후 원본 비율에 맞춰 드래그 이미지 설정
            dragImage.onload = () => {
                // 최대 크기 120px로 제한하되 원본 비율 유지
                const maxSize = 120;
                let width = dragImage.naturalWidth;
                let height = dragImage.naturalHeight;
                
                if (width > height) {
                    // 가로가 더 긴 경우
                    if (width > maxSize) {
                        height = (height * maxSize) / width;
                        width = maxSize;
                    }
                } else {
                    // 세로가 더 긴 경우
                    if (height > maxSize) {
                        width = (width * maxSize) / height;
                        height = maxSize;
                    }
                }
                
                dragImage.style.width = width + 'px';
                dragImage.style.height = height + 'px';
                
                e.dataTransfer.setDragImage(dragImage, width / 2, height / 2);
            };
            
            // 즉시 사용할 수 있는 경우 (이미 로드된 이미지)
            if (dragImage.complete) {
                const maxSize = 120;
                let width = dragImage.naturalWidth;
                let height = dragImage.naturalHeight;
                
                if (width > height) {
                    if (width > maxSize) {
                        height = (height * maxSize) / width;
                        width = maxSize;
                    }
                } else {
                    if (height > maxSize) {
                        width = (width * maxSize) / height;
                        height = maxSize;
                    }
                }
                
                dragImage.style.width = width + 'px';
                dragImage.style.height = height + 'px';
                
                e.dataTransfer.setDragImage(dragImage, width / 2, height / 2);
            }
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
            const draggedId = draggedItem.id;
            const targetId = targetImage.id;
            
            console.log('Drop event - draggedId:', draggedId, 'targetId:', targetId);
            
            if (draggedId !== targetId) {
                const draggedIndex = images.findIndex(img => img.id === draggedId);
                const targetIndex = images.findIndex(img => img.id === targetId);
                
                console.log('Drop indices - draggedIndex:', draggedIndex, 'targetIndex:', targetIndex);
                
                const newImages = [...images];
                const [draggedImage] = newImages.splice(draggedIndex, 1);
                newImages.splice(targetIndex, 0, draggedImage);
                
                // sequence 업데이트
                const updatedImages = newImages.map((img, index) => ({
                    ...img,
                    sequence: index + 1
                }));
                
                console.log('Images after reorder:', updatedImages);
                setImages(updatedImages);
            }
        }
    };

    const handleDragEnd = () => {
        setDraggedItem(null);
    };

    const handleDeleteToggle = (imageId, event) => {
        event.stopPropagation();
        console.log('Delete toggle clicked for image ID:', imageId);
        console.log('Current selectedImageToDelete:', selectedImageToDelete);
        const newSelectedId = selectedImageToDelete === imageId ? null : imageId;
        console.log('New selectedImageToDelete will be:', newSelectedId);
        setSelectedImageToDelete(newSelectedId);
    };

    const handleSaveOrder = async () => {
        try {
            console.log('=== handleSaveOrder called ===');
            console.log('Current images before saving order:', images);
            
            // 현재 이미지 배열에서 유효한 이미지만 필터링하고 sequence 재설정
            const validImages = images.filter(img => img.id && 
                                                   img.id !== undefined && 
                                                   img.id !== null && 
                                                   !img.id.toString().startsWith('temp-'));
            
            console.log('Valid images:', validImages);
            
            if (validImages.length === 0) {
                alert('유효한 이미지가 없습니다.');
                return;
            }
            
            // API 호출하여 순서 변경 - 현재 화면 순서대로 sequence 1부터 설정
            const sequences = validImages.map((img, index) => ({
                placeImageId: img.id,
                sequence: index + 1
            }));
            
            console.log('Sequences to send to API:', sequences);
            console.log('API request body:', JSON.stringify(sequences, null, 2));
            
            // API 호출
            await placeAPI.updatePlaceImageSequences(sequences);
            console.log('Sequences updated successfully');
            
            // 서버에서 최신 데이터를 다시 가져와서 업데이트
            const response = await placeAPI.getPlaces();
            const updatedPlace = response.find(p => p.placeId === place.placeId);
            if (updatedPlace) {
                const serverImages = updatedPlace.placeImages || [];
                console.log('Updated images from server (order):', serverImages);
                // 로컬 상태 업데이트
                setImages(serverImages);
                // 부모 컴포넌트에 업데이트 전달
                onUpdate({ placeId: place.placeId, placeImages: serverImages });
            } else {
                console.log('Updated place not found in response (order)');
            }
            
            setIsReorderMode(false);
        } catch (error) {
            console.error('Failed to save image order:', error);
            console.error('Error details:', error.response?.data);
            alert('이미지 순서 변경에 실패했습니다: ' + error.message);
        }
    };

    const handleSaveDeletions = async () => {
        try {
            console.log('handleSaveDeletions called');
            console.log('selectedImageToDelete:', selectedImageToDelete);
            console.log('selectedImageToDelete type:', typeof selectedImageToDelete);
            
            // 유효한 ID인지 확인 (temp-로 시작하지 않는 실제 서버 ID)
            if (selectedImageToDelete && 
                selectedImageToDelete !== undefined && 
                selectedImageToDelete !== null && 
                !selectedImageToDelete.toString().startsWith('temp-')) {
                
                console.log('Deleting image with ID:', selectedImageToDelete);
                // API 호출하여 이미지 삭제
                await placeAPI.deletePlaceImage(selectedImageToDelete);
                console.log('Image deleted successfully');
                
                // 서버에서 최신 데이터를 다시 가져와서 업데이트
                const response = await placeAPI.getPlaces();
                const updatedPlace = response.find(p => p.placeId === place.placeId);
                if (updatedPlace) {
                    const updatedImages = updatedPlace.placeImages || [];
                    console.log('Updated images from server:', updatedImages);
                    // 로컬 상태 업데이트
                    setImages(updatedImages);
                    // 부모 컴포넌트에 업데이트 전달
                    onUpdate({ placeId: place.placeId, placeImages: updatedImages });
                } else {
                    console.log('Updated place not found in response');
                }
            } else {
                console.log('No valid image selected for deletion');
                alert('삭제할 이미지를 선택해주세요.');
                return;
            }
            setIsDeleteMode(false);
            setSelectedImageToDelete(null);
        } catch (error) {
            console.error('Failed to delete images:', error);
            alert('이미지 삭제에 실패했습니다: ' + error.message);
        }
    };

    const handleCancelMode = () => {
        setIsReorderMode(false);
        setIsDeleteMode(false);
        setSelectedImageToDelete(null);
        setDraggedItem(null);
    };

    const handleImageClick = (image) => {
        if (!isReorderMode && !isDeleteMode) {
            setSelectedImage(image);
        }
    };

    const handleCloseDetail = () => {
        setSelectedImage(null);
    };

    const handleAddImageClick = () => {
        setShowAddImageModal(true);
    };

    const handleAddImage = async () => {
        try {
            // 서버에서 최신 데이터를 다시 가져와서 업데이트
            const response = await placeAPI.getPlaces();
            const updatedPlace = response.find(p => p.placeId === place.placeId);
            if (updatedPlace) {
                const normalizedImages = updatedPlace.placeImages?.map((image, index) => ({
                    ...image,
                    id: image.id || image.placeImageId,
                    sequence: image.sequence || index + 1
                })) || [];
                
                onUpdate({ placeId: place.placeId, placeImages: normalizedImages });
                setImages(normalizedImages);
            }
            setShowAddImageModal(false);
            console.log('플레이스 이미지가 성공적으로 추가되었습니다.');
        } catch (error) {
            console.error('Failed to refresh images after adding:', error);
            setShowAddImageModal(false);
        }
    };

    const handleImageUpdate = async () => {
        try {
            // 서버에서 최신 데이터를 다시 가져와서 업데이트
            const response = await placeAPI.getPlaces();
            const updatedPlace = response.find(p => p.placeId === place.placeId);
            if (updatedPlace) {
                const updatedImages = updatedPlace.placeImages || [];
                // 로컬 상태 업데이트
                setImages(updatedImages);
                // 부모 컴포넌트에 업데이트 전달
                onUpdate({ placeId: place.placeId, placeImages: updatedImages });
            }
            onClose();
        } catch (error) {
            console.error('Failed to refresh images:', error);
            // 에러가 발생해도 로컬 상태로 업데이트
            onUpdate({ placeId: place.placeId, placeImages: images });
            onClose();
        }
    };

    return (
        <Modal isOpen={true} onClose={onClose} maxWidth="max-w-7xl">
            <div className="p-6 h-[800px] flex flex-col">
                <div className="flex justify-between items-center mb-6">
                    <h2 className="text-2xl font-bold text-gray-900">플레이스 이미지 관리</h2>
                    <div className="flex space-x-2">
                        {!isReorderMode && !isDeleteMode && (
                            <>
                                <button 
                                    onClick={() => {
                                        console.log('Reorder mode button clicked');
                                        setIsReorderMode(true);
                                    }}
                                    className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors"
                                >
                                    순서 변경
                                </button>
                                <button 
                                    onClick={() => {
                                        console.log('Delete mode button clicked');
                                        setIsDeleteMode(true);
                                    }}
                                    className="bg-red-600 text-white px-4 py-2 rounded-lg hover:bg-red-700 transition-colors"
                                >
                                    삭제
                                </button>
                                <button 
                                    onClick={handleAddImageClick}
                                    className={`px-4 py-2 rounded-lg transition-colors ${
                                        images.length >= 5 
                                            ? 'bg-gray-400 text-gray-600 cursor-not-allowed' 
                                            : 'bg-black text-white hover:bg-gray-800'
                                    }`}
                                    disabled={images.length >= 5}
                                >
                                    새 이미지 추가
                                </button>
                            </>
                        )}
                        {(isReorderMode || isDeleteMode) && (
                            <>
                                <button 
                                    onClick={async () => {
                                        console.log('Save button clicked, isReorderMode:', isReorderMode);
                                        if (isReorderMode) {
                                            console.log('Calling handleSaveOrder...');
                                            await handleSaveOrder();
                                        } else {
                                            console.log('Calling handleSaveDeletions...');
                                            await handleSaveDeletions();
                                        }
                                    }}
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

                <div className="mb-4 p-3 bg-blue-50 border border-blue-200 rounded-lg">
                    <p className="text-blue-800 text-sm">
                        📸 플레이스 이미지는 최대 5개까지 저장할 수 있습니다.
                    </p>
                </div>

                {images && images.length > 0 ? (
                    <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4 flex-1 overflow-y-auto">
                        {images.filter(image => image.id).map((image, index) => {
                            const imageId = image.id;
                            return (
                            <div
                                key={imageId}
                                draggable={isReorderMode}
                                className={`relative group cursor-pointer ${
                                    isReorderMode ? 'cursor-move' : ''
                                } ${
                                    draggedItem?.id === image.id ? 'opacity-100' : ''
                                }`}
                                onClick={(e) => {
                                    if (isDeleteMode) {
                                        console.log('Image clicked for deletion:', image);
                                        handleDeleteToggle(image.id, e);
                                    } else if (!isReorderMode) {
                                        handleImageClick(image);
                                    }
                                }}
                                onDragStart={(e) => handleDragStart(e, image)}
                                onDragOver={handleDragOver}
                                onDrop={(e) => handleDrop(e, image)}
                                onDragEnd={handleDragEnd}
                            >
                                <div className={`aspect-square bg-gray-200 rounded-lg overflow-hidden transition-colors relative ${
                                    draggedItem?.id === image.id ? 'opacity-30 scale-95' : ''
                                }`}>
                                    <img
                                    src={image.imageUrl}
                                    alt={`플레이스 이미지 ${index + 1}`}
                                    className="w-full h-full object-cover pointer-events-auto"
                                    draggable={isReorderMode}
                                    onDragStart={(e) => handleDragStart(e, image)}
                                    onDragEnd={handleDragEnd}
                                    />
                                    {isDeleteMode && selectedImageToDelete === image.id && (
                                        <div className="absolute inset-0 border-4 border-red-500 rounded-lg pointer-events-none"></div>
                                    )}
                                    {isDeleteMode && selectedImageToDelete === image.id && (
                                        <div className="absolute top-2 left-2 bg-red-500 text-white p-1 rounded-full">
                                            <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                                                <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
                                            </svg>
                                        </div>
                                    )}
                                    <div className="absolute top-2 right-2 bg-black bg-opacity-75 text-white text-xs px-2 py-1 rounded">
                                        {image.sequence || index + 1}
                                    </div>
                                    {!isReorderMode && !isDeleteMode && (
                                        <div className="absolute inset-0 bg-black/0 group-hover:bg-black/50 transition-all duration-200 rounded-lg pointer-events-none">
                                            <div className="absolute inset-0 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity duration-200 pointer-events-auto">
                                                <span className="text-white text-sm font-medium">클릭하여 상세보기</span>
                                            </div>
                                        </div>
                                    )}
                                </div>
                            </div>
                            );
                        })}
                    </div>
                ) : (
                    <div className="text-center py-12 text-gray-500">
                        <p className="text-lg">플레이스 이미지가 없습니다.</p>
                        <p className="text-sm mt-2">새 이미지를 추가해보세요.</p>
                    </div>
                )}



                {/* 삭제 및 순서 변경 모드가 아닐 때만 하단 버튼 표시 */}
                {!isReorderMode && !isDeleteMode && (
                    <div className="mt-auto pt-6 flex justify-end space-x-3">
                        <button 
                            onClick={onClose} 
                            className="bg-gray-300 text-gray-700 font-bold py-2 px-4 rounded-lg hover:bg-gray-400 transition-all duration-200"
                        >
                            취소
                        </button>
                        <button 
                            onClick={handleImageUpdate} 
                            className="bg-black text-white font-bold py-2 px-4 rounded-lg hover:bg-gray-800 transition-all duration-200"
                        >
                            저장
                        </button>
                    </div>
                )}
            </div>

            {showAddImageModal && (
                <AddImageModal
                    isOpen={showAddImageModal}
                    onClose={() => setShowAddImageModal(false)}
                    onImageAdded={handleAddImage}
                    placeId={place?.placeId}
                    isPlaceImage={true}
                />
            )}

            {/* 이미지 상세 보기 오버레이 */}
            {selectedImage && (
                <div 
                    className="fixed inset-0 flex items-center justify-center z-50 pointer-events-none bg-black bg-opacity-0"
                    onClick={handleCloseDetail}
                    style={{
                        animation: 'fadeInOverlay 0.25s ease-out forwards'
                    }}
                >
                    <div 
                        className="relative pointer-events-auto transform scale-90 opacity-0"
                        onClick={(e) => e.stopPropagation()}
                        style={{
                            animation: 'zoomInImage 0.25s ease-out 0.05s forwards'
                        }}
                    >
                        <img
                            src={selectedImage.imageUrl}
                            alt="상세 이미지"
                            className="rounded-lg shadow-2xl select-none transition-transform duration-300 hover:scale-105"
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
                            className="absolute top-4 right-4 bg-black bg-opacity-60 text-white p-2 rounded-full hover:bg-opacity-80 transition-all duration-200 transform hover:scale-110 opacity-0"
                            style={{
                                animation: 'fadeInButton 0.2s ease-out 0.15s forwards'
                            }}
                        >
                            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                            </svg>
                        </button>
                    </div>
                </div>
            )}

        </Modal>
    );
};

export default PlaceImagesModal; 
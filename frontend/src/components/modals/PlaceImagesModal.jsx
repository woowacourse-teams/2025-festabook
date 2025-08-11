import React, { useState, useEffect } from 'react';
import Modal from '../common/Modal';
import AddImageModal from './AddImageModal';

const PlaceImagesModal = ({ place, onUpdate, onClose }) => {
    const [isReorderMode, setIsReorderMode] = useState(false);
    const [isDeleteMode, setIsDeleteMode] = useState(false);
    const [images, setImages] = useState([]);
    const [draggedItem, setDraggedItem] = useState(null);
    const [selectedImageToDelete, setSelectedImageToDelete] = useState(null);
    const [showAddImageModal, setShowAddImageModal] = useState(false);

    // ESC 키 이벤트 리스너
    useEffect(() => {
        const handleEscKey = (event) => {
            if (event.key === 'Escape') {
                if (isReorderMode || isDeleteMode) {
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
    }, [isReorderMode, isDeleteMode]);

    // 이미지 데이터 초기화
    useEffect(() => {
        if (place?.placeImages) {
            const normalizedImages = place.placeImages.map(image => ({
                ...image,
                id: image.id,
                imageUrl: image.imageUrl,
                sequence: image.sequence
            }));
            setImages(normalizedImages);
        }
    }, [place]);



    // 드래그 앤 드롭 함수들
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
            const draggedId = draggedItem.id;
            const targetId = targetImage.id;
            
            if (draggedId !== targetId) {
                const draggedIndex = images.findIndex(img => img.id === draggedId);
                const targetIndex = images.findIndex(img => img.id === targetId);
                
                const newImages = [...images];
                const [draggedImage] = newImages.splice(draggedIndex, 1);
                newImages.splice(targetIndex, 0, draggedImage);
                
                // sequence 업데이트
                const updatedImages = newImages.map((img, index) => ({
                    ...img,
                    sequence: index + 1
                }));
                
                setImages(updatedImages);
            }
        }
    };

    const handleDragEnd = () => {
        setDraggedItem(null);
    };

    const handleDeleteToggle = (imageId, event) => {
        event.stopPropagation();
        setSelectedImageToDelete(selectedImageToDelete === imageId ? null : imageId);
    };

    const handleSaveOrder = async () => {
        try {
            // 이미지 순서 저장 로직
            const updatedImages = images.map((img, index) => ({
                ...img,
                sequence: index + 1
            }));
            
            onUpdate({ placeId: place.placeId, placeImages: updatedImages });
            setIsReorderMode(false);
        } catch (error) {
            console.error('Failed to save image order:', error);
        }
    };

    const handleSaveDeletions = async () => {
        try {
            if (selectedImageToDelete) {
                const updatedImages = images.filter(img => img.id !== selectedImageToDelete);
                onUpdate({ placeId: place.placeId, placeImages: updatedImages });
            }
            setIsDeleteMode(false);
            setSelectedImageToDelete(null);
        } catch (error) {
            console.error('Failed to delete images:', error);
        }
    };

    const handleCancelMode = () => {
        setIsReorderMode(false);
        setIsDeleteMode(false);
        setSelectedImageToDelete(null);
        setDraggedItem(null);
    };

    const handleAddImageClick = () => {
        setShowAddImageModal(true);
    };

    const handleAddImage = (newImages) => {
        const maxSequence = images.length > 0 ? Math.max(...images.map(img => img.sequence)) : 0;
        const newImagesWithSequence = newImages.map((img, index) => ({
            ...img,
            id: Date.now() + index, // 임시 ID
            sequence: maxSequence + index + 1
        }));
        
        const updatedImages = [...images, ...newImagesWithSequence];
        setImages(updatedImages);
        setShowAddImageModal(false);
    };

    const handleImageUpdate = () => {
        onUpdate({ placeId: place.placeId, placeImages: images });
        onClose();
    };

    return (
        <Modal isOpen={true} onClose={onClose} maxWidth="max-w-7xl">
            <div className="p-6">
                <div className="flex justify-between items-center mb-6">
                    <h2 className="text-2xl font-bold text-gray-900">플레이스 이미지 관리</h2>
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

                <div className="mb-4 p-3 bg-blue-50 border border-blue-200 rounded-lg">
                    <p className="text-blue-800 text-sm">
                        📸 플레이스 이미지는 최대 5개까지 저장할 수 있습니다.
                    </p>
                </div>

                {(isReorderMode || isDeleteMode) && (
                    <div className="mb-4 p-3 bg-yellow-50 border border-yellow-200 rounded-lg">
                        <p className="text-yellow-800 text-sm">
                            {isReorderMode 
                                ? '드래그하여 플레이스 이미지 순서를 변경하세요. 변경사항을 저장하려면 "순서 저장" 버튼을 클릭하세요.'
                                : '삭제할 플레이스 이미지를 하나 선택하세요. 선택 완료 후 "삭제 완료" 버튼을 클릭하세요.'
                            }
                        </p>
                    </div>
                )}

                {images && images.length > 0 ? (
                    <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4 max-h-[600px] overflow-y-auto">
                        {images.map((image, index) => {
                            const imageId = image.id || `temp-${index}`;
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
                                        handleDeleteToggle(imageId, e);
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
                                        alt={`플레이스 이미지 ${index + 1}`}
                                        className="w-full h-full object-cover pointer-events-none"
                                    />
                                    {isDeleteMode && selectedImageToDelete === imageId && (
                                        <div className="absolute inset-0 border-4 border-red-500 rounded-lg pointer-events-none"></div>
                                    )}
                                </div>
                                <div className="absolute top-2 right-2 bg-black bg-opacity-75 text-white text-xs px-2 py-1 rounded">
                                    {image.sequence || index + 1}
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

                <div className="mt-6 flex justify-end space-x-3">
                    <button 
                        onClick={onClose} 
                        className="bg-gray-200 hover:bg-gray-300 text-gray-800 font-bold py-2 px-4 rounded-lg"
                    >
                        취소
                    </button>
                    <button 
                        onClick={handleImageUpdate} 
                        className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-lg"
                    >
                        저장
                    </button>
                </div>
            </div>

            {showAddImageModal && (
                <AddImageModal
                    isOpen={showAddImageModal}
                    onClose={() => setShowAddImageModal(false)}
                    onSave={handleAddImage}
                    title="플레이스 이미지 추가"
                />
            )}


        </Modal>
    );
};

export default PlaceImagesModal; 
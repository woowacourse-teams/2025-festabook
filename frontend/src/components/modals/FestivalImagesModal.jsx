import React, { useState, useEffect } from 'react';
import Modal from '../common/Modal';

const FestivalImagesModal = ({ isOpen, onClose, organization, showToast }) => {
    const [selectedImage, setSelectedImage] = useState(null);

    // ESC 키 이벤트 리스너
    useEffect(() => {
        const handleEscKey = (event) => {
            if (event.key === 'Escape' && selectedImage) {
                handleCloseDetail();
            }
        };

        if (selectedImage) {
            document.addEventListener('keydown', handleEscKey);
        }

        return () => {
            document.removeEventListener('keydown', handleEscKey);
        };
    }, [selectedImage]);

    const handleImageClick = (image) => {
        setSelectedImage(image);
    };

    const handleCloseDetail = () => {
        setSelectedImage(null);
    };

    const handleDeleteImage = (imageId) => {
        // TODO: API 호출로 이미지 삭제
        showToast('이미지 삭제 기능은 준비 중입니다.');
    };

    const handleEditImage = (imageId) => {
        // TODO: 이미지 수정 모달 열기
        showToast('이미지 수정 기능은 준비 중입니다.');
    };

    return (
        <Modal isOpen={isOpen} onClose={onClose} maxWidth="max-w-7xl">
            <div className="p-6">
                <div className="flex justify-between items-center mb-6">
                    <h2 className="text-2xl font-bold text-gray-900">축제 이미지 관리</h2>
                    <button 
                        onClick={() => showToast('이미지 업로드 기능은 준비 중입니다.')}
                        className="bg-black text-white px-4 py-2 rounded-lg hover:bg-gray-800 transition-colors"
                    >
                        새 이미지 추가
                    </button>
                </div>

                {organization?.festivalImages && organization.festivalImages.length > 0 ? (
                    <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4 max-h-[600px] overflow-y-auto">
                        {organization.festivalImages.map((image, index) => (
                            <div
                                key={image.id}
                                className="relative group cursor-pointer"
                                onClick={() => handleImageClick(image)}
                            >
                            <div className="aspect-[3/4]
                                            bg-gray-200 rounded-lg overflow-hidden
                                            transition-colors">
                                    <img
                                        src={image.imageUrl}
                                        alt={`축제 이미지 ${index + 1}`}
                                        className="w-full h-full object-cover"
                                    />
                                </div>
                                <div className="absolute top-2 right-2 bg-black bg-opacity-75 text-white text-xs px-2 py-1 rounded">
                                    {index + 1}
                                </div>
                                <div className="absolute inset-0
                                                    bg-black/0
                                                    group-hover:bg-black/50
                                                    transition-all duration-200
                                                    rounded-lg
                                                    pointer-events-none">
                                    <div className="absolute inset-0 flex items-center justify-center
                                                    opacity-0 group-hover:opacity-100
                                                    transition-opacity duration-200
                                                    pointer-events-auto">
                                        <span className="text-white text-sm font-medium">클릭하여 상세보기</span>
                                    </div>
                                </div>
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
                            onClick={() => showToast('이미지 업로드 기능은 준비 중입니다.')}
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
        </Modal>
    );
};

export default FestivalImagesModal; 
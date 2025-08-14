import React, { useState, useEffect, useRef } from 'react';
import Modal from '../common/Modal';
import { festivalAPI, placeAPI } from '../../utils/api';

const AddImageModal = ({ isOpen, onClose, showToast, onImageAdded, placeId, isPlaceImage = false }) => {
    // showToast가 없을 경우 기본 함수 사용
    const handleToast = showToast || ((message) => console.log(message));
    const [selectedFile, setSelectedFile] = useState(null);
    const [isDragging, setIsDragging] = useState(false);
    const [isUploading, setIsUploading] = useState(false);
    const fileInputRef = useRef(null);

    // ESC 키 이벤트 리스너
    useEffect(() => {
        const handleEscKey = (event) => {
            if (event.key === 'Escape' && isOpen && !isUploading) {
                event.preventDefault();
                event.stopPropagation();
                onClose();
            }
        };

        if (isOpen) {
            document.addEventListener('keydown', handleEscKey, true);
        }

        return () => {
            document.removeEventListener('keydown', handleEscKey, true);
        };
    }, [isOpen, isUploading, onClose]);

    // 모달이 닫힐 때 상태 초기화
    useEffect(() => {
        if (!isOpen) {
            setSelectedFile(null);
            setIsDragging(false);
            setIsUploading(false);
        }
    }, [isOpen]);

    const handleClose = () => {
        if (!isUploading) {
            onClose();
        }
    };

    const validateFile = (file) => {
        const allowedTypes = ['image/png', 'image/jpeg', 'image/jpg'];
        const maxSize = 5 * 1024 * 1024; // 5MB

        if (!allowedTypes.includes(file.type)) {
            handleToast('PNG, JPG, JPEG 파일만 업로드 가능합니다.');
            return false;
        }

        if (file.size > maxSize) {
            handleToast('파일 크기는 5MB 이하여야 합니다.');
            return false;
        }

        return true;
    };

    const handleFileSelect = (file) => {
        if (validateFile(file)) {
            setSelectedFile(file);
        }
    };

    const handleFileInputChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            handleFileSelect(file);
        }
    };

    const handleDragOver = (e) => {
        e.preventDefault();
        setIsDragging(true);
    };

    const handleDragLeave = (e) => {
        e.preventDefault();
        setIsDragging(false);
    };

    const handleDrop = (e) => {
        e.preventDefault();
        setIsDragging(false);
        
        const files = e.dataTransfer.files;
        if (files.length > 0) {
            handleFileSelect(files[0]);
        }
    };

    const handleUploadClick = () => {
        fileInputRef.current?.click();
    };

    const handleUpload = async () => {
        if (!selectedFile) {
            handleToast('업로드할 이미지를 선택해주세요.');
            return;
        }

        setIsUploading(true);
        
        try {
            const imageUrl = URL.createObjectURL(selectedFile);
            
            let response;
            if (isPlaceImage && placeId) {
                // 플레이스 이미지인 경우
                response = await placeAPI.createPlaceImage(placeId, {
                    imageUrl: imageUrl
                });
            } else {
                // 축제 이미지인 경우
                response = await festivalAPI.addFestivalImage({
                    imageUrl: imageUrl
                });
            }
            
            handleToast('이미지가 성공적으로 업로드되었습니다.');
            
            // 새로 추가된 이미지를 부모 컴포넌트에 전달
            if (onImageAdded && response) {
                onImageAdded(response);
            } else {
                onClose();
            }
        } catch (error) {
            handleToast('이미지 업로드에 실패했습니다.');
        } finally {
            setIsUploading(false);
        }
    };

    return (
        <Modal isOpen={isOpen} onClose={handleClose} maxWidth="max-w-md">
            <div className="p-6">
                <h2 className="text-2xl font-bold text-gray-900 mb-6 text-center">새 이미지 추가</h2>
                
                {/* 이미지 선택 영역 */}
                <div className="mb-6">
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                        이미지 선택
                    </label>
                    <div
                        className={`border-2 border-dashed rounded-lg p-8 text-center cursor-pointer transition-colors ${
                            isDragging 
                                ? 'border-blue-500 bg-blue-50' 
                                : selectedFile 
                                    ? 'border-green-500 bg-green-50' 
                                    : 'border-gray-300 hover:border-gray-400'
                        }`}
                        onClick={handleUploadClick}
                        onDragOver={handleDragOver}
                        onDragLeave={handleDragLeave}
                        onDrop={handleDrop}
                    >
                        {selectedFile ? (
                            <div>
                                <div className="w-16 h-16 mx-auto mb-4 bg-green-100 rounded-full flex items-center justify-center">
                                    <svg className="w-8 h-8 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                                    </svg>
                                </div>
                                <p className="text-green-700 font-medium mb-1">{selectedFile.name}</p>
                                <p className="text-green-600 text-sm">
                                    {(selectedFile.size / 1024 / 1024).toFixed(2)} MB
                                </p>
                                <p className="text-gray-500 text-xs mt-2">클릭하여 다른 파일 선택</p>
                            </div>
                        ) : (
                            <div>
                                <div className="w-16 h-16 mx-auto mb-4 bg-gray-100 rounded-full flex items-center justify-center">
                                    <svg className="w-8 h-8 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
                                    </svg>
                                </div>
                                <p className="text-gray-700 font-medium mb-1">클릭하여 이미지 선택</p>
                                <p className="text-gray-500 text-sm">PNG, JPG, JPEG (최대 5MB)</p>
                                <p className="text-gray-400 text-xs mt-2">또는 파일을 여기에 드래그하세요</p>
                            </div>
                        )}
                    </div>
                    
                    {/* 숨겨진 파일 입력 */}
                    <input
                        ref={fileInputRef}
                        type="file"
                        accept="image/png,image/jpeg,image/jpg"
                        onChange={handleFileInputChange}
                        className="hidden"
                    />
                </div>

                {/* 액션 버튼 */}
                <div className="flex justify-end space-x-3">
                    <button
                        onClick={handleClose}
                        disabled={isUploading}
                        className="px-4 py-2 text-gray-700 bg-gray-200 rounded-lg hover:bg-gray-300 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                        취소
                    </button>
                    <button
                        onClick={handleUpload}
                        disabled={!selectedFile || isUploading}
                        className="px-4 py-2 text-white bg-gray-600 rounded-lg hover:bg-gray-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center"
                    >
                        {isUploading ? (
                            <>
                                <svg className="animate-spin -ml-1 mr-2 h-4 w-4 text-white" fill="none" viewBox="0 0 24 24">
                                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                                </svg>
                                업로드 중...
                            </>
                        ) : (
                            '업로드'
                        )}
                    </button>
                </div>
            </div>
        </Modal>
    );
};

export default AddImageModal; 
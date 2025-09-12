import React, { useState, useEffect, useRef, useCallback } from 'react';
import Modal from '../common/Modal';
import { imageAPI } from '../../utils/api';

const LostItemModal = ({ item, onSave, onClose }) => {
    const [storageLocation, setStorageLocation] = useState('');
    const [selectedFile, setSelectedFile] = useState(null);
    const [isDragging, setIsDragging] = useState(false);
    const [isUploading, setIsUploading] = useState(false);
    const fileInputRef = useRef(null);

    const handleSave = useCallback(async () => {
        if (!storageLocation.trim()) {
            return;
        }

        if (!selectedFile && !item?.imageUrl) {
            return;
        }

        setIsUploading(true);
        
        try {
            let finalImageUrl = item?.imageUrl || '';
            
            if (selectedFile) {
                // 1단계: 이미지 파일을 백엔드 이미지 업로드 API로 전송
                const uploadResponse = await imageAPI.uploadImage(selectedFile);
                finalImageUrl = uploadResponse.imageUrl;
            }
            
            onSave({ 
                storageLocation, 
                imageUrl: finalImageUrl 
            }); 
            onClose();
        } catch (error) {
            console.error('Error uploading image:', error);
            alert('이미지 업로드에 실패했습니다: ' + (error.message || '알 수 없는 오류'));
        } finally {
            setIsUploading(false);
        }
    }, [storageLocation, selectedFile, item?.imageUrl, onSave, onClose]);

    // ESC 키와 Enter 키 이벤트 처리
    useEffect(() => {
        const handleKeyDown = (event) => {
            if (event.key === 'Escape' && !isUploading) {
                onClose();
            } else if (event.key === 'Enter' && !event.shiftKey && !isUploading) {
                event.preventDefault();
                event.stopPropagation();
                
                // Enter 키로 저장 실행
                if (storageLocation.trim() && (selectedFile || item?.imageUrl)) {
                    handleSave();
                }
            }
        };

        document.addEventListener('keydown', handleKeyDown, true);
        return () => document.removeEventListener('keydown', handleKeyDown, true);
    }, [onClose, isUploading, storageLocation, selectedFile, item, handleSave]);

    // 모달이 닫힐 때 상태 초기화
    useEffect(() => {
        if (!item) {
            setStorageLocation('');
            setSelectedFile(null);
            setIsDragging(false);
            setIsUploading(false);
        }
    }, [item]);

    useEffect(() => {
        setStorageLocation(item?.storageLocation || '');
        setSelectedFile(null);
        setIsDragging(false);
        setIsUploading(false);
    }, [item]);

    const validateFile = (file) => {
        const allowedTypes = ['image/png', 'image/jpeg', 'image/jpg'];
        const maxSize = 5 * 1024 * 1024; // 5MB

        if (!allowedTypes.includes(file.type)) {
            return false;
        }

        if (file.size > maxSize) {
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

    const handleClose = () => {
        if (!isUploading) {
            onClose();
        }
    };

    return (
        <Modal isOpen={true} onClose={handleClose} maxWidth="max-w-md">
            <div className="p-6">
                <h2 className="text-2xl font-bold text-gray-900 mb-6 text-center">
                    {item ? '분실물 정보 수정' : '새 분실물 등록'}
                </h2>
                
                {/* 보관 장소 입력 */}
                <div className="mb-6">
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                        보관 장소
                    </label>
                    <input 
                        type="text" 
                        value={storageLocation} 
                        onChange={e => setStorageLocation(e.target.value)} 
                        placeholder="보관 장소를 입력해 주세요 (20자 이내)" 
                        className="block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500" 
                    />
                </div>

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
                                    : item?.imageUrl
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
                        ) : item?.imageUrl ? (
                            <div>
                                <div className="w-16 h-16 mx-auto mb-4 bg-green-100 rounded-full flex items-center justify-center">
                                    <svg className="w-8 h-8 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                                    </svg>
                                </div>
                                <p className="text-green-700 font-medium mb-1">기존 이미지</p>
                                <p className="text-green-600 text-sm">이미지가 선택되어 있습니다</p>
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
                        onClick={handleSave}
                        disabled={!storageLocation.trim() || (!selectedFile && !item?.imageUrl) || isUploading}
                        className="px-4 py-2 text-white bg-gray-600 rounded-lg hover:bg-gray-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center"
                    >
                        {isUploading ? (
                            <>
                                <svg className="animate-spin -ml-1 mr-2 h-4 w-4 text-white" fill="none" viewBox="0 0 24 24">
                                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                                </svg>
                                저장 중...
                            </>
                        ) : (
                            '저장'
                        )}
                    </button>
                </div>
            </div>
        </Modal>
    );
};

export default LostItemModal;

import React, { useState, useEffect, useCallback } from 'react';
import Modal from '../common/Modal';
import { placeCategories } from '../../data/categories';

const PlaceModal = ({ booth, onSave, onClose, showToast, initialData }) => {
    const isEditMode = !!booth;
    const [form, setForm] = useState({});
    const [quantity, setQuantity] = useState(1);
    const [isCreating, setIsCreating] = useState(false);
    const [creationProgress, setCreationProgress] = useState(0);

    // 카테고리 변경시 타이틀 업데이트 함수
    const updateTitleByCategory = useCallback((category, currentForm) => {
        if (!isEditMode && category && placeCategories[category]) {
            return placeCategories[category];
        }
        return currentForm.title || '';
    }, [isEditMode]);

    useEffect(() => {
        const initialForm = booth || { 
            title: '', 
            category: initialData?.category || 'BOOTH', // initialData에서 카테고리 가져오기
            description: '',
            location: '',
            host: '',
            startTime: '',
            endTime: ''
        };
        
        // 새 플레이스 추가시 카테고리에 맞는 기본 타이틀 설정
        if (!isEditMode && initialForm.category) {
            initialForm.title = placeCategories[initialForm.category] || '';
        }
        
        setForm(initialForm);
    }, [booth, isEditMode, initialData]);

    // ESC 키 이벤트 리스너
    useEffect(() => {
        const handleEscKey = (event) => {
            if (event.key === 'Escape') {
                onClose();
            }
        };

        document.addEventListener('keydown', handleEscKey);

        return () => {
            document.removeEventListener('keydown', handleEscKey);
        };
    }, [onClose]);

    const handleChange = e => {
        const { name, value } = e.target;
        
        // 글자 수 제한 체크
        if (name === 'title' && value.length > 255) {
            showToast('플레이스 이름은 255자 이내로 입력해주세요.');
            return;
        }
        
        if (name === 'category' && !isEditMode) {
            // 카테고리 변경시 타이틀도 함께 업데이트
            setForm(prev => ({
                ...prev,
                [name]: value,
                title: updateTitleByCategory(value, prev)
            }));
        } else {
            setForm(prev => ({ ...prev, [name]: value }));
        }
    };

    const handleSave = async () => {
        if (isEditMode) {
            // 수정 모드: 기존 방식
            onSave(form);
            onClose();
        } else {
            // 생성 모드: 여러 개 생성
            if (quantity <= 0) {
                showToast('수량은 1개 이상이어야 합니다.');
                return;
            }
            
            if (quantity > 10) {
                showToast('한 번에 최대 10개까지 생성할 수 있습니다.');
                return;
            }
            
            setIsCreating(true);
            setCreationProgress(0);
            
            try {
                let successCount = 0;
                
                for (let i = 0; i < quantity; i++) {
                    try {
                        await onSave(form);
                        successCount++;
                        setCreationProgress(Math.round(((i + 1) / quantity) * 100));
                    } catch (error) {
                        console.error(`플레이스 ${i + 1} 생성 실패:`, error);
                    }
                    
                    // API 호출 간격 조절 (동시성 문제 방지)
                    if (i < quantity - 1) {
                        await new Promise(resolve => setTimeout(resolve, 500));
                    }
                }
                
                if (successCount === quantity) {
                    showToast(`${quantity}개의 플레이스가 성공적으로 생성되었습니다.`);
                } else if (successCount > 0) {
                    showToast(`${successCount}개의 플레이스가 생성되었습니다. (${quantity - successCount}개 실패)`);
                } else {
                    showToast('플레이스 생성에 실패했습니다.');
                }
                
                onClose();
            } catch (error) {
                console.error('플레이스 생성 중 오류:', error);
                showToast('플레이스 생성 중 오류가 발생했습니다.');
            } finally {
                setIsCreating(false);
                setCreationProgress(0);
            }
        }
    };
    
    return (
        <Modal isOpen={true} onClose={onClose} maxWidth="max-w-sm">
            <h3 className="text-xl font-bold mb-6">{isEditMode ? '플레이스 수정' : '새 플레이스 추가'}</h3>
            <div className={`space-y-4 ${isEditMode ? 'max-h-[60vh] overflow-y-auto pr-2' : ''}`}>
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">카테고리</label>
                    <select 
                        name="category" 
                        value={form.category || ''} 
                        onChange={handleChange} 
                        className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500 bg-white"
                    >
                        {Object.entries(placeCategories).map(([key, value]) => (
                            <option key={key} value={key}>{value}</option>
                        ))}
                    </select>
                </div>
                
                <div>
                    <div className="flex justify-between items-center mb-1">
                        <label className="block text-sm font-medium text-gray-700">이름</label>
                        <span className="text-xs text-gray-500">
                            {(form.title || '').length}/255
                        </span>
                    </div>
                    <input 
                        name="title" 
                        type="text" 
                        value={form.title || ''} 
                        onChange={handleChange} 
                        placeholder="플레이스 이름을 입력해주세요 (255자 이내)" 
                        className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500" 
                    />
                </div>

                {/* 수량 입력 (생성 모드일 때만) */}
                {!isEditMode && (
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">생성 수량</label>
                        <div className="flex items-center space-x-2">
                            <button
                                type="button"
                                onClick={() => setQuantity(Math.max(1, quantity - 1))}
                                className="bg-gray-200 hover:bg-gray-300 text-gray-700 w-10 h-10 rounded flex items-center justify-center font-bold text-lg transition-colors duration-200 disabled:opacity-50 disabled:cursor-not-allowed border border-gray-300 shadow-sm"
                                disabled={quantity <= 1}
                            >
                                −
                            </button>
                            <input
                                type="number"
                                min="1"
                                max="10"
                                value={quantity}
                                onChange={(e) => setQuantity(Math.max(1, Math.min(10, parseInt(e.target.value) || 1)))}
                                className="w-20 text-center border border-gray-300 rounded-md py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500"
                            />
                            <button
                                type="button"
                                onClick={() => setQuantity(Math.min(10, quantity + 1))}
                               className="bg-gray-200 hover:bg-gray-300 text-gray-700 w-10 h-10 rounded flex items-center justify-center font-bold text-lg transition-colors duration-200 disabled:opacity-50 disabled:cursor-not-allowed border border-gray-300 shadow-sm"
                                disabled={quantity >= 10}
                            >
                                +
                            </button>
                        </div>
                        <p className="text-xs text-gray-500 mt-1">한 번에 최대 10개까지 생성 가능</p>
                    </div>
                )}
            
            </div>
            <div className="mt-6 flex justify-end w-full relative z-10">
                <div className="space-x-3">
                    <button 
                        type="button"
                        onClick={onClose} 
                        className="bg-gray-200 hover:bg-gray-300 text-gray-800 font-bold py-2 px-4 rounded-lg transition-colors duration-200"
                        disabled={isCreating}
                    >
                        취소
                    </button>
                    <button 
                        type="button"
                        onClick={handleSave} 
                        className="bg-gray-800 hover:bg-gray-900 text-white font-bold py-2 px-4 rounded-lg transition-colors duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
                        disabled={isCreating}
                    >
                        {isCreating ? (
                            <div className="flex items-center">
                                <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                                생성 중... ({creationProgress}%)
                            </div>
                        ) : (
                            isEditMode ? '수정' : `생성 (${quantity}개)`
                        )}
                    </button>
                </div>
            </div>
            
            {/* 진행률 바 (생성 중일 때만) */}
            {isCreating && (
                <div className="mt-4">
                    <div className="bg-gray-200 rounded-full h-2">
                        <div 
                            className="bg-blue-600 h-2 rounded-full transition-all duration-300"
                            style={{ width: `${creationProgress}%` }}
                        ></div>
                    </div>
                    <p className="text-xs text-gray-500 mt-1 text-center">
                        {quantity}개 중 {Math.ceil((creationProgress / 100) * quantity)}개 생성 완료
                    </p>
                </div>
            )}
        </Modal>
    );
};

export default PlaceModal;

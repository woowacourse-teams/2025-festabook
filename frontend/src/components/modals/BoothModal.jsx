import React, { useState, useEffect, useCallback } from 'react';
import Modal from '../common/Modal';
import { placeCategories } from '../../data/categories';

const BoothModal = ({ booth, onSave, onClose, showToast }) => {
    const isEditMode = !!booth;
    const [form, setForm] = useState({});

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
            category: 'BOOTH', // 하드코딩 제거 - 명시적으로 'BOOTH' 설정
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
    }, [booth, isEditMode]);

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

    const handleSave = () => { 
        onSave(form); 
        onClose(); 
    };
    
    return (
        <Modal isOpen={true} onClose={onClose} maxWidth="max-w-2xl">
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
            
            </div>
            <div className="mt-6 flex justify-between w-full relative z-10">
                <div className="space-x-3">
                    <button 
                        type="button"
                        onClick={onClose} 
                        className="bg-gray-200 hover:bg-gray-300 text-gray-800 font-bold py-2 px-4 rounded-lg transition-colors duration-200"
                    >
                        취소
                    </button>
                    <button 
                        type="button"
                        onClick={handleSave} 
                        className="bg-gray-800 hover:bg-gray-900 text-white font-bold py-2 px-4 rounded-lg transition-colors duration-200"
                    >
                        저장
                    </button>
                </div>
            </div>
        </Modal>
    );
};

export default BoothModal;

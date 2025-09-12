import React, { useState, useEffect } from 'react';
import Modal from '../common/Modal';
import { festivalAPI } from '../../utils/api';

const FestivalInfoModal = ({ isOpen, onClose, festival, showToast, onUpdate }) => {
    const [formData, setFormData] = useState({
        festivalName: festival?.festivalName || '',
        startDate: festival?.startDate ? festival.startDate.split('T')[0] : '',
        endDate: festival?.endDate ? festival.endDate.split('T')[0] : '',
        userVisible: festival?.userVisible ?? true
    });

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        try {
            const requestData = {
                festivalName: formData.festivalName,
                startDate: formData.startDate,
                endDate: formData.endDate,
                userVisible: formData.userVisible
            };
            
            const response = await festivalAPI.updateFestivalInfo(requestData);
            
            // 상태 업데이트
            if (onUpdate) {
                onUpdate(prev => ({
                    ...prev,
                    festivalName: response.festivalName,
                    startDate: response.startDate,
                    endDate: response.endDate,
                    userVisible: response.userVisible
                }));
            }
            
            showToast('축제 정보가 성공적으로 수정되었습니다.');
            onClose();
        } catch (error) {
            console.error('Festival info update error:', error);
            showToast('축제 정보 수정에 실패했습니다.');
        }
    };

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        
        // 글자 수 제한 체크
        if (name === 'festivalName' && value.length > 100) {
            showToast('축제 이름은 100자 이내로 입력해주세요.');
            return;
        }
        
        setFormData(prev => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : (name === 'userVisible' ? value === 'true' : value)
        }));
    };

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

    return (
        <Modal isOpen={isOpen} onClose={onClose} maxWidth="max-w-md">
            <form onSubmit={handleSubmit}>
                <h2 className="text-2xl font-bold mb-6 text-center">축제 정보 수정</h2>
                
                <div className="space-y-4">
                    <div>
                        <div className="flex justify-between items-center mb-2">
                            <label className="block text-sm font-medium text-gray-700">
                                축제 이름
                            </label>
                            <span className="text-xs text-gray-500">
                                {formData.festivalName.length}/100
                            </span>
                        </div>
                        <textarea
                            name="festivalName"
                            value={formData.festivalName}
                            onChange={handleChange}
                            onKeyDown={(e) => {
                                if (e.key === 'Enter' && !e.shiftKey) {
                                    e.preventDefault();
                                    handleSubmit(e);
                                }
                            }}
                            className="block w-full border border-gray-300 rounded-lg shadow-sm py-2 px-3 focus:ring-black focus:border-black resize-none"
                            placeholder="축제 이름을 입력하세요 (50자 이내)"
                            required
                        />
                    </div>
                    
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            시작일
                        </label>
                        <input
                            type="date"
                            name="startDate"
                            value={formData.startDate}
                            onChange={handleChange}
                            className="block w-full border border-gray-300 rounded-lg shadow-sm py-2 px-3 focus:ring-black focus:border-black"
                            required
                        />
                    </div>
                    
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            종료일
                        </label>
                        <input
                            type="date"
                            name="endDate"
                            value={formData.endDate}
                            onChange={handleChange}
                            className="block w-full border border-gray-300 rounded-lg shadow-sm py-2 px-3 focus:ring-black focus:border-black"
                            required
                        />
                    </div>
                    
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            축제 공개 여부
                        </label>
                        <div className="flex items-center space-x-3">
                            <label className="flex items-center">
                                <input
                                    type="radio"
                                    name="userVisible"
                                    value="true"
                                    checked={formData.userVisible === true}
                                    onChange={handleChange}
                                    className="h-4 w-4 text-black border-gray-300 focus:ring-black"
                                />
                                <span className="ml-2 text-sm text-gray-700">공개</span>
                            </label>
                            <label className="flex items-center">
                                <input
                                    type="radio"
                                    name="userVisible"
                                    value="false"
                                    checked={formData.userVisible === false}
                                    onChange={handleChange}
                                    className="h-4 w-4 text-black border-gray-300 focus:ring-black"
                                />
                                <span className="ml-2 text-sm text-gray-700">비공개</span>
                            </label>
                        </div>
                        <p className="mt-1 text-xs text-gray-500">
                            공개: 앱에서 검색 가능 / 비공개: 앱에서 검색 불가능
                        </p>
                    </div>
                    
                </div>
                
                <div className="flex space-x-3 mt-6">
                    <button
                        type="button"
                        onClick={onClose}
                        className="flex-1 bg-gray-300 text-gray-700 font-bold py-2 px-4 rounded-lg hover:bg-gray-400 transition-colors duration-200"
                    >
                        취소
                    </button>
                    <button
                        type="submit"
                        className="flex-1 bg-black text-white font-bold py-2 px-4 rounded-lg hover:bg-gray-800 transition-colors duration-200"
                    >
                        수정
                    </button>
                </div>
            </form>
        </Modal>
    );
};

export default FestivalInfoModal; 
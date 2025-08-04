import React, { useState, useEffect } from 'react';
import Modal from '../common/Modal';

const FestivalInfoModal = ({ isOpen, onClose, organization, showToast }) => {
    const [formData, setFormData] = useState({
        festivalName: organization?.festivalName || '',
        startDate: organization?.startDate ? organization.startDate.split('T')[0] : '',
        endDate: organization?.endDate ? organization.endDate.split('T')[0] : ''
    });

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        try {
            // TODO: API 호출로 축제 정보 업데이트
            // const response = await api.put('/organizations', formData);
            
            showToast('축제 정보가 성공적으로 수정되었습니다.');
            onClose();
        } catch (error) {
            showToast('축제 정보 수정에 실패했습니다.');
        }
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
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

    // 모달 전체에서 엔터 키 감지
    useEffect(() => {
        const handleGlobalKeyDown = (event) => {
            if (event.key === 'Enter' && !event.shiftKey) {
                event.preventDefault();
                handleSubmit(event);
            }
        };

        document.addEventListener('keydown', handleGlobalKeyDown);

        return () => {
            document.removeEventListener('keydown', handleGlobalKeyDown);
        };
    }, [formData]); // formData가 변경될 때마다 이벤트 리스너 재등록

    return (
        <Modal isOpen={isOpen} onClose={onClose} maxWidth="max-w-md">
            <form onSubmit={handleSubmit}>
                <h2 className="text-2xl font-bold mb-6 text-center">축제 정보 수정</h2>
                
                <div className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            축제명
                        </label>
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
                            placeholder="축제명을 입력하세요"
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
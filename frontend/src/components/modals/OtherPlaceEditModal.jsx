import React, { useState, useEffect } from 'react';
import Modal from '../common/Modal';
import { placeAPI } from '../../utils/api';

const OtherPlaceEditModal = ({ place, onSave, onClose, showToast }) => {
    const [form, setForm] = useState({
        title: '',
        category: 'EXTRA'
    });
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (place) {
            setForm({
                title: place.title || '',
                category: place.category || 'EXTRA'
            });
        }
    }, [place]);

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

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!form.title.trim()) {
            showToast('플레이스 이름을 입력해주세요.', 'error');
            return;
        }

        setLoading(true);
        try {
            // ✅ PATCH /places/etc/{placeId} 호출
            await placeAPI.updateEtcPlace(place.placeId, {
                title: form.title.trim()
            });

            showToast('기타 시설이 성공적으로 수정되었습니다.');
            onSave({ placeId: place.placeId }); // 부모에서 최신 데이터 다시 불러오기
            onClose();
        } catch (error) {
            console.error('Failed to update etc place:', error);
            showToast(error.message || '기타 시설 수정에 실패했습니다.', 'error');
        } finally {
            setLoading(false);
        }
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setForm(prev => ({
            ...prev,
            [name]: value
        }));
    };

    return (
        <Modal 
            isOpen={true} 
            onClose={onClose} 
            title="기타 시설 수정"
            maxWidth="max-w-md"
        >
            <form onSubmit={handleSubmit} className="space-y-6">
                {/* 플레이스 이름 */}
                <div>
                    <label className="block text-sm font-semibold text-gray-700 mb-2">
                        플레이스 이름 *
                    </label>
                    <input
                        type="text"
                        name="title"
                        value={form.title}
                        onChange={handleInputChange}
                        className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                        placeholder="플레이스 이름을 입력하세요"
                        autoFocus
                        disabled={loading}
                    />
                </div>

                {/* 버튼 */}
                <div className="mt-6 flex justify-end w-full relative z-10">
                    <div className="space-x-3">
                        <button
                            type="button"
                            onClick={onClose}
                            className="bg-gray-200 hover:bg-gray-300 text-gray-800 font-bold py-2 px-4 rounded-lg transition-colors duration-200"
                            disabled={loading}
                        >
                            취소
                        </button>
                        <button
                            type="submit"
                            className={`${
                                loading ? 'bg-gray-500' : 'bg-gray-800 hover:bg-gray-900'
                            } text-white font-bold py-2 px-4 rounded-lg transition-colors duration-200`}
                            disabled={loading}
                        >
                            {loading ? '수정 중...' : '수정'}
                        </button>
                    </div>
                </div>
            </form>
        </Modal>
    );
};

export default OtherPlaceEditModal;

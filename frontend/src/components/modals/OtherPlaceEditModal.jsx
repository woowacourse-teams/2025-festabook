import React, { useState, useEffect } from 'react';
import Modal from '../common/Modal';
import { placeCategories } from '../../data/categories';

const OtherPlaceEditModal = ({ place, onSave, onClose, showToast }) => {
    const [form, setForm] = useState({
        title: '',
        category: 'EXTRA'
    });

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

    const handleSubmit = (e) => {
        e.preventDefault();
        
        if (!form.title.trim()) {
            showToast('플레이스 이름을 입력해주세요.', 'error');
            return;
        }

        const updatedPlace = {
            ...place,
            title: form.title.trim()
        };

        onSave(updatedPlace);
        showToast('기타 시설이 수정되었습니다.');
        onClose();
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
                    />
                </div>

                {/* 버튼 */}
                <div className="mt-6 flex justify-end w-full relative z-10">
                    <div className="space-x-3">
                        <button
                            type="button"
                            onClick={onClose}
                            className="bg-gray-200 hover:bg-gray-300 text-gray-800 font-bold py-2 px-4 rounded-lg transition-colors duration-200"
                        >
                            취소
                        </button>
                        <button
                            type="submit"
                            className="bg-gray-800 hover:bg-gray-900 text-white font-bold py-2 px-4 rounded-lg transition-colors duration-200"
                        >
                            수정
                        </button>
                    </div>
                </div>
            </form>
        </Modal>
    );
};

export default OtherPlaceEditModal;

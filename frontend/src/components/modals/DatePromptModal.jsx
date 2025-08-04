import React, { useState, useEffect } from 'react';
import Modal from '../common/Modal';
import { getCurrentDate } from '../../utils/date';

const DatePromptModal = ({ onSave, onClose, defaultDate }) => {
    const [date, setDate] = useState(defaultDate || getCurrentDate());

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

    const handleSave = async () => {
        try {
            await onSave(date);
            onClose();
        } catch (error) {
            onClose();
        }
    };

    const handleKeyDown = (e) => {
        if (e.key === 'Enter') {
            e.preventDefault();
            handleSave();
        }
    };

    // 모달 전체에서 엔터 키 감지
    useEffect(() => {
        const handleGlobalKeyDown = (event) => {
            if (event.key === 'Enter') {
                event.preventDefault();
                handleSave();
            }
        };

        document.addEventListener('keydown', handleGlobalKeyDown);

        return () => {
            document.removeEventListener('keydown', handleGlobalKeyDown);
        };
    }, [date]); // date가 변경될 때마다 이벤트 리스너 재등록
    return (
        <Modal isOpen={true} onClose={onClose} maxWidth="max-w-sm">
            <h3 className="text-lg font-bold mb-4">새 날짜 추가</h3>
            <input 
                type="date" 
                value={date} 
                onChange={e => setDate(e.target.value)} 
                onKeyDown={handleKeyDown}
                className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500" 
            />
            <div className="mt-6 flex justify-end space-x-3">
                <button onClick={onClose} className="bg-gray-200 hover:bg-gray-300 text-gray-800 font-bold py-2 px-4 rounded-lg">취소</button>
                <button onClick={handleSave} className="bg-gray-800 hover:bg-gray-900 text-white font-bold py-2 px-4 rounded-lg">추가</button>
            </div>
        </Modal>
    );
};

export default DatePromptModal;

import React, { useEffect } from 'react';
import Modal from './Modal';

const ConfirmModal = ({ title, message, onConfirm, onCancel }) => {
    useEffect(() => {
        const handleKeyPress = (e) => {
            if (e.key === 'Enter') {
                e.preventDefault();
                onConfirm();
            } else if (e.key === 'Escape') {
                e.preventDefault();
                onCancel();
            }
        };

        document.addEventListener('keydown', handleKeyPress);
        return () => {
            document.removeEventListener('keydown', handleKeyPress);
        };
    }, [onConfirm, onCancel]);

    return (
    <Modal isOpen={true} onClose={onCancel} maxWidth="max-w-sm">
        <h3 className="text-lg font-bold mb-4">{title}</h3><p>{message}</p>
        <div className="mt-6 flex justify-end space-x-3">
            <button onClick={onCancel} className="bg-gray-200 hover:bg-gray-300 text-gray-800 font-bold py-2 px-4 rounded-lg">취소</button>
            <button onClick={onConfirm} className="bg-red-600 hover:bg-red-700 text-white font-bold py-2 px-4 rounded-lg">삭제</button>
        </div>
    </Modal>
    );
};

export default ConfirmModal;

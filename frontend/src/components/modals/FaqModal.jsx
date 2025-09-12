import React, { useState, useEffect, useCallback } from 'react';
import Modal from '../common/Modal';

const FaqModal = ({ faq, onSave, onClose, showToast }) => {
    const [question, setQuestion] = useState('');
    const [answer, setAnswer] = useState('');
    useEffect(() => {
        setQuestion(faq?.question || '');
        setAnswer(faq?.answer || '');
    }, [faq]);

    const handleQuestionChange = (e) => {
        const value = e.target.value;
        if (value.length > 255) {
            showToast('질문은 255자 이내로 입력해주세요.');
            return;
        }
        setQuestion(value);
    };

    const handleAnswerChange = (e) => {
        const value = e.target.value;
        if (value.length > 3000) {
            showToast('답변은 3000자 이내로 입력해주세요.');
            return;
        }
        setAnswer(value);
    };
    
    const handleSave = useCallback(() => { 
        onSave({ question, answer }); 
        onClose(); 
    }, [question, answer, onSave, onClose]);
    
    useEffect(() => {
        const handleKeyPress = (e) => {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                handleSave();
            } else if (e.key === 'Escape') {
                e.preventDefault();
                onClose();
            }
        };

        document.addEventListener('keydown', handleKeyPress);
        return () => {
            document.removeEventListener('keydown', handleKeyPress);
        };
    }, [question, answer, onClose, handleSave]);

    return (
        <Modal isOpen={true} onClose={onClose}>
            <h3 className="text-xl font-bold mb-6">{faq ? 'FAQ 수정' : '새 FAQ 등록'}</h3>
            <div className="space-y-4">
                <div>
                    <div className="flex justify-between items-center mb-1">
                        <label className="block text-sm font-medium text-gray-700">질문</label>
                        <span className="text-xs text-gray-500">
                            {question.length}/255
                        </span>
                    </div>
                    <input 
                        type="text" 
                        value={question} 
                        onChange={handleQuestionChange} 
                        placeholder="질문을 작성해 주세요 (255자 이내)" 
                        className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500" 
                    />
                </div>
                <div>
                    <div className="flex justify-between items-center mb-1">
                        <label className="block text-sm font-medium text-gray-700">답변</label>
                        <span className="text-xs text-gray-500">
                            {answer.length}/3000
                        </span>
                    </div>
                    <textarea 
                        value={answer} 
                        onChange={handleAnswerChange} 
                        rows="4" 
                        placeholder="답변을 작성해 주세요 (3000자 이내)" 
                        className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500" 
                    />
                </div>
            </div>
            <div className="mt-6 flex justify-between w-full">
                <div className="space-x-3">
                    <button onClick={onClose} className="bg-gray-200 hover:bg-gray-300 text-gray-800 font-bold py-2 px-4 rounded-lg">취소</button>
                    <button onClick={handleSave} className="bg-gray-800 hover:bg-gray-900 text-white font-bold py-2 px-4 rounded-lg">저장</button>
                </div>
            </div>
        </Modal>
    );
};

export default FaqModal;

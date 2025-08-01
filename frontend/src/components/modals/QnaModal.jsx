import React, { useState, useEffect } from 'react';
import Modal from '../common/Modal';

const QnaModal = ({ qna, onSave, onClose }) => {
    const [question, setQuestion] = useState('');
    const [answer, setAnswer] = useState('');
    useEffect(() => {
        setQuestion(qna?.question || '');
        setAnswer(qna?.answer || '');
    }, [qna]);
    const handleSave = () => { onSave({ question, answer }); onClose(); };
    
    const handleKeyPress = (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            handleSave();
        }
    };

    return (
        <Modal isOpen={true} onClose={onClose}>
            <h3 className="text-xl font-bold mb-6">{qna ? 'QnA 수정' : '새 QnA 등록'}</h3>
            <div className="space-y-4">
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">질문</label>
                    <input 
                        type="text" 
                        value={question} 
                        onChange={e => setQuestion(e.target.value)} 
                        onKeyPress={handleKeyPress}
                        placeholder="[20자 이내로 작성해주세요]" 
                        className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500" 
                    />
                </div>
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">답변</label>
                    <textarea 
                        value={answer} 
                        onChange={e => setAnswer(e.target.value)} 
                        onKeyPress={handleKeyPress}
                        rows="4" 
                        placeholder="사용자가 이해하기 쉽게 친절한 답변을 작성해주세요." 
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

export default QnaModal;

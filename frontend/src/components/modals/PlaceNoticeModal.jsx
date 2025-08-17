import React, { useState, useEffect } from 'react';
import Modal from '../common/Modal';

const PlaceNoticeModal = ({ place, onSave, onClose }) => {
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');

  useEffect(() => {
    setTitle('');
    setContent('');
  }, []);

  const handleSave = () => {
    if (!title.trim() || !content.trim()) {
      alert('제목과 내용을 모두 입력해주세요.');
      return;
    }

    const newNotice = {
      id: Date.now(), // 임시 ID
      title: title.trim(),
      content: content.trim(),
      createdAt: new Date().toISOString()
    };

    onSave({ placeId: place.placeId, newNotice });
    onClose();
  };

  // 전역 Enter 키와 ESC 키 처리
  useEffect(() => {
    const handleKeyDown = (event) => {
      if (event.key === 'Escape') {
        onClose();
      } else if (event.key === 'Enter' && !event.shiftKey) {
        // Shift+Enter가 아닌 Enter만 처리 (textarea에서 줄바꿈 방지)
        event.preventDefault();
        handleSave();
      }
    };

    // 전역 이벤트 리스너 추가 (capture phase 사용)
    document.addEventListener('keydown', handleKeyDown, true);
    
    return () => {
      document.removeEventListener('keydown', handleKeyDown, true);
    };
  }, [title, content]);

  return (
    <Modal isOpen={true} onClose={onClose}>
      <h3 className="text-xl font-bold mb-6">플레이스 공지 생성</h3>
      
      <div className="space-y-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            제목
          </label>
          <input
            type="text"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            placeholder="공지 제목을 입력해주세요 (20자 이내)"
            maxLength={20}
            className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500"
          />
        </div>
        
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            내용
          </label>
          <textarea
            value={content}
            onChange={(e) => setContent(e.target.value)}
            rows="6"
            placeholder="공지 내용을 입력해주세요."
            className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500"
          />
        </div>

        <div className="mt-4 p-3 bg-red-50 border border-red-200 rounded-md">
          <p className="text-red-600 text-sm font-medium">
            ⚠️ 3개를 초과하는 플레이스 공지는 가장 오래된 순으로 삭제됩니다.
          </p>
        </div>
      </div>

      <div className="mt-6 flex justify-end space-x-3">
        <button 
          onClick={onClose} 
          className="bg-gray-200 hover:bg-gray-300 text-gray-800 font-bold py-2 px-4 rounded-lg"
        >
          취소
        </button>
        <button 
          onClick={handleSave} 
          className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-lg"
        >
          생성
        </button>
      </div>
    </Modal>
  );
};

export default PlaceNoticeModal; 
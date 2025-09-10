import React, { useState, useEffect } from "react";
import Modal from "../common/Modal";

const NoticeModal = ({ notice, onSave, onClose }) => {
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [isPinned, setIsPinned] = useState(false);

  // 글자 수 제한 상수
  const TITLE_MAX_LENGTH = 50;
  const CONTENT_MAX_LENGTH = 1000;

  useEffect(() => {
    setTitle(notice?.title || "");
    setContent(notice?.content || "");
  }, [notice]);

  const handleSave = () => {
    // 글자 수 검증
    if (title.length > TITLE_MAX_LENGTH) {
      alert(`제목은 ${TITLE_MAX_LENGTH}자를 초과할 수 없습니다.`);
      return;
    }
    if (content.length > CONTENT_MAX_LENGTH) {
      alert(`내용은 ${CONTENT_MAX_LENGTH}자를 초과할 수 없습니다.`);
      return;
    }
    onSave({ title, content, isPinned });
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
        // 글자 수 검증
        if (title.length <= TITLE_MAX_LENGTH && content.length <= CONTENT_MAX_LENGTH) {
          onSave({ title, content, isPinned });
          onClose();
        }
      }
    };

    // 전역 이벤트 리스너 추가 (capture phase 사용)
    document.addEventListener('keydown', handleKeyDown, true);
    
    return () => {
      document.removeEventListener('keydown', handleKeyDown, true);
    };
  }, [title, content, isPinned, onSave, onClose]); // 의존성 배열 단순화

  // 글자 수 초과 여부 확인
  const isTitleOverflow = title.length > TITLE_MAX_LENGTH;
  const isContentOverflow = content.length > CONTENT_MAX_LENGTH;

  // 저장 버튼 비활성화 조건
  const isSaveDisabled = isTitleOverflow || isContentOverflow;

  return (
    <Modal isOpen={true} onClose={onClose}>
      <h3 className="text-xl font-bold mb-6">
        {notice ? "공지사항 수정" : "새 공지사항"}
      </h3>
      <div className="space-y-6">
        <div>
          <div className="flex justify-between items-center mb-2">
            <label className="block text-sm font-medium text-gray-700">
              제목
            </label>
            <span className={`text-xs ${isTitleOverflow ? 'text-red-500 font-semibold' : 'text-gray-500'}`}>
              {title.length}/{TITLE_MAX_LENGTH}
            </span>
          </div>
          <input
            type="text"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            placeholder="[50자 이내로 작성해주세요]"
            className={`block w-full border rounded-md shadow-sm py-2 px-3 transition-colors duration-200
              ${isTitleOverflow 
                ? 'border-red-500 focus:ring-red-500 focus:border-red-500' 
                : 'border-gray-300 focus:ring-indigo-500 focus:border-indigo-500'
              }`}
          />
        </div>
        <div>
          <div className="flex justify-between items-center mb-2">
            <label className="block text-sm font-medium text-gray-700">
              내용
            </label>
            <span className={`text-xs ${isContentOverflow ? 'text-red-500 font-semibold' : 'text-gray-500'}`}>
              {content.length}/{CONTENT_MAX_LENGTH}
            </span>
          </div>
          <textarea
            value={content}
            onChange={(e) => setContent(e.target.value)}
            rows="6"
            placeholder="공지 내용을 입력해주세요. (1000자 이내)"
            className={`block w-full border rounded-md shadow-sm py-2 px-3 transition-colors duration-200
              ${isContentOverflow 
                ? 'border-red-500 focus:ring-red-500 focus:border-red-500' 
                : 'border-gray-300 focus:ring-indigo-500 focus:border-indigo-500'
              }`}
          />
        </div>
        {!notice && (
          <div className="flex items-center mt-2">
            <input
              id="pin-notice"
              type="checkbox"
              checked={isPinned}
              onChange={(e) => setIsPinned(e.target.checked)}
              className="mr-2"
            />
            <label
              htmlFor="pin-notice"
              className="text-sm text-gray-700 select-none"
            >
              고정 공지 여부
            </label>
          </div>
        )}
      </div>
      <div className="mt-6 flex justify-between w-full">
        <div className="space-x-3">
          <button
            onClick={onClose}
            className="bg-gray-200 hover:bg-gray-300 text-gray-800 font-bold py-2 px-4 rounded-lg"
          >
            취소
          </button>
          <button
            onClick={handleSave}
            disabled={isSaveDisabled}
            className={`font-bold py-2 px-4 rounded-lg transition-colors duration-200 ${
              isSaveDisabled
                ? "bg-gray-300 text-gray-400 cursor-not-allowed"
                : "bg-gray-800 hover:bg-gray-900 text-white"
            }`}
          >
            저장
          </button>
        </div>
      </div>
    </Modal>
  );
};

const NoticeDetailModal = ({ notice, onClose }) => {
  // ESC 키 처리
  useEffect(() => {
    const handleKeyDown = (event) => {
      if (event.key === 'Escape') {
        onClose();
      }
    };

    document.addEventListener('keydown', handleKeyDown, true);
    
    return () => {
      document.removeEventListener('keydown', handleKeyDown, true);
    };
  }, [onClose]);

  return (
    <Modal isOpen={true} onClose={onClose}>
      <h3 className="text-xl font-bold mb-6">공지사항 상세</h3>
      <div className="space-y-4">
        <div>
          <div className="text-lg font-semibold mb-2">{notice.title}</div>
          <div className="text-gray-500 text-sm mb-4">{notice.date}</div>
          <div className="whitespace-pre-line text-gray-800">
            {notice.content}
          </div>
        </div>
      </div>
      <div className="mt-6 flex justify-end w-full">
        <button
          onClick={onClose}
          className="bg-gray-800 hover:bg-gray-900 text-white font-bold py-2 px-4 rounded-lg"
        >
          닫기
        </button>
      </div>
    </Modal>
  );
};

export { NoticeModal as default, NoticeDetailModal };
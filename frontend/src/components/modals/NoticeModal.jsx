import React, { useState, useEffect } from "react";
import Modal from "../common/Modal";

const NoticeModal = ({ notice, onSave, onClose }) => {
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [agreePush, setAgreePush] = useState(false);
  const [isPinned, setIsPinned] = useState(false);
  
  useEffect(() => {
    setTitle(notice?.title || "");
    setContent(notice?.content || "");
  }, [notice]);
  
  const handleSave = () => {
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
        handleSave();
      }
    };

    // 전역 이벤트 리스너 추가 (capture phase 사용)
    document.addEventListener('keydown', handleKeyDown, true);
    
    return () => {
      document.removeEventListener('keydown', handleKeyDown, true);
    };
  }, [title, content, isPinned]); // 의존성 배열에 form 데이터 추가
  
  return (
    <Modal isOpen={true} onClose={onClose}>
      <h3 className="text-xl font-bold mb-6">
        {notice ? "공지사항 수정" : "새 공지사항"}
      </h3>
      <div className="space-y-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            제목
          </label>
          <input
            type="text"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            placeholder="[20자 이내로 작성해주세요]"
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
        {!notice && (
          <div className="flex flex-col gap-2 mt-2">
            <div className="flex items-center">
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
                상단 고정
              </label>
            </div>
            <div className="flex items-center">
              <input
                id="push-agree"
                type="checkbox"
                checked={agreePush}
                onChange={(e) => setAgreePush(e.target.checked)}
                className="mr-2"
              />
              <label
                htmlFor="push-agree"
                className="text-sm text-gray-700 select-none"
              >
                푸시 알림을 사용자에게 전송하시겠습니까?
              </label>
            </div>
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
            className="bg-gray-800 hover:bg-gray-900 text-white font-bold py-2 px-4 rounded-lg"
          >
            저장
          </button>
        </div>
      </div>
    </Modal>
  );
};

// 공지사항 상세보기 모달
export const NoticeDetailModal = ({ notice, onClose }) => {
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
  }, []);

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

export default NoticeModal;
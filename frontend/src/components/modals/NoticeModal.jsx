import React, { useState, useEffect } from "react";
import Modal from "../common/Modal";

const NoticeModal = ({ notice, onSave, onClose, isPlaceNotice = false }) => {
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [agreePush, setAgreePush] = useState(false);
  const [isPinned, setIsPinned] = useState(false);

  // 글자 수 제한 상수
  const TITLE_MAX_LENGTH = 50;
  const CONTENT_MAX_LENGTH = 1000;

  const [agreePush, setAgreePush] = useState(true); // 푸시 알림 동의 상태 추가

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
    onSave({ title, content });
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
        if (!isSaveDisabled) {
          handleSave();
        }
      }
    };

    // 전역 이벤트 리스너 추가 (capture phase 사용)
    document.addEventListener('keydown', handleKeyDown, true);
    
    return () => {
      document.removeEventListener('keydown', handleKeyDown, true);
    };
  }, [title, content, isPinned]); // 의존성 배열에 form 데이터 추가

  // 글자 수 초과 여부 확인
  const isTitleOverflow = title.length > TITLE_MAX_LENGTH;
  const isContentOverflow = content.length > CONTENT_MAX_LENGTH;

  // 저장 버튼 비활성화 조건
  const isSaveDisabled = (!notice && !agreePush) || isTitleOverflow || isContentOverflow;

  }, [title, content, onClose, handleSave]); // 의존성 배열 수정

  // 제목과 내용 길이 체크
  const isTitleOverflow = title.length > 20;
  const isContentOverflow = content.length > 500;
  const isSaveDisabled = (!title.trim() || !content.trim()) || isTitleOverflow || isContentOverflow;

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
            placeholder="[20자 이내로 작성해주세요]"
            className={`mt-1 block w-full border rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500 ${
              isTitleOverflow ? 'border-red-300' : 'border-gray-300'
            }`}
            placeholder="[50자 이내로 작성해주세요]"
            className={`block w-full border rounded-md shadow-sm py-2 px-3 transition-colors duration-200
              ${isTitleOverflow 
                ? 'border-red-500 focus:ring-red-500 focus:border-red-500' 
                : 'border-gray-300 focus:ring-indigo-500 focus:border-indigo-500'
              }`}
          />
          {isTitleOverflow && (
            <p className="text-red-500 text-xs mt-1">제목은 20자 이내로 작성해주세요.</p>
          )}
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
          {isContentOverflow && (
            <p className="text-red-500 text-xs mt-1">내용은 500자 이내로 작성해주세요.</p>
          )}
        </div>
        {!notice && isPlaceNotice && (
          <div className="mt-4 p-3 bg-red-50 border border-red-200 rounded-md">
            <p className="text-red-600 text-sm font-medium">
              ⚠️ 3개를 초과하는 플레이스 공지는 가장 오래된 순으로 삭제됩니다.
            </p>
          </div>
        )}
      </div>
      {!notice && (
        <div className="mt-4 space-y-3">
          <div className="flex items-center">
            <input
              type="checkbox"
              id="agreePush"
              checked={agreePush}
              onChange={(e) => setAgreePush(e.target.checked)}
              className="h-4 w-4 text-indigo-600 focus:ring-indigo-500 border-gray-300 rounded"
            />
            <label htmlFor="agreePush" className="ml-2 text-sm text-gray-700">
              푸시 알림 전송에 동의합니다
            </label>
          </div>
          <p className="text-xs text-gray-500">
            <i className="fas fa-info-circle mr-1"></i>
            새롭게 작성된 공지는 사용자에게 푸시 알림으로 전송됩니다.
          </p>
        </div>
      )}
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
            disabled={isSaveDisabled}
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

export { NoticeModal as default, NoticeDetailModal };
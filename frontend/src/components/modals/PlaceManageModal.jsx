import React, { useState, useEffect } from 'react';
import Modal from '../common/Modal';
import { placeAPI } from '../../utils/api';

const PlaceManageModal = ({ place, onSave, onClose }) => {
  const [notices, setNotices] = useState([]);
  const [isEditMode, setIsEditMode] = useState(false);
  const [editingNotice, setEditingNotice] = useState(null);
  const [newNotice, setNewNotice] = useState({ title: '', content: '' });
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    loadPlaceAnnouncements();
  }, [place]);

  const loadPlaceAnnouncements = async () => {
    if (!place?.placeId) return;
    
    try {
      setIsLoading(true);
      const announcements = await placeAPI.getPlaceAnnouncements(place.placeId);
      setNotices(announcements || []);
    } catch (error) {
      console.error('Failed to load place announcements:', error);
      // API 호출 실패 시 기존 데이터 사용
      if (place?.placeAnnouncements) {
        setNotices([...place.placeAnnouncements]);
      } else {
        setNotices([]);
      }
    } finally {
      setIsLoading(false);
    }
  };

  const handleAddNotice = async () => {
    if (!newNotice.title.trim() || !newNotice.content.trim()) {
      alert('제목과 내용을 모두 입력해주세요.');
      return;
    }

    if (notices.length >= 3) {
      alert('공지는 최대 3개까지 생성할 수 있습니다.');
      return;
    }

    try {
      const newAnnouncement = await placeAPI.createPlaceAnnouncement(place.placeId, {
        title: newNotice.title.trim(),
        content: newNotice.content.trim()
      });

      const updatedNotices = [...notices, newAnnouncement];
      setNotices(updatedNotices);
      setNewNotice({ title: '', content: '' });
      
      // 부모 컴포넌트에 업데이트된 공지 목록 전달
      onSave({ placeId: place.placeId, placeAnnouncements: updatedNotices });
    } catch (error) {
      alert('공지사항 생성에 실패했습니다: ' + error.message);
    }
  };

  const handleEditNotice = (notice) => {
    setEditingNotice({ ...notice });
    setIsEditMode(true);
  };

  const handleSaveEdit = async () => {
    if (!editingNotice.title.trim() || !editingNotice.content.trim()) {
      alert('제목과 내용을 모두 입력해주세요.');
      return;
    }

    try {
      await placeAPI.updatePlaceAnnouncement(editingNotice.id, {
        title: editingNotice.title.trim(),
        content: editingNotice.content.trim()
      });

      const updatedNotices = notices.map(notice => 
        notice.id === editingNotice.id ? editingNotice : notice
      );
      
      setNotices(updatedNotices);
      setEditingNotice(null);
      setIsEditMode(false);
      
      // 부모 컴포넌트에 업데이트된 공지 목록 전달
      onSave({ placeId: place.placeId, placeAnnouncements: updatedNotices });
    } catch (error) {
      alert('공지사항 수정에 실패했습니다: ' + error.message);
    }
  };

  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [noticeToDelete, setNoticeToDelete] = useState(null);

  const handleDeleteNotice = (noticeId) => {
    const notice = notices.find(n => n.id === noticeId);
    setNoticeToDelete(notice);
    setShowDeleteModal(true);
  };

  const confirmDeleteNotice = async () => {
    if (noticeToDelete) {
      try {
        await placeAPI.deletePlaceAnnouncement(noticeToDelete.id);
        
        const updatedNotices = notices.filter(notice => notice.id !== noticeToDelete.id);
        setNotices(updatedNotices);

        // 부모 컴포넌트에 업데이트된 공지 목록 전달
        onSave({ placeId: place.placeId, placeAnnouncements: updatedNotices });

        setShowDeleteModal(false);
        setNoticeToDelete(null);
      } catch (error) {
        alert('공지사항 삭제에 실패했습니다: ' + error.message);
      }
    }
  };

  const handleCancelEdit = () => {
    setEditingNotice(null);
    setIsEditMode(false);
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleString('ko-KR', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    }).replace(/\./g, '-').replace(/\s/g, ' ');
  };

  return (
    <Modal isOpen={true} onClose={onClose} maxWidth="max-w-4xl">
      <h3 className="text-xl font-bold mb-6">플레이스 관리</h3>
      
      {isLoading ? (
        <div className="space-y-6">
          {/* 새 공지 생성 섹션 스켈레톤 */}
          <div className="bg-gray-50 p-4 rounded-lg">
            <div className="h-6 bg-gray-200 rounded w-32 mb-3 animate-pulse"></div>
            <div className="space-y-3">
              <div>
                <div className="h-4 bg-gray-200 rounded w-16 mb-1 animate-pulse"></div>
                <div className="h-10 bg-gray-200 rounded animate-pulse"></div>
              </div>
              <div>
                <div className="h-4 bg-gray-200 rounded w-16 mb-1 animate-pulse"></div>
                <div className="h-24 bg-gray-200 rounded animate-pulse"></div>
              </div>
              <div className="flex justify-between items-center">
                <div className="h-4 bg-gray-200 rounded w-24 animate-pulse"></div>
                <div className="h-10 bg-gray-200 rounded w-24 animate-pulse"></div>
              </div>
            </div>
          </div>

          {/* 기존 공지 목록 스켈레톤 */}
          <div>
            <div className="h-6 bg-gray-200 rounded w-32 mb-3 animate-pulse"></div>
            <div className="space-y-3">
              {[1, 2, 3].map((i) => (
                <div key={i} className="border border-gray-200 rounded-lg p-4">
                  <div className="space-y-2">
                    <div className="flex justify-between items-start">
                      <div className="h-5 bg-gray-200 rounded w-24 animate-pulse"></div>
                      <div className="flex space-x-2">
                        <div className="h-6 bg-gray-200 rounded w-12 animate-pulse"></div>
                        <div className="h-6 bg-gray-200 rounded w-12 animate-pulse"></div>
                      </div>
                    </div>
                    <div className="h-4 bg-gray-200 rounded w-full animate-pulse"></div>
                    <div className="h-3 bg-gray-200 rounded w-32 animate-pulse"></div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      ) : (
        <div className="space-y-6">
          {/* 새 공지 생성 섹션 */}
          <div className="bg-gray-50 p-4 rounded-lg">
          <h4 className="font-semibold text-lg mb-3">새 공지 생성</h4>
          <div className="space-y-3">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                제목
              </label>
              <input
                type="text"
                value={newNotice.title}
                onChange={(e) => setNewNotice(prev => ({ ...prev, title: e.target.value }))}
                placeholder="공지 제목을 입력해주세요 (20자 이내)"
                maxLength={20}
                className="w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500"
              />
            </div>
            
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                내용
              </label>
              <textarea
                value={newNotice.content}
                onChange={(e) => setNewNotice(prev => ({ ...prev, content: e.target.value }))}
                rows="3"
                placeholder="공지 내용을 입력해주세요."
                className="w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500"
              />
            </div>

            <div className="flex justify-between items-center">
              <div className="text-sm text-gray-600">
                현재 공지: {notices.length}/3개
              </div>
              <button 
                onClick={handleAddNotice}
                disabled={notices.length >= 3 || !newNotice.title.trim() || !newNotice.content.trim()}
                className={`px-4 py-2 rounded-lg font-medium ${
                  notices.length >= 3 || !newNotice.title.trim() || !newNotice.content.trim()
                    ? 'bg-gray-300 text-gray-500 cursor-not-allowed'
                    : 'bg-blue-600 text-white hover:bg-blue-700'
                }`}
              >
                공지 생성
              </button>
            </div>

            {notices.length >= 3 && (
              <div className="p-2 bg-red-50 border border-red-200 rounded">
                <p className="text-red-600 text-xs font-medium">
                  ⚠️ 생성이 불가능합니다. 기존의 공지를 하나 삭제하여주십시오!
                </p>
              </div>
            )}
          </div>
        </div>

        {/* 기존 공지 목록 섹션 */}
        <div>
          <h4 className="font-semibold text-lg mb-3">기존 공지 관리</h4>
          {notices.length > 0 ? (
            <div className="space-y-3">
              {notices.map((notice, index) => (
                <div key={notice.id} className="border border-gray-200 rounded-lg p-4">
                  {editingNotice?.id === notice.id ? (
                    // 수정 모드
                    <div className="space-y-3">
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">제목</label>
                        <input
                          type="text"
                          value={editingNotice.title}
                          onChange={(e) => setEditingNotice(prev => ({ ...prev, title: e.target.value }))}
                          maxLength={20}
                          className="w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500"
                        />
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">내용</label>
                        <textarea
                          value={editingNotice.content}
                          onChange={(e) => setEditingNotice(prev => ({ ...prev, content: e.target.value }))}
                          rows="3"
                          className="w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500"
                        />
                      </div>
                      <div className="flex justify-end space-x-2">
                        <button 
                          onClick={handleCancelEdit}
                          className="px-3 py-1 bg-gray-200 text-gray-800 rounded hover:bg-gray-300"
                        >
                          취소
                        </button>
                        <button 
                          onClick={handleSaveEdit}
                          className="px-3 py-1 bg-green-600 text-white rounded hover:bg-green-700"
                        >
                          저장
                        </button>
                      </div>
                    </div>
                  ) : (
                    // 보기 모드
                    <div>
                      <div className="flex justify-between items-start mb-2">
                        <h5 className="font-medium text-gray-900">{notice.title}</h5>
                        <div className="flex space-x-2">
                          <button 
                            onClick={() => handleEditNotice(notice)}
                            className="text-blue-600 hover:text-blue-800 text-sm"
                          >
                            수정
                          </button>
                          <button 
                            onClick={() => handleDeleteNotice(notice.id)}
                            className="text-red-600 hover:text-red-800 text-sm"
                          >
                            삭제
                          </button>
                        </div>
                      </div>
                      <p className="text-gray-700 mb-2">{notice.content}</p>
                      <p className="text-xs text-gray-400">{formatDate(notice.createdAt)}</p>
                    </div>
                  )}
                </div>
              ))}
            </div>
          ) : (
            <div className="text-center py-8 text-gray-500">
              <p>등록된 공지가 없습니다.</p>
              <p className="text-sm mt-1">새로운 공지를 생성해보세요.</p>
            </div>
          )}
        </div>
        </div>
      )}

      <div className="mt-6 flex justify-end">
        <button 
          onClick={onClose} 
          className="bg-gray-200 hover:bg-gray-300 text-gray-800 font-bold py-2 px-4 rounded-lg"
        >
          닫기
        </button>
      </div>

      {/* 삭제 확인 모달 */}
      {showDeleteModal && (
        <div className="absolute inset-0 bg-black bg-opacity-50 flex items-center justify-center z-10">
          <div className="bg-white rounded-lg p-6 max-w-sm w-full mx-4">
            <h3 className="text-lg font-semibold mb-4">공지 삭제 확인</h3>
            <p className="text-gray-700 mb-6">
              정말로 "{noticeToDelete?.title}" 공지를 삭제하시겠습니까?
            </p>
            <div className="flex justify-end space-x-3">
              <button 
                onClick={() => setShowDeleteModal(false)}
                className="px-4 py-2 bg-gray-200 text-gray-800 rounded-lg hover:bg-gray-300"
              >
                취소
              </button>
              <button 
                onClick={confirmDeleteNotice}
                className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700"
              >
                삭제
              </button>
            </div>
          </div>
        </div>
      )}
    </Modal>
  );
};

export default PlaceManageModal;

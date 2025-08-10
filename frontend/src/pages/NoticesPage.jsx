import React, { useEffect, useState } from 'react';
import { useModal } from '../hooks/useModal';
import { announcementAPI } from '../utils/api';

function formatDate(dateString) {
    if (!dateString) return '';
    const d = new Date(dateString);
    const yyyy = d.getFullYear();
    const mm = String(d.getMonth() + 1).padStart(2, '0');
    const dd = String(d.getDate()).padStart(2, '0');
    const hh = String(d.getHours()).padStart(2, '0');
    const min = String(d.getMinutes()).padStart(2, '0');
    return `${yyyy}-${mm}-${dd} ${hh}:${min}`;
}

const NoticesPage = () => {
    const { openModal, showToast } = useModal();
    const [pinned, setPinned] = useState([]);
    const [unpinned, setUnpinned] = useState([]);

    useEffect(() => {
        announcementAPI.getAnnouncements().then(res => {
            // 고정된 공지사항과 일반 공지사항을 모두 작성 시간 순서대로 정렬 (최신순)
            const pinnedNotices = (res.pinned || []).sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
            const unpinnedNotices = (res.unpinned || []).sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
            
            setPinned(pinnedNotices);
            setUnpinned(unpinnedNotices);
        });
    }, []);

    const handleSave = async (id, data) => {
        if (!data.title || !data.content) { showToast('제목과 내용을 모두 입력해주세요.'); return; }
        if (id) {
            try {
                await announcementAPI.updateAnnouncement(id, data);
                // 공지 수정 후 목록 재로딩
                const res = await announcementAPI.getAnnouncements();
                const pinnedNotices = (res.pinned || []).sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
                const unpinnedNotices = (res.unpinned || []).sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
                
                setPinned(pinnedNotices);
                setUnpinned(unpinnedNotices);
                showToast('공지사항이 수정되었습니다.');
            } catch {
                showToast('공지사항 수정에 실패했습니다.');
            }
        } else {
            try {
                await announcementAPI.createAnnouncement(data);
                
                // 공지 추가 후 목록 재로딩
                const res = await announcementAPI.getAnnouncements();
                const pinnedNotices = (res.pinned || []).sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
                const unpinnedNotices = (res.unpinned || []).sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
                
                setPinned(pinnedNotices);
                setUnpinned(unpinnedNotices);
                showToast('새 공지사항이 등록되었습니다.');
            } catch {
                showToast('공지사항 등록에 실패했습니다.');
            }
        }
    };

    const handleDelete = async (id) => {
        try {
            await announcementAPI.deleteAnnouncement(id);
            
            // 공지 삭제 후 목록 재로딩
            const res = await announcementAPI.getAnnouncements();
            const pinnedNotices = (res.pinned || []).sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
            const unpinnedNotices = (res.unpinned || []).sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
            
            setPinned(pinnedNotices);
            setUnpinned(unpinnedNotices);
            showToast('공지사항이 삭제되었습니다.');
        } catch {
            showToast('공지사항 삭제에 실패했습니다.');
        }
    };

    const handleTogglePin = async (noticeId, currentIsPinned) => {
        try {
            const pinnedCount = pinned.length;
            if (!currentIsPinned && pinnedCount >= 3) {
                showToast('고정은 최대 3개까지만 가능합니다.');
                return;
            }
            
            // API 호출 - 변경하려는 값으로 전달
            const response = await announcementAPI.toggleAnnouncementPin(noticeId, !currentIsPinned);
            
            if (response.status === 204) {
                // 클라이언트 상태 업데이트
                if (currentIsPinned) {
                    // 고정 해제: pinned -> unpinned (작성 순서대로 정렬)
                    const notice = pinned.find(n => n.id === noticeId);
                    setPinned(prev => prev.filter(n => n.id !== noticeId));
                    
                    // 기존 unpinned에 추가하고 createdAt 기준으로 정렬 (최신순)
                    setUnpinned(prev => {
                        const newUnpinned = [...prev, { ...notice, isPinned: false }];
                        return newUnpinned.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
                    });
                } else {
                    // 고정 등록: unpinned -> pinned (작성 순서대로 정렬)
                    const notice = unpinned.find(n => n.id === noticeId);
                    setUnpinned(prev => prev.filter(n => n.id !== noticeId));
                    
                    // 기존 pinned에 추가하고 createdAt 기준으로 정렬 (최신순)
                    setPinned(prev => {
                        const newPinned = [...prev, { ...notice, isPinned: true }];
                        return newPinned.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
                    });
                }
                
                showToast(currentIsPinned ? '공지사항 고정이 해제되었습니다.' : '공지사항이 고정되었습니다.');
            }
        } catch (error) {
            showToast('고정 상태 변경에 실패했습니다.');
            console.error('Pin toggle error:', error);
        }
    };
    
    return (
        <div>
            <div className="flex justify-between items-center mb-6">
                <h2 className="text-3xl font-bold">공지사항 관리</h2>
                <button 
                    onClick={() => openModal('notice', { onSave: (data) => handleSave(null, data) })} 
                    className="bg-gray-800 hover:bg-gray-900 text-white font-bold py-2 px-4 rounded-lg flex items-center"
                >
                    <i className="fas fa-plus mr-2"></i> 공지사항 등록
                </button>
            </div>
            
            <p className="text-sm text-gray-500 mb-4">※ 고정은 최대 3개만 가능합니다.</p>
            
            {/* 고정된 공지사항 */}
            {pinned.length > 0 && (
                <div className="mb-6">
                    <h3 className="text-lg font-semibold text-gray-700 mb-3 flex items-center">
                        <i className="fas fa-thumbtack text-yellow-500 mr-2"></i>
                        고정된 공지사항
                    </h3>
                    <div className="space-y-4">
                        {pinned.map((notice) => (
                            <div 
                                key={notice.id} 
                                data-id={notice.id} 
                                className="bg-white rounded-lg shadow-sm border border-gray-200 p-5"
                            >
                                <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-2 mb-3">
                                    <div className="flex-1 flex items-start gap-3">
                                        <button 
                                            onClick={() => handleTogglePin(notice.id, notice.isPinned)} 
                                            title="고정 해제"
                                            className="text-yellow-500 hover:text-yellow-600 transition-colors mt-1 flex-shrink-0"
                                        >
                                            <i className="fas fa-thumbtack text-lg"></i>
                                        </button>
                                        <div className="flex-1 min-w-0">
                                            <p className="font-semibold text-lg truncate" title={notice.title}>
                                                {notice.title}
                                            </p>
                                            <p className="text-sm text-gray-500 mt-1">
                                                {formatDate(notice.createdAt)}
                                            </p>
                                        </div>
                                    </div>
                                    <div className="flex items-center space-x-3 ml-0 sm:ml-4 flex-wrap">
                                        <button 
                                            onClick={() => openModal('notice', { notice, onSave: (data) => handleSave(notice.id, data) })} 
                                            className="text-blue-600 hover:text-blue-800 font-bold"
                                        >
                                            수정
                                        </button>
                                        <button 
                                            onClick={() => {
                                                openModal('confirm', {
                                                    title: '공지사항 삭제 확인',
                                                    message: `'${notice.title}' 공지사항을 정말 삭제하시겠습니까?`,
                                                    onConfirm: () => {
                                                        handleDelete(notice.id);
                                                    }
                                                });
                                            }} 
                                            className="text-red-600 hover:text-red-800 font-bold"
                                        >
                                            삭제
                                        </button>
                                    </div>
                                </div>
                                
                                <div className="mt-4 pl-4 border-l-4 border-yellow-400 bg-yellow-50 p-3 rounded-r-lg">
                                    <p className="text-gray-700 whitespace-pre-wrap">{notice.content}</p>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            )}
            
            {/* 일반 공지사항 */}
            <div>
                <h3 className="text-lg font-semibold text-gray-700 mb-3">일반 공지사항</h3>
                <div className="space-y-4">
                    {unpinned.map((notice) => (
                        <div 
                            key={notice.id} 
                            data-id={notice.id} 
                            className="bg-white rounded-lg shadow-sm border border-gray-200 p-5"
                        >
                            <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-2 mb-3">
                                <div className="flex-1 flex items-start gap-3">
                                    <button 
                                        onClick={() => handleTogglePin(notice.id, notice.isPinned)} 
                                        title="고정하기"
                                        className="text-gray-400 hover:text-gray-600 transition-colors mt-1 flex-shrink-0"
                                    >
                                        <i className="fas fa-thumbtack text-lg"></i>
                                    </button>
                                    <div className="flex-1 min-w-0">
                                        <p className="font-semibold text-lg truncate" title={notice.title}>
                                            {notice.title}
                                        </p>
                                        <p className="text-sm text-gray-500 mt-1">
                                            {formatDate(notice.createdAt)}
                                        </p>
                                    </div>
                                </div>
                                <div className="flex items-center space-x-3 ml-0 sm:ml-4 flex-wrap">
                                    <button 
                                        onClick={() => openModal('notice', { notice, onSave: (data) => handleSave(notice.id, data) })} 
                                        className="text-blue-600 hover:text-blue-800 font-bold"
                                    >
                                        수정
                                    </button>
                                    <button 
                                        onClick={() => {
                                            openModal('confirm', {
                                                title: '공지사항 삭제 확인',
                                                message: `'${notice.title}' 공지사항을 정말 삭제하시겠습니까?`,
                                                onConfirm: () => {
                                                    handleDelete(notice.id);
                                                }
                                            });
                                        }} 
                                        className="text-red-600 hover:text-red-800 font-bold"
                                    >
                                        삭제
                                    </button>
                                </div>
                            </div>
                            
                            <div className="mt-4 pl-4 border-l-4 border-gray-200 bg-gray-50 p-3 rounded-r-lg">
                                <p className="text-gray-700 whitespace-pre-wrap">{notice.content}</p>
                            </div>
                        </div>
                    ))}
                </div>
                
                {/* 공지사항이 없을 때 */}
                {pinned.length === 0 && unpinned.length === 0 && (
                    <div className="text-center py-12">
                        <i className="fas fa-bullhorn text-4xl text-gray-400 mb-4"></i>
                        <p className="text-gray-500 mb-4">등록된 공지사항이 없습니다</p>
                        <button
                            onClick={() => openModal('notice', { onSave: (data) => handleSave(null, data) })}
                            className="bg-gray-800 hover:bg-gray-900 text-white px-4 py-2 rounded-lg transition-colors"
                        >
                            첫 번째 공지사항 등록
                        </button>
                    </div>
                )}
            </div>
        </div>
    );
};

export default NoticesPage;

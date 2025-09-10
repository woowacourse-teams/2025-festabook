import React, { useEffect, useState, useRef } from 'react';
import FlipMove from 'react-flip-move';
import { useModal } from '../hooks/useModal';
import { announcementAPI } from '../utils/api';

function formatDate(dateString) {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}.${month}.${day}`;
}

const NoticesPage = () => {
    const { openModal, showToast } = useModal();
    const [pinned, setPinned] = useState([]);
    const [unpinned, setUnpinned] = useState([]);
    const [notificationCooldowns, setNotificationCooldowns] = useState(() => {
        // localStorage에서 쿨다운 정보를 불러오기
        try {
            const saved = localStorage.getItem('notificationCooldowns');
            if (saved) {
                const parsed = JSON.parse(saved);
                // 만료된 쿨다운들 제거
                const now = Date.now();
                const filtered = {};
                Object.keys(parsed).forEach(key => {
                    if (parsed[key] > now) {
                        filtered[key] = parsed[key];
                    }
                });
                return filtered;
            }
        } catch (error) {
            console.error('쿨다운 정보 불러오기 실패:', error);
        }
        return {};
    }); // 알림 쿨다운 상태
    const [currentTime, setCurrentTime] = useState(Date.now()); // 실시간 업데이트를 위한 현재 시간
    const hasLoadedRef = useRef(false);

    useEffect(() => {
        if (hasLoadedRef.current) return;
        
        announcementAPI.getAnnouncements().then(res => {
            // 고정된 공지사항과 일반 공지사항을 모두 작성 시간 순서대로 정렬 (최신순)
            const pinnedNotices = (res.pinned || []).sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
            const unpinnedNotices = (res.unpinned || []).sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
            
            setPinned(pinnedNotices);
            setUnpinned(unpinnedNotices);
            hasLoadedRef.current = true;
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
            if (!noticeId) {
                showToast('공지사항 ID를 찾을 수 없습니다.');
                return;
            }
            
            const pinnedCount = pinned.length;
            if (!currentIsPinned && pinnedCount >= 3) {
                showToast('고정은 최대 3개까지만 가능합니다.');
                return;
            }
            
            // API 호출 - 변경하려는 값으로 전달
            const response = await announcementAPI.toggleAnnouncementPin(noticeId, !currentIsPinned);
            
            // 204 No Content 또는 200 OK 등 성공적인 응답 처리
            if (response.status >= 200 && response.status < 300) {
                // 클라이언트 상태 업데이트
                if (currentIsPinned) {
                    // 고정 해제: pinned -> unpinned (작성 순서대로 정렬)
                    const notice = pinned.find(n => n.announcementId === noticeId);
                    
                    setPinned(prev => prev.filter(n => n.announcementId !== noticeId));
                    
                    setUnpinned(prev => {
                        const newUnpinned = [...prev, { ...notice, isPinned: false }];
                        return newUnpinned.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
                    });
                } else {
                    // 고정: unpinned -> pinned (작성 순서대로 정렬)
                    const notice = unpinned.find(n => n.announcementId === noticeId);
                    
                    setUnpinned(prev => prev.filter(n => n.announcementId !== noticeId));
                    
                    setPinned(prev => {
                        const newPinned = [...prev, { ...notice, isPinned: true }];
                        return newPinned.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
                    });
                }
                showToast(currentIsPinned ? '고정이 해제되었습니다.' : '공지사항이 고정되었습니다.');
            }
        } catch (error) {
            showToast('고정 상태 변경에 실패했습니다.');
        }
    };

    // 알림 전송 함수
    const handleSendNotification = async (noticeId, noticeTitle) => {
        try {
            // API 호출 (실제 알림 전송 API 엔드포인트 사용)
            // await announcementAPI.sendNotification(noticeId);
            
            // 쿨다운 시작 (3분 = 180초)
            const cooldownTime = 3 * 60 * 1000; // 3분을 밀리초로 변환
            const endTime = Date.now() + cooldownTime;
            
            setNotificationCooldowns(prev => {
                const updated = {
                    ...prev,
                    [noticeId]: endTime
                };
                // localStorage에 저장
                try {
                    localStorage.setItem('notificationCooldowns', JSON.stringify(updated));
                } catch (error) {
                    console.error('쿨다운 정보 저장 실패:', error);
                }
                return updated;
            });
            
            showToast(`'${noticeTitle}' 공지사항 알림이 전송되었습니다.`);
        } catch (error) {
            showToast('알림 전송에 실패했습니다.');
        }
    };

    // 쿨다운 남은 시간 계산 (currentTime을 사용해서 리렌더링 보장)
    const getCooldownTimeLeft = (noticeId) => {
        const endTime = notificationCooldowns[noticeId];
        if (!endTime) return 0;
        
        const timeLeft = Math.max(0, endTime - currentTime);
        return Math.ceil(timeLeft / 1000); // 초 단위로 반환
    };

    // 쿨다운 텍스트 포맷팅
    const formatCooldownTime = (seconds) => {
        if (seconds <= 0) return '';
        const minutes = Math.floor(seconds / 60);
        const remainingSeconds = seconds % 60;
        return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`;
    };

    // 쿨다운 카운트다운을 위한 useEffect
    useEffect(() => {
        const interval = setInterval(() => {
            const now = Date.now();
            
            setNotificationCooldowns(prev => {
                const updated = { ...prev };
                let hasChanges = false;
                let hasActiveCooldowns = false;
                
                // 만료된 쿨다운들 정리 및 활성화된 쿨다운 확인
                Object.keys(updated).forEach(noticeId => {
                    if (updated[noticeId] <= now) {
                        delete updated[noticeId];
                        hasChanges = true;
                    } else {
                        hasActiveCooldowns = true;
                    }
                });
                
                // 활성화된 쿨다운이 있으면 현재 시간 업데이트하여 리렌더링 트리거
                if (hasActiveCooldowns) {
                    setCurrentTime(now);
                }
                
                if (hasChanges) {
                    // localStorage 업데이트
                    try {
                        if (Object.keys(updated).length === 0) {
                            localStorage.removeItem('notificationCooldowns');
                        } else {
                            localStorage.setItem('notificationCooldowns', JSON.stringify(updated));
                        }
                    } catch (error) {
                        console.error('쿨다운 정보 업데이트 실패:', error);
                    }
                    return updated;
                }
                
                return prev;
            });
        }, 1000);
        
        return () => clearInterval(interval);
    }, []); // 의존성 배열을 빈 배열로 유지
    
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
                    <FlipMove className="space-y-4" duration={300} easing="cubic-bezier(0.4, 0.0, 0.2, 1)" appearAnimation="none">
                        {pinned.map((notice) => {
                            return (
                            <div 
                                key={notice.announcementId} 
                                data-id={notice.announcementId} 
                                className="bg-white rounded-lg shadow-sm border border-gray-200 p-5"
                            >
                                <div className="flex flex-col sm:flex-row justify-between items-start sm:items-start gap-2 mb-3">
                                    <div className="flex-1 flex items-start gap-3">
                                        <button 
                                            onClick={() => handleTogglePin(notice.announcementId, notice.isPinned)} 
                                            title="고정 해제"
                                            className="text-yellow-500 hover:text-yellow-600 transition-colors mt-1 flex-shrink-0"
                                        >
                                            <i className="fas fa-thumbtack text-lg"></i>
                                        </button>
                                        <div className="flex-1 min-w-0">
                                            <p className="font-semibold text-lg break-words leading-relaxed" title={notice.title}>
                                                {notice.title}
                                            </p>
                                            <p className="text-sm text-gray-500 mt-1">
                                                {formatDate(notice.createdAt)}
                                            </p>
                                        </div>
                                    </div>
                                    <div className="flex items-center space-x-3 ml-0 sm:ml-4 flex-shrink-0">
                                        <button 
                                            onClick={() => {
                                                const cooldownLeft = getCooldownTimeLeft(notice.announcementId);
                                                if (cooldownLeft > 0) return;
                                                
                                                openModal('pushNotificationConfirm', {
                                                    notice: notice,
                                                    onConfirm: () => handleSendNotification(notice.announcementId, notice.title)
                                                });
                                            }}
                                            disabled={getCooldownTimeLeft(notice.announcementId) > 0}
                                            className={`font-bold py-1 px-3 rounded text-sm transition-colors duration-200 ${
                                                getCooldownTimeLeft(notice.announcementId) > 0
                                                    ? 'bg-gray-300 text-gray-500 cursor-not-allowed'
                                                    : 'bg-emerald-600 hover:bg-emerald-700 text-white'
                                            }`}
                                            title={getCooldownTimeLeft(notice.announcementId) > 0 
                                                ? `${formatCooldownTime(getCooldownTimeLeft(notice.announcementId))} 후 재전송 가능` 
                                                : '알림 전송'
                                            }
                                        >
                                            {getCooldownTimeLeft(notice.announcementId) > 0 
                                                ? `알림 (${formatCooldownTime(getCooldownTimeLeft(notice.announcementId))})` 
                                                : '알림 전송'
                                            }
                                        </button>
                                        <button 
                                            onClick={() => openModal('notice', { notice, onSave: (data) => handleSave(notice.announcementId, data) })} 
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
                                                        handleDelete(notice.announcementId);
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
                        );
                        })}
                    </FlipMove>
                </div>
            )}
            
            {/* 일반 공지사항 */}
            <div>
                <h3 className="text-lg font-semibold text-gray-700 mb-3">일반 공지사항</h3>
                <FlipMove className="space-y-4" duration={300} easing="cubic-bezier(0.4, 0.0, 0.2, 1)" appearAnimation="none">
                    {unpinned.map((notice) => {
                        return (
                        <div 
                            key={notice.announcementId} 
                            data-id={notice.announcementId} 
                            className="bg-white rounded-lg shadow-sm border border-gray-200 p-5"
                        >
                            <div className="flex flex-col sm:flex-row justify-between items-start sm:items-start gap-2 mb-3">
                                <div className="flex-1 flex items-start gap-3 min-w-0">
                                    <button 
                                        onClick={() => handleTogglePin(notice.announcementId, notice.isPinned)} 
                                        title="고정하기"
                                        className="text-gray-400 hover:text-gray-600 transition-colors mt-1 flex-shrink-0"
                                    >
                                        <i className="fas fa-thumbtack text-lg"></i>
                                    </button>
                                    <div className="flex-1 min-w-0">
                                        <p className="font-semibold text-lg break-words leading-relaxed" title={notice.title}>
                                            {notice.title}
                                        </p>
                                        <p className="text-sm text-gray-500 mt-1">
                                            {formatDate(notice.createdAt)}
                                        </p>
                                    </div>
                                </div>
                                <div className="flex items-center space-x-3 ml-0 sm:ml-4 flex-shrink-0">
                                    <button 
                                        onClick={() => {
                                            const cooldownLeft = getCooldownTimeLeft(notice.announcementId);
                                            if (cooldownLeft > 0) return;
                                            
                                            openModal('pushNotificationConfirm', {
                                                notice: notice,
                                                onConfirm: () => handleSendNotification(notice.announcementId, notice.title)
                                            });
                                        }}
                                        disabled={getCooldownTimeLeft(notice.announcementId) > 0}
                                        className={`font-bold py-1 px-3 rounded text-sm transition-colors duration-200 ${
                                            getCooldownTimeLeft(notice.announcementId) > 0
                                                ? 'bg-gray-300 text-gray-500 cursor-not-allowed'
                                                : 'bg-emerald-600 hover:bg-emerald-700 text-white'
                                        }`}
                                        title={getCooldownTimeLeft(notice.announcementId) > 0 
                                            ? `${formatCooldownTime(getCooldownTimeLeft(notice.announcementId))} 후 재전송 가능` 
                                            : '알림 전송'
                                        }
                                    >
                                        {getCooldownTimeLeft(notice.announcementId) > 0 
                                            ? `알림 (${formatCooldownTime(getCooldownTimeLeft(notice.announcementId))})` 
                                            : '알림 전송'
                                        }
                                    </button>
                                    <button 
                                        onClick={() => openModal('notice', { notice, onSave: (data) => handleSave(notice.announcementId, data) })} 
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
                                                    handleDelete(notice.announcementId);
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
                    );
                    })}
                </FlipMove>
                
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

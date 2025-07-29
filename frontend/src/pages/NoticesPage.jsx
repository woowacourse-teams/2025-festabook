import React, { useEffect, useState } from 'react';
import { useModal } from '../hooks/useModal';
import api from '../utils/api';

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
        api.get('/announcements').then(res => {
            setPinned(res.data.pinned || []);
            setUnpinned(res.data.unpinned || []);
        });
    }, []);

    const handleSave = async (id, data) => {
        if (!data.title || !data.content) { showToast('제목과 내용을 모두 입력해주세요.'); return; }
        if (id) {
            try {
                await api.patch(`/announcements/${id}`, data);
                // 공지 수정 후 목록 재로딩
                const res = await api.get('/announcements');
                setPinned(res.data.pinned || []);
                setUnpinned(res.data.unpinned || []);
                showToast('공지사항이 수정되었습니다.');
            } catch {
                showToast('공지사항 수정에 실패했습니다.');
            }
        } else {
            try {
                await api.post('/announcements', data);
                // 공지 추가 후 목록 재로딩
                const res = await api.get('/announcements');
                setPinned(res.data.pinned || []);
                setUnpinned(res.data.unpinned || []);
                showToast('새 공지사항이 등록되었습니다.');
            } catch {
                showToast('공지사항 등록에 실패했습니다.');
            }
        }
    };

    const handleDelete = async (id) => {
        try {
            await api.delete(`/announcements/${id}`);
            // 공지 삭제 후 목록 재로딩
            const res = await api.get('/announcements');
            setPinned(res.data.pinned || []);
            setUnpinned(res.data.unpinned || []);
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
            const response = await api.patch(`/announcements/${noticeId}/pin`, { 
                pinned: !currentIsPinned
            });
            
            if (response.status === 204) {
                // 클라이언트 상태 업데이트
                if (currentIsPinned) {
                    // 고정 해제: pinned -> unpinned
                    const notice = pinned.find(n => n.id === noticeId);
                    setPinned(prev => prev.filter(n => n.id !== noticeId));
                    setUnpinned(prev => [{ ...notice, isPinned: false }, ...prev]);
                } else {
                    // 고정 등록: unpinned -> pinned  
                    const notice = unpinned.find(n => n.id === noticeId);
                    setUnpinned(prev => prev.filter(n => n.id !== noticeId));
                    setPinned(prev => [...prev, { ...notice, isPinned: true }]);
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
            <style>{`
                /* 고정핀 버튼 스타일 */
                .pin-button {
                    cursor: pointer;
                    border: none;
                    background: none;
                    padding: 4px;
                    border-radius: 4px;
                }
                
                .pin-button:hover {
                    background-color: #f3f4f6;
                }
                
                .pin-icon { 
                    color: #d1d5db; 
                    transition: all 0.2s ease-in-out;
                    font-size: 16px;
                }
                
                .pin-icon.pinned { 
                    color: #f59e0b; 
                    transform: rotate(45deg);
                }
                
                .pin-icon:hover {
                    color: #9ca3af;
                }
                
                .pin-icon.pinned:hover {
                    color: #d97706;
                }
            `}</style>
            <div className="flex justify-between items-center mb-6"><h2 className="text-3xl font-bold">공지사항</h2><button onClick={() => openModal('notice', { onSave: (data) => handleSave(null, data) })} className="bg-gray-800 hover:bg-gray-900 text-white font-bold py-2 px-4 rounded-lg flex items-center"><i className="fas fa-plus mr-2"></i> 새 공지사항</button></div>
            <p className="text-sm text-gray-500 mb-2">※ 고정은 최대 3개만 가능합니다.</p>
            <div className="bg-white rounded-lg shadow-sm border border-gray-200 overflow-x-auto">
                <table className="min-w-full w-full">
                    <thead className="table-header">
                        <tr>
                            <th className="p-4 text-center font-semibold min-w-[60px] w-20">고정</th>
                            <th className="p-4 text-left font-semibold min-w-[120px] w-1/4">제목</th>
                            <th className="p-4 text-left font-semibold min-w-[180px] w-1/3">내용</th>
                            <th className="p-4 text-left font-semibold min-w-[100px] w-1/6">작성일</th>
                            <th className="p-4 text-left font-semibold min-w-[120px] w-1/6">관리</th>
                        </tr>
                    </thead>
                    <tbody>
                        {/* 고정 공지 */}
                        {pinned.map(notice => (
                            <React.Fragment key={notice.id}>
                                <tr className="border-b border-gray-200 last:border-b-0 hover:bg-gray-50">
                                    <td className="p-4 text-center align-top">
                                        <button 
                                            onClick={() => handleTogglePin(notice.id, notice.isPinned)} 
                                            title="고정 토글"
                                            className="pin-button hover:scale-110 transition-transform duration-200"
                                        >
                                            <i className={`fas fa-thumbtack pin-icon ${notice.isPinned ? 'pinned' : ''}`}></i>
                                        </button>
                                    </td>
                                    <td className="p-4 align-top max-w-xs truncate" title={notice.title}>{notice.title}</td>
                                    <td className="p-4 align-top text-gray-800 max-w-xs truncate" style={{maxWidth: '320px'}} title={notice.content}>{notice.content}</td>
                                    <td className="p-4 text-gray-600 align-top">{formatDate(notice.createdAt)}</td>
                                    <td className="p-4 align-top min-w-[120px]">
                                        <div className="flex flex-row items-center space-x-6 flex-wrap">
                                            <button onClick={() => openModal('notice', { notice, onSave: (data) => handleSave(notice.id, data) })} className="text-blue-600 hover:text-blue-800 font-bold">수정</button>
                                            <button onClick={() => {
                                                openModal('confirm', {
                                                    title: '공지사항 삭제 확인',
                                                    message: `'${notice.title}' 공지사항을 정말 삭제하시겠습니까?`,
                                                    onConfirm: () => {
                                                        handleDelete(notice.id);
                                                    }
                                                });
                                            }} className="text-red-600 hover:text-red-800 font-bold">삭제</button>
                                        </div>
                                    </td>
                                </tr>
                            </React.Fragment>
                        ))}
                        {/* 일반 공지 */}
                        {unpinned.map(notice => (
                            <React.Fragment key={notice.id}>
                                <tr className="border-b border-gray-200 last:border-b-0 hover:bg-gray-50">
                                    <td className="p-4 text-center align-top">
                                        <button 
                                            onClick={() => handleTogglePin(notice.id, notice.isPinned)} 
                                            title="고정 토글"
                                            className="pin-button hover:scale-110 transition-transform duration-200"
                                        >
                                            <i className={`fas fa-thumbtack pin-icon ${notice.isPinned ? 'pinned' : ''}`}></i>
                                        </button>
                                    </td>
                                    <td className="p-4 align-top max-w-xs truncate" title={notice.title}>{notice.title}</td>
                                    <td className="p-4 align-top text-gray-800 max-w-xs truncate" style={{maxWidth: '320px'}} title={notice.content}>{notice.content}</td>
                                    <td className="p-4 text-gray-600 align-top">{formatDate(notice.createdAt)}</td>
                                    <td className="p-4 align-top min-w-[120px]">
                                        <div className="flex flex-row items-center space-x-6 flex-wrap">
                                            <button onClick={() => openModal('notice', { notice, onSave: (data) => handleSave(notice.id, data) })} className="text-blue-600 hover:text-blue-800 font-bold">수정</button>
                                            <button onClick={() => {
                                                openModal('confirm', {
                                                    title: '공지사항 삭제 확인',
                                                    message: `'${notice.title}' 공지사항을 정말 삭제하시겠습니까?`,
                                                    onConfirm: () => {
                                                        handleDelete(notice.id);
                                                    }
                                                });
                                            }} className="text-red-600 hover:text-red-800 font-bold">삭제</button>
                                        </div>
                                    </td>
                                </tr>
                            </React.Fragment>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default NoticesPage;

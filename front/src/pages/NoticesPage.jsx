import React from 'react';
import { useData } from '../hooks/useData';
import { useModal } from '../hooks/useModal';

const NoticesPage = () => {
    const { notices, togglePinNotice, addNotice, updateNotice, deleteNotice } = useData();
    const { openModal, showToast } = useModal();
    const sortedNotices = [...notices].sort((a, b) => b.pinned - a.pinned || new Date(b.date) - new Date(a.date));

    const handleSave = (id, data) => {
        if (!data.title || !data.content) { showToast('제목과 내용을 모두 입력해주세요.'); return; }
        if (id) { updateNotice(id, data); showToast('공지사항이 수정되었습니다.'); } 
        else { addNotice(data); showToast('새 공지사항이 등록되었습니다.'); }
    };
    
    return (
        <div>
            <div className="flex justify-between items-center mb-6"><h2 className="text-3xl font-bold">공지사항 관리</h2><button onClick={() => openModal('notice', { onSave: (data) => handleSave(null, data) })} className="bg-gray-800 hover:bg-gray-900 text-white font-bold py-2 px-4 rounded-lg flex items-center"><i className="fas fa-plus mr-2"></i> 새 공지사항</button></div>
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
                        {sortedNotices.map(notice => (
                            <React.Fragment key={notice.id}>
                                <tr className="border-b border-gray-200 last:border-b-0 hover:bg-gray-50">
                                    <td className="p-4 text-center align-top">
                                        <button onClick={() => {
                                            const pinnedCount = sortedNotices.filter(n => n.pinned).length;
                                            if (!notice.pinned && pinnedCount >= 3) {
                                                showToast('고정은 최대 3개까지만 가능합니다.');
                                                return;
                                            }
                                            togglePinNotice(notice.id, showToast);
                                        }} title="고정 토글"><i className={`fas fa-thumbtack pin-icon ${notice.pinned ? 'pinned' : ''}`}></i></button>
                                    </td>
                                    <td className="p-4 align-top max-w-xs truncate" title={notice.title}>{notice.title}</td>
                                    <td className="p-4 align-top text-gray-800 max-w-xs truncate" style={{maxWidth: '320px'}} title={notice.content}>{notice.content}</td>
                                    <td className="p-4 text-gray-600 align-top">{notice.date}</td>
                                    <td className="p-4 align-top min-w-[120px]">
                                        <div className="flex flex-row items-center space-x-6 flex-wrap">
                                            <button onClick={() => openModal('notice', { notice, onSave: (data) => handleSave(notice.id, data) })} className="text-blue-600 hover:text-blue-800 font-bold">수정</button>
                                            <button onClick={() => {
                                                openModal('confirm', {
                                                    title: '공지사항 삭제 확인',
                                                    message: `'${notice.title}' 공지사항을 정말 삭제하시겠습니까?`,
                                                    onConfirm: () => {
                                                        deleteNotice(notice.id);
                                                        showToast('공지사항이 삭제되었습니다.');
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

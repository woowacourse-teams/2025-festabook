import React from 'react';
import { useData } from '../hooks/useData';
import { useModal } from '../hooks/useModal';

const LostFoundPage = () => {
    const { lostItems, toggleLostItemStatus, addLostItem, updateLostItem, deleteLostItem } = useData();
    const { openModal, showToast } = useModal();
    const sortedItems = [...lostItems].sort((a,b) => new Date(b.date) - new Date(a.date));

    const handleSave = (id, data) => {
        if (!data.name || !data.location) { showToast('물품명과 습득 장소를 입력해주세요.'); return; }
        if (id) { updateLostItem(id, data); showToast('분실물 정보가 수정되었습니다.'); }
        else { addLostItem(data); showToast('분실물이 등록되었습니다.'); }
    };

    return (
        <div>
             <div className="flex justify-between items-center mb-6"><h2 className="text-3xl font-bold">분실물 관리</h2><button onClick={() => openModal('lostItem', { onSave: (data) => handleSave(null, data) })} className="bg-gray-800 hover:bg-gray-900 text-white font-bold py-2 px-4 rounded-lg flex items-center"><i className="fas fa-plus mr-2"></i> 새 분실물 등록</button></div>
            <div className="bg-white rounded-lg shadow-sm border border-gray-200 overflow-x-auto">
                <table className="min-w-full w-full">
                    <thead className="table-header"><tr>
                        <th className="p-4 text-left font-semibold min-w-[80px]">상태</th>
                        <th className="p-4 text-left font-semibold min-w-[120px]">물품명</th>
                        <th className="p-4 text-left font-semibold min-w-[80px]">사진</th>
                        <th className="p-4 text-left font-semibold min-w-[120px]">습득 장소</th>
                        <th className="p-4 text-left font-semibold min-w-[100px]">습득일</th>
                        <th className="p-4 text-left font-semibold min-w-[120px]">관리</th>
                    </tr></thead>
                     <tbody>
                        {sortedItems.map(item => (
                            <tr key={item.id} className="border-b border-gray-200 last:border-b-0 hover:bg-gray-50">
                                <td className="p-4"><span className={`text-xs font-medium mr-2 px-2.5 py-0.5 rounded-full ${item.status === '보관중' ? 'bg-green-100 text-green-800' : 'bg-gray-200 text-gray-800'}`}>{item.status}</span></td>
                                <td className="p-4 max-w-xs truncate" title={item.name}>{item.name}</td>
                                <td className="p-4"><img src={item.photo} onError={(e) => { e.target.onerror = null; e.target.src='https://placehold.co/100x100/e0e0e0/757575?text=Error' }} className="w-12 h-12 object-cover rounded-md cursor-pointer" alt={item.name} onClick={() => openModal('image', { src: item.photo })}/></td>
                                <td className="p-4 text-gray-600">{item.location}</td><td className="p-4 text-gray-600">{item.date}</td>
                                <td className="p-4 min-w-[120px]">
                                    <div className="flex items-center justify-start space-x-4 flex-wrap">
                                        <button onClick={() => openModal('lostItem', { item, onSave: (data) => handleSave(item.id, data) })} className="text-blue-600 hover:text-blue-800 font-bold">수정</button>
                                        <button onClick={() => {
                                            openModal('confirm', {
                                                title: '분실물 삭제 확인',
                                                message: `'${item.name}' 분실물을 정말 삭제하시겠습니까?`,
                                                onConfirm: () => {
                                                    deleteLostItem(item.id);
                                                    showToast('분실물이 삭제되었습니다.');
                                                }
                                            });
                                        }} className="text-red-600 hover:text-red-800 font-bold">삭제</button>
                                        <button onClick={() => toggleLostItemStatus(item.id, showToast)} className={`text-white text-sm py-1 px-3 rounded-lg ${item.status === '보관중' ? 'bg-blue-500 hover:bg-blue-600' : 'bg-gray-400 hover:bg-gray-500'}`}>{item.status === '보관중' ? '완료 처리' : '보관 처리'}</button>
                                    </div>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default LostFoundPage;

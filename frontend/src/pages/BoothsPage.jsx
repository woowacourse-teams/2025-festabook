import React, { useState } from 'react';
import { useData } from '../hooks/useData';
import { useModal } from '../hooks/useModal';
import { placeCategories } from '../constants/categories';

const BoothDetails = ({ booth, openModal, handleSave, showToast, updateBooth }) => {
    return (
        <div className="p-6 bg-gray-50">
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                <div className="md:col-span-2">
                    <h4 className="font-semibold text-lg mb-2">상세 정보</h4>
                    <p className="text-gray-700 whitespace-pre-wrap mb-4">{booth.description}</p>
                     <div className="text-sm text-gray-600 space-y-1">
                        <p><i className="fas fa-map-marker-alt w-4 mr-2"></i>위치: {booth.location}</p>
                        <p><i className="fas fa-user-friends w-4 mr-2"></i>운영 주체: {booth.host}</p>
                        <p><i className="fas fa-clock w-4 mr-2"></i>운영 시간: {booth.startTime} - {booth.endTime}</p>
                    </div>
                    <h4 className="font-semibold text-lg mt-4 mb-2">공지사항</h4>
                    {booth.notices && booth.notices.length > 0 ? (
                        <ul className="list-disc list-inside text-sm text-gray-700 space-y-1">
                            {booth.notices.map(notice => <li key={notice.id}>{notice.text}</li>)}
                        </ul>
                    ) : <p className="text-sm text-gray-500">공지사항 없음</p>}
                </div>
                <div>
                     <h4 className="font-semibold text-lg mb-2">사진</h4>
                     <div className="grid grid-cols-2 gap-2 mb-10">
                        {booth.images && booth.images.length > 0 ? booth.images.map((img, index) => (
                            <div key={index} className="relative">
                                <img src={img} alt={`${booth.title} ${index+1}`} className="w-full h-24 object-cover rounded-md"/>
                                 {index === booth.mainImageIndex && <span className="main-image-indicator">대표</span>}
                            </div>
                        )) : (
                            <p className="text-sm text-gray-500">사진 없음</p>
                        )}
                     </div>
                </div>
            </div>
            <div className="flex items-center gap-4 justify-end mt-2">
                <button onClick={() => openModal('copyLink', { link: `https://example.com/edit?key=${booth.editKey}` })} className="text-green-600 hover:text-green-800 text-sm font-semibold">권한 링크 복사</button>
                <button onClick={() => openModal('booth', { booth, onSave: handleSave })} className="text-blue-600 hover:text-blue-800 text-sm font-semibold">수정</button>
                <button onClick={() => {
                    openModal('confirm', {
                        title: '플레이스 삭제 확인',
                        message: `'${booth.title}' 플레이스를 정말 삭제하시겠습니까?`,
                        onConfirm: () => {
                            updateBooth(booth.id, { ...booth, _delete: true });
                            showToast('플레이스가 삭제되었습니다.');
                        }
                    });
                }} className="text-red-600 hover:text-red-800 text-sm font-semibold">삭제</button>
            </div>
        </div>
    );
};

const BoothsPage = () => {
    const { booths, addBooth, updateBooth } = useData();
    const { openModal, showToast } = useModal();
    const [expandedIds, setExpandedIds] = useState([]);

    const toggleExpand = id => {
        setExpandedIds(prev => 
            prev.includes(id) 
                ? prev.filter(expandedId => expandedId !== id)
                : [...prev, id]
        );
    };
    
    const handleSave = (data) => {
        if (!data.title || !data.category) { showToast('플레이스명과 카테고리는 필수 항목입니다.'); return; }
        if (data.id) {
            updateBooth(data.id, data);
            showToast('플레이스 정보가 수정되었습니다.');
        } else {
            const newBoothData = {
                title: data.title,
                category: data.category,
                description: '상세 정보가 아직 없습니다.',
                images: [],
                mainImageIndex: -1,
                location: '미지정',
                host: '미지정',
                startTime: '00:00',
                endTime: '00:00',
                notices: [],
                editKey: `key${Date.now()}`
            };
            addBooth(newBoothData);
            showToast('새 플레이스가 추가되었습니다.');
        }
    };

    return (
        <div>
            <div className="flex justify-between items-center mb-6"><h2 className="text-3xl font-bold">플레이스</h2><button onClick={() => openModal('booth', { onSave: handleSave })} className="bg-gray-800 hover:bg-gray-900 text-white font-bold py-2 px-4 rounded-lg flex items-center"><i className="fas fa-plus mr-2"></i> 새 플레이스 추가</button></div>
            <div className="bg-white rounded-lg shadow-sm border border-gray-200 overflow-x-auto">
                <table className="min-w-full w-full">
                    <thead className="table-header">
                        <tr>
                            <th className="p-4 text-left font-semibold min-w-[120px] w-1/4">플레이스명</th>
                            <th className="p-4 text-center font-semibold min-w-[100px] w-1/6">카테고리</th>
                            <th className="p-4 text-center font-semibold min-w-[180px] w-1/4">관리</th>
                        </tr>
                    </thead>
                    <tbody>
                        {booths.map(booth => (
                            <React.Fragment key={booth.id}>
                                <tr className="border-b border-gray-200 last:border-b-0 hover:bg-gray-50">
                                    <td className="p-4 truncate align-middle text-left max-w-xs" title={booth.title}>{booth.title}</td>
                                    <td className="p-4 text-gray-600 align-middle text-center max-w-xs truncate" title={placeCategories[booth.category]}>{placeCategories[booth.category]}</td>
                                    <td className="p-4 align-middle text-right min-w-[180px]">
                                        <div className="flex items-center justify-end space-x-4 flex-wrap">
                                            <button
                                                onClick={() => !['SMOKING', 'TRASH_CAN'].includes(booth.category) && toggleExpand(booth.id)}
                                                className={`text-gray-600 hover:text-gray-900 text-sm font-semibold ${['SMOKING', 'TRASH_CAN'].includes(booth.category) ? 'invisible' : ''}`}
                                                tabIndex={['SMOKING', 'TRASH_CAN'].includes(booth.category) ? -1 : 0}
                                                disabled={['SMOKING', 'TRASH_CAN'].includes(booth.category)}
                                            >
                                                <i className={`fas ${expandedIds.includes(booth.id) ? 'fa-chevron-up' : 'fa-chevron-down'} mr-1`}></i>
                                                상세보기
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                                <tr>
                                    <td colSpan="3" className="p-0">
                                        <div className={`details-row-container ${expandedIds.includes(booth.id) ? 'open' : ''}`}>
                                            {expandedIds.includes(booth.id) && (
                                                <BoothDetails 
                                                    booth={booth} 
                                                    openModal={openModal}
                                                    handleSave={handleSave}
                                                    showToast={showToast}
                                                    updateBooth={updateBooth}
                                                />
                                            )}
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

export default BoothsPage;

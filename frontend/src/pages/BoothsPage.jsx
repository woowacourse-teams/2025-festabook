import React, { useState, useEffect } from 'react';
import { useModal } from '../hooks/useModal';
import { placeCategories } from '../constants/categories';
import api from '../utils/api';
import { isMainPlace, getDefaultValueIfNull, defaultBooth } from '../utils/booth';

const BoothDetails = ({ booth, openModal, handleSave, openDeleteModal, showToast, updateBooth, isMainPlace }) => {

    const newBooth = defaultBooth(booth);

    const renderAnnouncement = (announcement) => {
        // const maxTitle = 20;
        let content = announcement.content;

        // TODO 글자수 제한을 두려면 수정할 것
        // if (announcement.content.length > maxTitle) {
        //     content = announcement.content.substring(0, maxTitle);
        // }

        return <li key={announcement.id}>{announcement.title} - {content}</li>
    }

    const renderImage = (mainIdx, title, image, idx) => {
        const alt = `${title} ${idx + 1}`;
        const isMainImage = (image.id === mainIdx);


        return <div key={image.imageUrl} className="relative">
            <img src={image.imageUrl} alt={alt} className="w-full h-24 object-cover rounded-md" />
            {isMainImage && <span className="main-image-indicator">대표</span>}
        </div>
    }

    return (
        <div className="p-6 bg-gray-50">
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                <div className="md:col-span-2">

                    <h4 className="font-semibold text-lg mb-2">상세 정보</h4>
                    <p className="text-gray-700 whitespace-pre-wrap mb-4">{newBooth.description}</p>
                    <div className="text-sm text-gray-600 space-y-1">
                        <p><i className="fas fa-map-marker-alt w-4 mr-2"></i>위치: {newBooth.location}</p>
                        <p><i className="fas fa-user-friends w-4 mr-2"></i>운영 주체: {newBooth.host}</p>
                        <p><i className="fas fa-clock w-4 mr-2"></i>운영 시간: {newBooth.startTime} - {newBooth.endTime}</p>
                    </div>

                    <h4 className="font-semibold text-lg mt-4 mb-2">공지사항</h4>
                    {newBooth.placeAnnouncements && newBooth.placeAnnouncements.length > 0 ? (
                        <ul className="list-disc list-inside text-sm text-gray-700 space-y-1">
                            {newBooth.placeAnnouncements.map(announcement => renderAnnouncement(announcement))}
                        </ul>
                    ) : <p className="text-sm text-gray-500">공지사항 없음</p>}
                </div>
                <div>

                    <h4 className="font-semibold text-lg mb-2">사진</h4>
                    <div className="grid grid-cols-2 gap-2 mb-10">
                        {newBooth.placeImages && newBooth.placeImages.length > 0 ? newBooth.placeImages.map((image, idx) => (
                            renderImage(newBooth.placeImages[0].id, newBooth.title, image, idx)
                        )) : (
                            <p className="text-sm text-gray-500">사진 없음</p>
                        )}

                    </div>
                </div>
            </div>
            <div className="flex items-center gap-4 justify-end mt-2">
                <button onClick={() => openModal('copyLink', { link: `https://example.com/edit?key=${newBooth.editKey}` })} className="text-green-600 hover:text-green-800 text-sm font-semibold">권한 링크 복사</button>
                <button onClick={() => openModal('booth', { booth: newBooth, onSave: handleSave, isMainPlace: isMainPlace })} className="text-blue-600 hover:text-blue-800 text-sm font-semibold">수정</button>
                <button onClick={() => openDeleteModal(newBooth)}
                    className="text-red-600 hover:text-red-800 text-sm font-semibold">삭제</button>
            </div>
        </div>
    );
};

const BoothsPage = () => {
    const { openModal, showToast } = useModal();
    const [booths, setBooths] = useState([]);
    const [expandedIds, setExpandedIds] = useState([]);
    const [loading, setLoading] = useState(false);

    // 1. Booth 목록 불러오기
    useEffect(() => {
        setLoading(true);
        api.get('/places')
            .then(res => {
                setBooths(res.data.map(defaultBooth));
            })
            .catch(() => showToast('플레이스 목록을 불러오지 못했습니다.'))
            .finally(() => setLoading(false));
    }, []);

    const toggleExpand = id => {
        setExpandedIds(prev =>
            prev.includes(id)
                ? prev.filter(expandedId => expandedId !== id)
                : [...prev, id]
        );
    };

    // 2. Booth 생성
    const handleCreate = async (data) => {
        if (!data.category) { showToast('카테고리는 필수 항목입니다.'); return; }
        try {
            setLoading(true);
            const res = await api.post('/places', { placeCategory: data.category });
            // 201 응답이면 성공 처리, 응답 구조 반영
            if (res.status === 201 && res.data) {
                const newBooth = defaultBooth(res.data);
                setBooths(prev => [newBooth, ...prev]);
                showToast('새 플레이스가 추가되었습니다.');
            } else {
                showToast('플레이스 생성에 실패했습니다.');
            }
        } catch (e) {
            showToast('플레이스 생성에 실패했습니다.');
        } finally {
            setLoading(false);
        }
    };

    // 3. Booth 삭제
    const handleDelete = async (id) => {
        try {
            setLoading(true);
            const res = await api.delete(`/places/${id}`);
            // 201 응답이면 성공 처리, 응답 구조 반영
            if (res.status === 204) {
                setBooths(prevBooths => prevBooths.filter(booth => booth.id !== id));
                showToast('성공적으로 플레이스가 삭제되었습니다.');
            } else {
                showToast('플레이스 삭제에 실패했습니다.');
            }
        } catch (e) {
            showToast('플레이스 삭제에 실패했습니다.');
        } finally {
            setLoading(false);
        }
    };

    const openDeleteModal = (booth) => {
        openModal('confirm', {
            title: '플레이스 삭제 확인',
            message: (
                <>
                    {booth.title} 플레이스를 정말 삭제하시겠습니까?
                    <br />
                    <div className="font-bold text-red-500 text-xs">
                        플레이스의 즐겨찾기 정보, 이미지, 세부 정보, 세부 공지사항도 모두 삭제됩니다.
                    </div>
                </>
            ),
            onConfirm: () => {
                handleDelete(booth.id);
            }
        });
    }

    // 기존 handleSave는 수정만 담당
    const handleSave = (data) => {
        if (!data.category) { showToast('카테고리는 필수 항목입니다.'); return; }
        // TODO: 수정 API 연동 필요 (현재는 로컬 상태만 갱신)
        setBooths(prev => prev.map(b => b.id === data.id ? { ...b, ...data } : b));
        showToast('플레이스 정보가 수정되었습니다.');
    };

    return (
        <div>
            <div className="flex justify-between items-center mb-6">
                <h2 className="text-3xl font-bold">플레이스</h2>
                <button onClick={() => openModal('booth', { onSave: handleCreate })} className="bg-gray-800 hover:bg-gray-900 text-white font-bold py-2 px-4 rounded-lg flex items-center">
                    <i className="fas fa-plus mr-2"></i> 새 플레이스 추가
                </button>
            </div>
            {loading ? (
                <div className="p-8 text-center text-gray-500">로딩 중...</div>
            ) : (
                <div>
                    <div className='text-xl font-bold ml-1 mt-10 mb-2'>
                        메인 플레이스
                    </div>
                    <div className="bg-white rounded-lg shadow-sm border border-gray-200 overflow-x-auto">
                        <table className="min-w-full w-full">
                            <thead className="table-header">
                                <tr>
                                    <th className="p-4 text-left font-semibold min-w-[120px] w-1/4">플레이스명</th>
                                    <th className="p-4 text-center font-semibold min-w-[100px] w-1/6">카테고리</th>
                                    <th className="p-4 text-right font-semibold min-w-[180px] w-1/4">관리</th>
                                </tr>
                            </thead>
                            <tbody>
                                {booths.map(booth => (
                                    isMainPlace(booth.category) ?
                                        <React.Fragment key={booth.id}>
                                            <tr className="border-b border-gray-200 last:border-b-0 hover:bg-gray-50">
                                                <td className="p-4 truncate align-middle text-left max-w-xs" title={booth.title}>{booth.title}</td>
                                                <td className="p-4 text-gray-600 align-middle text-center max-w-xs truncate" title={placeCategories[booth.category]}>{placeCategories[booth.category]}</td>
                                                <td className="p-4 align-middle text-right min-w-[180px]">
                                                    <div className="flex items-center justify-end space-x-4 flex-wrap">
                                                        <button
                                                            onClick={() => toggleExpand(booth.id)}
                                                            className={`text-gray-600 hover:text-gray-900 text-sm font-semibold`}
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
                                                                openDeleteModal={openDeleteModal}
                                                                openModal={openModal}
                                                                handleSave={handleSave}
                                                                showToast={showToast}
                                                                isMainPlace={isMainPlace}
                                                                updateBooth={(id, data) => setBooths(prev => prev.map(b => b.id === id ? { ...b, ...data } : b))}
                                                            />
                                                        )}
                                                    </div>
                                                </td>
                                            </tr>
                                        </React.Fragment> : <></>
                                ))}
                            </tbody>
                        </table>
                    </div>


                    <div className='text-xl font-bold ml-1 mt-10 mb-2'>
                        기타 플레이스
                    </div>
                    <div className="bg-white rounded-lg shadow-sm border border-gray-200 overflow-x-auto ">
                        <table className="min-w-full w-full">
                            <thead className="table-header">
                                <tr>
                                    <th className="p-4 text-left font-semibold min-w-[120px] w-1/4">플레이스명</th>
                                    <th className="p-4 text-center font-semibold min-w-[100px] w-1/6">카테고리</th>
                                    <th className="p-4 text-right font-semibold min-w-[180px] w-1/4">관리</th>
                                </tr>
                            </thead>
                            <tbody>
                                {booths.map(booth => (
                                    !isMainPlace(booth.category) ?
                                        <React.Fragment key={booth.id}>
                                            <tr className="border-b border-gray-200 last:border-b-0 hover:bg-gray-50">
                                                <td className="p-4 truncate align-middle text-left max-w-xs" title={booth.title}>{booth.title}</td>
                                                <td className="p-4 text-gray-600 align-middle text-center max-w-xs truncate" title={placeCategories[booth.category]}>{placeCategories[booth.category]}</td>
                                                <td className="p-4 align-middle text-right min-w-[180px]">
                                                    <div className="flex items-center justify-end space-x-4 flex-wrap">
                                                        <button
                                                            onClick={() => openDeleteModal(booth)}
                                                            className="text-red-600 hover:text-red-800 text-sm font-semibold">
                                                            삭제
                                                        </button>
                                                    </div>
                                                </td>
                                            </tr>
                                        </React.Fragment> : <></>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            )}

        </div>
    );
};

export default BoothsPage;

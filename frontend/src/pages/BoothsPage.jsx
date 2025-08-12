import React, { useState, useEffect } from 'react';
import { useModal } from '../hooks/useModal';
import { placeCategories } from '../data/categories';
import { placeAPI } from '../utils/api';
import { BoothsPageSkeleton } from '../components/common/Skeleton';

const BoothDetails = ({ booth, openModal, handleSave, openDeleteModal, updateBooth, handleImageUpdate, handleNoticeCreate }) => {
    const [imageLoadingStates, setImageLoadingStates] = useState({});

    const handleImageLoad = (index) => {
        setImageLoadingStates(prev => ({ ...prev, [index]: true }));
    };

    const defaultBooth = (booth) => {
        return {
            placeId: booth.placeId,
            category: booth.category,
            placeImages: booth.placeImages || [],
            placeAnnouncements: booth.placeAnnouncements || [],
            // 기존 코드와의 호환성을 위한 필드들 (DTO 반영)
            images: (booth.placeImages || []).map(img => img.imageUrl),
            notices: booth.placeAnnouncements || [],
            mainImageIndex: 0,

            title: getDefaultValueIfNull('플레이스 이름을 지정하여 주십시오.', booth.title),
            description: getDefaultValueIfNull('플레이스 설명이 아직 없습니다.', booth.description),
            startTime: getDefaultValueIfNull('00:00', booth.startTime),
            endTime: getDefaultValueIfNull('00:00', booth.endTime),
            location: getDefaultValueIfNull('미지정', booth.location),
            host: getDefaultValueIfNull('미지정', booth.host),
        }
    }

    const getDefaultValueIfNull = (defaultValue, nullableValue) => {
        return nullableValue === null ? defaultValue : nullableValue;
    }

    React.useEffect(() => {
        if (
            booth.title === null ||
            booth.description === null ||
            booth.startTime === null ||
            booth.endTime === null ||
            booth.location === null ||
            booth.host === null
        ) {
            const newBooth = defaultBooth(booth);
            updateBooth(newBooth.placeId, newBooth);
        }
    }, [
        booth.placeId,
        booth.title,
        booth.description,
        booth.startTime,
        booth.endTime,
        booth.location,
        booth.host,
        updateBooth
    ]);

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
                        <ul className="list-disc list-inside text-sm text-gray-700 space-y-2">
                            {booth.notices.map(notice => (
                                <li key={notice.id} className="flex items-start">
                                    <span className="mr-2 mt-1">•</span>
                                    <div className="flex-1">
                                        <div>
                                            <span className="font-medium">{notice.title}</span>
                                            <span className="mx-1">-</span>
                                            <span>{notice.content}</span>
                                        </div>
                                        {notice.createdAt && (
                                            <div className="text-xs text-gray-400 mt-1">
                                                {new Date(notice.createdAt).toLocaleString('ko-KR', {
                                                    year: 'numeric',
                                                    month: '2-digit',
                                                    day: '2-digit',
                                                    hour: '2-digit',
                                                    minute: '2-digit'
                                                }).replace(/\./g, '-').replace(/\s/g, ' ')}
                                            </div>
                                        )}
                                    </div>
                                </li>
                            ))}
                        </ul>
                    ) : <p className="text-sm text-gray-500">공지사항 없음</p>}

                </div>
                <div>
                    <h4 className="font-semibold text-lg mb-2">사진</h4>
                    <div className="grid grid-cols-3 gap-2 mb-10">
                        {booth.images && booth.images.length > 0 ? booth.images.map((img, index) => (
                            <div key={index} className="relative aspect-square">
                                {!imageLoadingStates[index] && (
                                    <div className="w-full h-full bg-gray-200 rounded-md animate-pulse"></div>
                                )}
                                <img 
                                    src={img} 
                                    alt={`${booth.title} ${index + 1}`} 
                                    className={`w-full h-full object-cover rounded-md transition-opacity duration-300 ${
                                        imageLoadingStates[index] ? 'opacity-100' : 'opacity-0'
                                    }`}
                                    onLoad={() => handleImageLoad(index)}
                                    style={{ display: imageLoadingStates[index] ? 'block' : 'none' }}
                                />
                                {index === booth.mainImageIndex && imageLoadingStates[index] && (
                                    <span className="main-image-indicator">대표</span>
                                )}
                            </div>
                        )) : (
                            <p className="text-sm text-gray-500">사진 없음</p>
                        )}
                    </div>
                </div>
            </div>
            <div className="flex items-center gap-4 justify-end mt-2">
                <button onClick={() => openModal('placeNotice', { place: booth, onSave: handleNoticeCreate })} className="text-orange-600 hover:text-orange-800 text-sm font-semibold">플레이스 관리</button>
                <button onClick={() => openModal('placeImages', { place: booth, onUpdate: handleImageUpdate })} className="text-purple-600 hover:text-purple-800 text-sm font-semibold">이미지 관리</button>
                <button onClick={() => openModal('booth', { booth, onSave: handleSave })} className="text-blue-600 hover:text-blue-800 text-sm font-semibold">세부사항 수정</button>
                <button onClick={() => openDeleteModal(booth)}
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

    // defaultBooth 메서드 (유저가 만든 것 사용)
    const getDefaultValueIfNull = (defaultValue, nullableValue) => nullableValue === null ? defaultValue : nullableValue;
    const defaultBooth = (booth) => ({
        placeId: booth.placeId,
        category: booth.category,
        placeImages: booth.placeImages || [],
        placeAnnouncements: booth.placeAnnouncements || [],
        // 흡연구역, 쓰레기통은 title을 category명으로 세팅
        title: ['SMOKING', 'TRASH_CAN'].includes(booth.category)
            ? placeCategories[booth.category]
            : getDefaultValueIfNull('플레이스 이름을 지정하여 주십시오.', booth.title),
        description: getDefaultValueIfNull('플레이스 설명이 아직 없습니다.', booth.description),
        startTime: getDefaultValueIfNull('00:00', booth.startTime),
        endTime: getDefaultValueIfNull('00:00', booth.endTime),
        location: getDefaultValueIfNull('미지정', booth.location),
        host: getDefaultValueIfNull('미지정', booth.host),
        // 기존 코드와의 호환성을 위한 필드들
        images: (booth.placeImages || []).map(img => img.imageUrl),
        notices: booth.placeAnnouncements || [],
    });

    // 1. Booth 목록 불러오기
    useEffect(() => {
        const fetchPlaces = async () => {
            try {
                setLoading(true);
                const places = await placeAPI.getPlaces();
                setBooths(places.map(defaultBooth));
            } catch (error) {
                showToast('플레이스 목록을 불러오지 못했습니다.');
                console.error('Failed to fetch places:', error);
            } finally {
                setLoading(false);
            }
        };
        
        fetchPlaces();
    }, []);

    const toggleExpand = placeId => {
        setExpandedIds(prev =>
            prev.includes(placeId)
                ? prev.filter(expandedId => expandedId !== placeId)
                : [...prev, placeId]
        );
    };

    // 2. Booth 생성
    const handleCreate = async (data) => {
        if (!data.category) { showToast('카테고리는 필수 항목입니다.'); return; }
        try {
            setLoading(true);
            await placeAPI.createPlace({ placeCategory: data.category });
            // 성공 후 목록 다시 조회
            const places = await placeAPI.getPlaces();
            setBooths(places.map(defaultBooth));
            showToast('새 플레이스가 추가되었습니다.');
        } catch {
            showToast('플레이스 생성에 실패했습니다.');
        } finally {
            setLoading(false);
        }
    };

    // 3. Booth 삭제
    const handleDelete = async (placeId) => {
        try {
            setLoading(true);
            await placeAPI.deletePlace(placeId);
            // 성공 후 목록 다시 조회
            const places = await placeAPI.getPlaces();
            setBooths(places.map(defaultBooth));
            showToast('성공적으로 플레이스가 삭제되었습니다.');
        } catch {
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
                handleDelete(booth.placeId);
            }
        });
    }

    // 기존 handleSave는 수정만 담당
    const handleSave = (data) => {
        if (!data.category) { showToast('카테고리는 필수 항목입니다.'); return; }
        // TODO: 수정 API 연동 필요 (현재는 로컬 상태만 갱신)
        setBooths(prev => prev.map(prevBooth => {
            if (prevBooth.placeId !== data.placeId) return prevBooth;

            return {
                ...prevBooth,
                ...data,
                // 공지사항과 이미지는 별도 모달에서 처리되므로 기존 값 유지
                placeImages: prevBooth.placeImages,
                placeAnnouncements: prevBooth.placeAnnouncements,
                images: prevBooth.images,
                notices: prevBooth.notices,
            };
        }));
        showToast('플레이스 정보가 수정되었습니다.');
    };

    // 이미지 수정 전용 핸들러
    const handleImageUpdate = (data) => {
        setBooths(prev => prev.map(prevBooth => {
            if (prevBooth.placeId !== data.placeId) return prevBooth;

            return {
                ...prevBooth,
                placeImages: data.placeImages || [],
                images: (data.placeImages || []).map(img => img.imageUrl),
            };
        }));
        showToast('플레이스 이미지가 수정되었습니다.');
    };

    // 공지 관리 전용 핸들러
    const handleNoticeCreate = (data) => {
        setBooths(prev => prev.map(prevBooth => {
            if (prevBooth.placeId !== data.placeId) return prevBooth;

            return {
                ...prevBooth,
                placeAnnouncements: data.placeAnnouncements || [],
                notices: data.placeAnnouncements || [],
            };
        }));
        showToast('플레이스 공지가 업데이트되었습니다.');
    };

    const isMainPlace = (category) => {
        return !['SMOKING', 'TRASH_CAN'].includes(category);
    }

    return (
        <div>
            <div className="flex justify-between items-center mb-6">
                <h2 className="text-3xl font-bold">플레이스 대시보드</h2>
                <button onClick={() => openModal('booth', { onSave: handleCreate })} className="bg-gray-800 hover:bg-gray-900 text-white font-bold py-2 px-4 rounded-lg flex items-center">
                    <i className="fas fa-plus mr-2"></i> 새 플레이스 추가
                </button>
            </div>
            {loading ? (
                <BoothsPageSkeleton />
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
                                        <React.Fragment key={booth.placeId}>
                                            <tr className="border-b border-gray-200 last:border-b-0 hover:bg-gray-50">
                                                <td className="p-4 truncate align-middle text-left max-w-xs" title={booth.title}>{booth.title}</td>
                                                <td className="p-4 text-gray-600 align-middle text-center max-w-xs truncate" title={placeCategories[booth.category]}>{placeCategories[booth.category]}</td>
                                                <td className="p-4 align-middle text-right min-w-[180px]">
                                                    <div className="flex items-center justify-end space-x-4 flex-wrap">
                                                        <button
                                                            onClick={() => toggleExpand(booth.placeId)}
                                                            className={`text-gray-600 hover:text-gray-900 text-sm font-semibold`}
                                                        >
                                                            <i className={`fas ${expandedIds.includes(booth.placeId) ? 'fa-chevron-up' : 'fa-chevron-down'} mr-1`}></i>
                                                            상세보기
                                                        </button>
                                                    </div>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td colSpan="3" className="p-0">
                                                    <div className={`details-row-container ${expandedIds.includes(booth.placeId) ? 'open' : ''}`}>
                                                        {expandedIds.includes(booth.placeId) && (
                                                            <BoothDetails
                                                                booth={booth}
                                                                openDeleteModal={openDeleteModal}
                                                                openModal={openModal}
                                                                handleSave={handleSave}
                                                                handleImageUpdate={handleImageUpdate}
                                                                handleNoticeCreate={handleNoticeCreate}
                                                                updateBooth={(id, data) => setBooths(prev => prev.map(b => b.placeId === id ? { ...b, ...data } : b))}
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
                                        <React.Fragment key={booth.placeId}>
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

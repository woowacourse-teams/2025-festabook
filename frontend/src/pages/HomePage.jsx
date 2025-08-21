import React, { useState, useEffect, useRef } from 'react';
import { useModal } from '../hooks/useModal';
import { festivalAPI, lineupAPI } from '../utils/api';

const HomePage = () => {
    const { openModal, showToast } = useModal();
    const [festival, setFestival] = useState(null);
    const [lineups, setLineups] = useState([]);
    const [loading, setLoading] = useState(true);
    const [isDragging, setIsDragging] = useState(false);
    const scrollContainerRef = useRef(null);
    const dragStartRef = useRef(null);
    const scrollLeftRef = useRef(null);

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        try {
            setLoading(true);
            const festivalId = localStorage.getItem('festivalId');
            if (!festivalId) {
                showToast('Festival ID가 설정되지 않았습니다.');
                return;
            }
            
            // 축제 정보와 라인업 정보를 병렬로 가져오기
            const [festivalData, lineupsData] = await Promise.all([
                festivalAPI.getFestival(),
                lineupAPI.getLineups()
            ]);
            
            setFestival(festivalData);
            setLineups(lineupsData);
        } catch (error) {
            if (error.response?.status === 404) {
                showToast('축제 정보를 찾을 수 없습니다. Festival ID를 확인해주세요.');
            } else if (error.response?.status === 401) {
                showToast('인증에 실패했습니다. Festival ID를 다시 설정해주세요.');
            } else if (error.code === 'NETWORK_ERROR') {
                showToast('네트워크 연결을 확인해주세요.');
            } else {
                showToast(`데이터를 불러오는데 실패했습니다. (${error.response?.status || '알 수 없는 오류'})`);
            }
        } finally {
            setLoading(false);
        }
    };

    const formatDate = (dateString) => {
        const date = new Date(dateString);
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}년 ${month}월 ${day}일`;
    };

    const formatDateTime = (dateTimeString) => {
        const date = new Date(dateTimeString);
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        return `${month}.${day} ${hours}:${minutes}`;
    };

    const handleMouseDown = (e) => {
        setIsDragging(true);
        dragStartRef.current = e.pageX - scrollContainerRef.current.offsetLeft;
        scrollLeftRef.current = scrollContainerRef.current.scrollLeft;
    };

    const handleMouseMove = (e) => {
        if (!isDragging) return;
        e.preventDefault();
        const x = e.pageX - scrollContainerRef.current.offsetLeft;
        const walk = (x - dragStartRef.current) * 2;
        scrollContainerRef.current.scrollLeft = scrollLeftRef.current - walk;
    };

    const handleMouseUp = () => {
        setIsDragging(false);
    };

    const handleMouseLeave = () => {
        setIsDragging(false);
    };

    if (loading) {
        return (
            <div className="flex items-center justify-center h-64">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-black"></div>
            </div>
        );
    }

    if (!festival) {
        return (
            <div className="text-center py-12">
                <p className="text-gray-600 mb-4">축제 정보를 불러올 수 없습니다.</p>
                <button 
                    onClick={fetchData}
                    className="bg-black text-white px-4 py-2 rounded-lg hover:bg-gray-800 transition-colors"
                >
                    다시 시도
                </button>
            </div>
        );
    }

    return (
        <div className="w-full">
            {/* 헤더 섹션 */}
            <div className="mb-8">
                <div className="flex justify-between items-start">
                    <div>
                        <h1 className="text-3xl font-bold text-gray-900 mb-2">
                            {festival.universityName} 관리
                        </h1>
                    </div>
                </div>
            </div>

            {/* 축제 기본 정보 */}
            <div className="bg-white rounded-xl shadow-lg p-6 mb-8">
                <div className="flex justify-between items-start mb-4">
                    <h3 className="text-lg font-semibold text-gray-900">축제 정보</h3>
                    <button 
                        onClick={() => openModal('festival-info', { festival, onUpdate: setFestival })}
                        className="text-black hover:text-gray-700 text-sm font-medium"
                    >
                        수정
                    </button>
                </div>
                <div className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">축제명</label>
                        <p className="text-lg font-semibold text-gray-900 whitespace-pre-line">{festival.festivalName}</p>
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">기간</label>
                        <p className="text-gray-900">{formatDate(festival.startDate)} - {formatDate(festival.endDate)}</p>
                    </div>
                </div>
            </div>

            {/* 축제 이미지 갤러리 */}
            <div className="bg-white rounded-xl shadow-lg p-6 mb-8">
                <div className="flex justify-between items-center mb-6">
                    <h3 className="text-lg font-semibold text-gray-900">축제 이미지</h3>
                    <button 
                        onClick={() => openModal('festival-images', { festival, onUpdate: setFestival })}
                        className="text-black hover:text-gray-700 text-sm font-medium"
                    >
                        수정
                    </button>
                </div>
                
                {festival.festivalImages && festival.festivalImages.length > 0 ? (
                    <div 
                        ref={scrollContainerRef}
                        className={`overflow-x-auto ${isDragging ? 'cursor-grabbing' : 'cursor-grab'}`}
                        onMouseDown={handleMouseDown}
                        onMouseMove={handleMouseMove}
                        onMouseUp={handleMouseUp}
                        onMouseLeave={handleMouseLeave}
                    >
                        <div className="flex space-x-4 w-max select-none">
                            {festival.festivalImages.map((image, index) => (
                                <div
                                    key={image.festivalImageId || image.id || index}
                                    className="relative group flex-shrink-0 w-[300px] h-[400px] bg-gray-200 rounded-lg overflow-hidden"
                                >
                                    <img
                                        src={image.imageUrl}
                                        alt={`축제 이미지 ${index + 1}`}
                                        className="w-full h-full object-cover block pointer-events-none"
                                    />
                                    <div className="absolute top-2 right-2 bg-black bg-opacity-75 text-white text-xs px-2 py-1 rounded">
                                        {index + 1}
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                ) : (
                    <div className="text-center py-12">
                        <svg className="w-16 h-16 text-gray-400 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                                d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"/>
                        </svg>
                        <p className="text-gray-500 mb-4">축제 이미지가 없습니다</p>
                        <button
                            onClick={() => showToast('이미지 업로드 기능은 준비 중입니다.')}
                            className="bg-black text-white px-4 py-2 rounded-lg hover:bg-gray-800 transition-colors"
                        >
                            첫 번째 이미지 추가
                        </button>
                    </div>
                )}
            </div>

            {/* 축제 라인업 */}
            <div className="bg-white rounded-xl shadow-lg p-6 mb-8">
                <div className="flex justify-between items-center mb-6">
                    <h3 className="text-lg font-semibold text-gray-900">축제 라인업</h3>
                    <button 
                        onClick={() => openModal('lineup-add', { onUpdate: setLineups })}
                        className="text-black hover:text-gray-700 text-sm font-medium"
                    >
                        추가
                    </button>
                </div>
                
                {lineups.length > 0 ? (
                    <div 
                        ref={scrollContainerRef}
                        className={`overflow-x-auto ${isDragging ? 'cursor-grabbing' : 'cursor-grab'}`}
                        onMouseDown={handleMouseDown}
                        onMouseMove={handleMouseMove}
                        onMouseUp={handleMouseUp}
                        onMouseLeave={handleMouseLeave}
                    >
                        <div className="flex space-x-6 w-max select-none py-2">
                            {lineups.map((lineup, index) => (
                                <div
                                    key={lineup.lineupId}
                                    className="relative flex-shrink-0 text-center cursor-pointer hover:scale-105 transition-transform duration-300 ease-out"
                                    onClick={() => openModal('lineup-edit', { 
                                        lineup, 
                                        onUpdate: setLineups 
                                    })}
                                >
                                    {/* 개선된 프로필 이미지 - 축제 분위기 */}
                                    <div className="relative w-24 h-24 mb-3 mt-2">
                                        {/* 회색 테두리로 복구 */}
                                        <div className="w-full h-full bg-white overflow-hidden relative border-2 border-gray-300 shadow-2xl hover:shadow-black transition-shadow duration-300" style={{borderRadius: '50% 50% 50% 15%', boxShadow: '0 10px 25px rgba(0, 0, 0, 0.3), 0 6px 10px rgba(0, 0, 0, 0.2)'}}>
                                            <img
                                                src={lineup.imageUrl}
                                                alt={lineup.name}
                                                className="w-full h-full object-cover"
                                            />
                                        </div>
                                    </div>
                                    <p className="text-sm font-medium text-gray-900">{lineup.name}</p>
                                </div>
                            ))}
                        </div>
                    </div>
                ) : (
                    <div className="text-center py-12">
                        <svg className="w-16 h-16 text-gray-400 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                                d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"/>
                        </svg>
                        <p className="text-gray-500 mb-4">등록된 라인업이 없습니다</p>
                        <button
                            onClick={() => openModal('lineup-add', { onUpdate: setLineups })}
                            className="bg-black text-white px-4 py-2 rounded-lg hover:bg-gray-800 transition-colors"
                        >
                            첫 번째 라인업 추가
                        </button>
                    </div>
                )}
            </div>
        </div>
    );
};

export default HomePage; 
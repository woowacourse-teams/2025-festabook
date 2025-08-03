import React, { useState, useEffect, useRef } from 'react';
import { useModal } from '../hooks/useModal';
import api from '../utils/api';

const HomePage = () => {
    const { openModal, showToast } = useModal();
    const [organization, setOrganization] = useState(null);
    const [loading, setLoading] = useState(true);
    const [isDragging, setIsDragging] = useState(false);
    const scrollContainerRef = useRef(null);
    const dragStartRef = useRef(null);
    const scrollLeftRef = useRef(null);

    useEffect(() => {
        fetchOrganizationData();
    }, []);

    const fetchOrganizationData = async () => {
        try {
            setLoading(true);
            const orgId = localStorage.getItem('organization');
            if (!orgId) {
                showToast('Organization ID가 설정되지 않았습니다.');
                return;
            }
            const response = await api.get('/organizations');
            setOrganization(response.data);
        } catch (error) {
            if (error.response?.status === 404) {
                showToast('조직 정보를 찾을 수 없습니다. Organization ID를 확인해주세요.');
            } else if (error.response?.status === 401) {
                showToast('인증에 실패했습니다. Organization ID를 다시 설정해주세요.');
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

    if (!organization) {
        return (
            <div className="text-center py-12">
                <p className="text-gray-600 mb-4">조직 정보를 불러올 수 없습니다.</p>
                <button 
                    onClick={fetchOrganizationData}
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
                            {organization.universityName} 관리
                        </h1>
                    </div>
                </div>
            </div>

            {/* 축제 기본 정보 */}
            <div className="bg-white rounded-xl shadow-lg p-6 mb-8">
                <div className="flex justify-between items-start mb-4">
                    <h3 className="text-lg font-semibold text-gray-900">축제 정보</h3>
                    <button 
                        onClick={() => openModal('festival-info', { organization })}
                        className="text-black hover:text-gray-700 text-sm font-medium"
                    >
                        수정
                    </button>
                </div>
                <div className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">축제명</label>
                        <p className="text-lg font-semibold text-gray-900">{organization.festivalName}</p>
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">기간</label>
                        <p className="text-gray-900">{formatDate(organization.startDate)} - {formatDate(organization.endDate)}</p>
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">상태</label>
                        <span className={`inline-flex items-center px-3 py-0.5 rounded-full text-xs font-medium ${
                            organization.isActive !== false 
                                ? 'bg-black text-white' 
                                : 'bg-gray-300 text-gray-700'
                        }`}>
                            {organization.isActive !== false ? '활성' : '비활성'}
                        </span>
                    </div>
                </div>
            </div>

            {/* 축제 이미지 갤러리 */}
            <div className="bg-white rounded-xl shadow-lg p-6 mb-8">
                <div className="flex justify-between items-center mb-6">
                    <h3 className="text-lg font-semibold text-gray-900">축제 이미지</h3>
                    <button 
                        onClick={() => openModal('festival-images', { organization })}
                        className="text-black hover:text-gray-700 text-sm font-medium"
                    >
                        수정
                    </button>
                </div>
                
                {organization.festivalImages && organization.festivalImages.length > 0 ? (
                    <div 
                        ref={scrollContainerRef}
                        className={`overflow-x-auto ${isDragging ? 'cursor-grabbing' : 'cursor-grab'}`}
                        onMouseDown={handleMouseDown}
                        onMouseMove={handleMouseMove}
                        onMouseUp={handleMouseUp}
                        onMouseLeave={handleMouseLeave}
                    >
                        <div className="flex space-x-4 w-max select-none">
                            {organization.festivalImages.map((image, index) => (
                                <div
                                    key={image.id}
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
        </div>
    );
};

export default HomePage; 
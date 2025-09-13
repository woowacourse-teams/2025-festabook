import React, { useState, useEffect, useRef } from 'react';
import Modal from '../common/Modal';
import { lineupAPI, imageAPI } from '../../utils/api';

const LineupEditModal = ({ isOpen, onClose, lineup, showToast, onUpdate }) => {
    const [formData, setFormData] = useState({
        name: lineup?.name || '',
        imageUrl: lineup?.imageUrl || '',
        performanceDate: lineup?.performanceAt ? lineup.performanceAt.split('T')[0] : '',
        performanceTime: lineup?.performanceAt ? lineup.performanceAt.split('T')[1].slice(0, 5) : ''
    });
    const [selectedFile, setSelectedFile] = useState(null);
    const [isDragging, setIsDragging] = useState(false);
    const [isUploading, setIsUploading] = useState(false);
    const fileInputRef = useRef(null);
    
    // 날짜 필드들을 개별적으로 관리
    const [year, setYearState] = useState('');
    const [month, setMonthState] = useState('');
    const [day, setDayState] = useState('');
    
    // 시간 필드들을 개별적으로 관리
    const [hours, setHoursState] = useState('');
    const [minutes, setMinutesState] = useState('');

    // 개별 필드 업데이트 함수들
    const setYear = (newYear) => {
        setYearState(newYear);
        setFormData(prev => {
            const currentDate = prev.performanceDate || '';
            const [, currentMonth = '01', currentDay = '01'] = currentDate.split('-');
            return {
                ...prev,
                performanceDate: `${newYear}-${currentMonth}-${currentDay}`
            };
        });
    };

    const setMonth = (newMonth) => {
        setMonthState(newMonth);
        setFormData(prev => {
            const currentDate = prev.performanceDate || '';
            const [currentYear = new Date().getFullYear().toString(), , currentDay = '01'] = currentDate.split('-');
            return {
                ...prev,
                performanceDate: `${currentYear}-${newMonth.padStart(2, '0')}-${currentDay}`
            };
        });
    };

    const setDay = (newDay) => {
        setDayState(newDay);
        setFormData(prev => {
            const currentDate = prev.performanceDate || '';
            const [currentYear = new Date().getFullYear().toString(), currentMonth = '01'] = currentDate.split('-');
            return {
                ...prev,
                performanceDate: `${currentYear}-${currentMonth}-${newDay.padStart(2, '0')}`
            };
        });
    };

    // 시간 개별 필드 업데이트 함수들
    const setHours = (newHours) => {
        setHoursState(newHours);
        setFormData(prev => {
            const currentTime = prev.performanceTime || '';
            const [, currentMinutes = '00'] = currentTime.split(':');
            return {
                ...prev,
                performanceTime: `${newHours.padStart(2, '0')}:${currentMinutes}`
            };
        });
    };

    const setMinutes = (newMinutes) => {
        setMinutesState(newMinutes);
        setFormData(prev => {
            const currentTime = prev.performanceTime || '';
            const [currentHours = '00'] = currentTime.split(':');
            return {
                ...prev,
                performanceTime: `${currentHours}:${newMinutes.padStart(2, '0')}`
            };
        });
    };

    // lineup이 변경될 때마다 폼 데이터 업데이트
    useEffect(() => {
        if (lineup) {
            const performanceDate = lineup.performanceAt ? lineup.performanceAt.split('T')[0] : '';
            const performanceTime = lineup.performanceAt ? lineup.performanceAt.split('T')[1].slice(0, 5) : '';
            const [yearPart = '', monthPart = '', dayPart = ''] = performanceDate.split('-');
            const [hoursPart = '', minutesPart = ''] = performanceTime.split(':');
            
            setFormData({
                name: lineup.name || '',
                imageUrl: lineup.imageUrl || '',
                performanceDate: performanceDate,
                performanceTime: performanceTime
            });
            
            // 개별 날짜 상태도 업데이트
            setYearState(yearPart);
            setMonthState(monthPart);
            setDayState(dayPart);
            
            // 개별 시간 상태도 업데이트
            setHoursState(hoursPart);
            setMinutesState(minutesPart);
        }
    }, [lineup]);

    // ESC 키 이벤트 리스너
    useEffect(() => {
        const handleEscKey = (event) => {
            if (event.key === 'Escape' && !isUploading) {
                onClose();
            }
        };

        document.addEventListener('keydown', handleEscKey);

        return () => {
            document.removeEventListener('keydown', handleEscKey);
        };
    }, [onClose, isUploading]);

    // 모달이 닫힐 때 상태 초기화
    useEffect(() => {
        if (!isOpen) {
            setSelectedFile(null);
            setIsDragging(false);
            setIsUploading(false);
        }
    }, [isOpen]);

    const validateFile = (file) => {
        const allowedTypes = ['image/png', 'image/jpeg', 'image/jpg'];
        const maxSize = 5 * 1024 * 1024; // 5MB

        if (!allowedTypes.includes(file.type)) {
            showToast('PNG, JPG, JPEG 파일만 업로드 가능합니다.');
            return false;
        }

        if (file.size > maxSize) {
            showToast('파일 크기는 5MB 이하여야 합니다.');
            return false;
        }

        return true;
    };

    const handleFileSelect = (file) => {
        if (validateFile(file)) {
            setSelectedFile(file);
        }
    };

    const handleFileInputChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            handleFileSelect(file);
        }
    };

    const handleDragOver = (e) => {
        e.preventDefault();
        setIsDragging(true);
    };

    const handleDragLeave = (e) => {
        e.preventDefault();
        setIsDragging(false);
    };

    const handleDrop = (e) => {
        e.preventDefault();
        setIsDragging(false);
        
        const files = e.dataTransfer.files;
        if (files.length > 0) {
            handleFileSelect(files[0]);
        }
    };

    const handleUploadClick = () => {
        fileInputRef.current?.click();
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        if (!formData.name.trim()) {
            showToast('아티스트 이름을 입력해주세요.');
            return;
        }

        if (!selectedFile && !lineup?.imageUrl) {
            showToast('이미지를 선택해주세요.');
            return;
        }

        if (!formData.performanceDate || !formData.performanceTime) {
            showToast('공연 일시를 입력해주세요.');
            return;
        }
        
        setIsUploading(true);
        
        try {
            let finalImageUrl = lineup?.imageUrl || '';
            
            if (selectedFile) {
                // 1단계: 이미지 파일을 백엔드 이미지 업로드 API로 전송
                const uploadResponse = await imageAPI.uploadImage(selectedFile);
                finalImageUrl = uploadResponse.imageUrl;
            }

            // 2단계: 라인업 API에 이미지 URL과 함께 데이터 전송
            const performanceAt = `${formData.performanceDate}T${formData.performanceTime}:00`;
            const response = await lineupAPI.updateLineup(lineup.lineupId, {
                name: formData.name,
                imageUrl: finalImageUrl,
                performanceAt: performanceAt
            });
            
            // 상태 업데이트
            if (onUpdate) {
                onUpdate(prev => prev.map(item => 
                    item.lineupId === lineup.lineupId ? response : item
                ));
            }
            
            showToast('라인업이 성공적으로 수정되었습니다.');
            onClose();
        } catch (error) {
            console.error('Lineup update error:', error);
            showToast(error.message || '라인업 수정에 실패했습니다.');
        } finally {
            setIsUploading(false);
        }
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        
        // 글자 수 제한 체크
        if (name === 'name' && value.length > 50) {
            showToast('아티스트 이름은 50자 이내로 입력해주세요.');
            return;
        }
        
        // 시간 입력 실시간 제한
        if (name === 'performanceTime') {
            // 숫자와 콜론만 허용, 최대 5자 (HH:MM)
            const filteredValue = value.replace(/[^0-9:]/g, '');
            if (filteredValue.length <= 5) {
                // 콜론은 한 번만 허용
                const colonCount = (filteredValue.match(/:/g) || []).length;
                if (colonCount <= 1) {
                    setFormData(prev => ({
                        ...prev,
                        [name]: filteredValue
                    }));
                }
            }
            return;
        }
        
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleDelete = async () => {
        if (window.confirm(`${lineup?.name} 라인업을 삭제하시겠습니까?`)) {
            try {
                await lineupAPI.deleteLineup(lineup.lineupId);
                
                // 상태 업데이트
                if (onUpdate) {
                    onUpdate(prev => prev.filter(item => item.lineupId !== lineup.lineupId));
                }
                
                showToast('라인업이 성공적으로 삭제되었습니다.');
                onClose();
            } catch (error) {
                console.error('Lineup delete error:', error);
                showToast('라인업 삭제에 실패했습니다.');
            }
        }
    };

    const handleClose = () => {
        if (!isUploading) {
            onClose();
        }
    };

    return (
        <Modal isOpen={isOpen} onClose={handleClose} maxWidth="max-w-md">
            <form onSubmit={handleSubmit}>
                <h2 className="text-2xl font-bold mb-6 text-center">라인업 수정</h2>
                
                <div className="space-y-4">
                    <div>
                        <div className="flex justify-between items-center mb-2">
                            <label className="block text-sm font-medium text-gray-700">
                                아티스트 이름
                            </label>
                            <span className="text-xs text-gray-500">
                                {formData.name.length}/50
                            </span>
                        </div>
                        <input
                            type="text"
                            name="name"
                            value={formData.name}
                            onChange={handleChange}
                            className="block w-full border border-gray-300 rounded-lg shadow-sm py-2 px-3 focus:ring-black focus:border-black"
                            placeholder="아티스트 이름을 입력하세요 (50자 이내)"
                            required
                        />
                    </div>
                    
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            이미지 선택
                        </label>
                        <div
                            className={`border-2 border-dashed rounded-lg p-6 text-center cursor-pointer transition-colors ${
                                isDragging 
                                    ? 'border-blue-500 bg-blue-50' 
                                    : selectedFile 
                                        ? 'border-green-500 bg-green-50' 
                                        : lineup?.imageUrl
                                            ? 'border-green-500 bg-green-50'
                                            : 'border-gray-300 hover:border-gray-400'
                            }`}
                            onClick={handleUploadClick}
                            onDragOver={handleDragOver}
                            onDragLeave={handleDragLeave}
                            onDrop={handleDrop}
                        >
                            {selectedFile ? (
                                <div>
                                    <div className="w-12 h-12 mx-auto mb-3 bg-green-100 rounded-full flex items-center justify-center">
                                        <svg className="w-6 h-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                                        </svg>
                                    </div>
                                    <p className="text-green-700 font-medium mb-1">{selectedFile.name}</p>
                                    <p className="text-green-600 text-sm">
                                        {(selectedFile.size / 1024 / 1024).toFixed(2)} MB
                                    </p>
                                    <p className="text-gray-500 text-xs mt-2">클릭하여 다른 파일 선택</p>
                                </div>
                            ) : lineup?.imageUrl ? (
                                <div>
                                    <div className="w-12 h-12 mx-auto mb-3 bg-green-100 rounded-full flex items-center justify-center">
                                        <svg className="w-6 h-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                                        </svg>
                                    </div>
                                    <p className="text-green-700 font-medium mb-1">기존 이미지</p>
                                    <p className="text-green-600 text-sm">이미지가 선택되어 있습니다</p>
                                    <p className="text-gray-500 text-xs mt-2">클릭하여 다른 파일 선택</p>
                                </div>
                            ) : (
                                <div>
                                    <div className="w-12 h-12 mx-auto mb-3 bg-gray-100 rounded-full flex items-center justify-center">
                                        <svg className="w-6 h-6 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
                                        </svg>
                                    </div>
                                    <p className="text-gray-700 font-medium mb-1">클릭하여 이미지 선택</p>
                                    <p className="text-gray-500 text-sm">PNG, JPG, JPEG (최대 5MB)</p>
                                    <p className="text-gray-400 text-xs mt-2">또는 파일을 여기에 드래그하세요</p>
                                </div>
                            )}
                        </div>
                        
                        {/* 숨겨진 파일 입력 */}
                        <input
                            ref={fileInputRef}
                            type="file"
                            accept="image/png,image/jpeg,image/jpg"
                            onChange={handleFileInputChange}
                            className="hidden"
                        />
                    </div>
                    
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-3">
                            공연 일시
                        </label>
                        <div className="space-y-4">
                            {/* 날짜 선택 */}
                            <div>
                                <label className="block text-xs font-medium text-gray-600 mb-1">
                                    날짜
                                </label>
                                
                                <div className="space-y-2">
                                    <div className="grid grid-cols-3 gap-2 w-full">
                                        {/* 년도 선택 */}
                                        <select
                                            name="performanceYear"
                                            value={year}
                                            onChange={(e) => {
                                                const newYear = e.target.value;
                                                const [, m = '01', d = '01'] = formData.performanceDate
                                                ? formData.performanceDate.split('-')
                                                : ['', '01', '01'];

                                                setFormData(prev => ({
                                                ...prev,
                                                performanceDate: `${newYear}-${m}-${d}`,
                                                }));
                                                setYear(newYear);
                                            }}
                                            className="w-full border border-gray-300 rounded-lg shadow-sm py-2 px-3 focus:ring-black focus:border-black text-sm font-medium  text-center"
                                            >
                                            <option value="">년도</option>
                                            {Array.from({ length: 51 }, (_, i) => {
                                                const yearOption = 2000 + i; // 2000 ~ 2050
                                                return (
                                                <option key={yearOption} value={yearOption.toString()}>
                                                    {yearOption}년
                                                </option>
                                                );
                                            })}
                                        </select>
                                        {/* 월 선택 */}
                                        <select
                                            name="performanceMonth"
                                            value={month}
                                            onChange={(e) => {
                                                const newMonth = e.target.value;
                                                const year = formData.performanceDate ? formData.performanceDate.split('-')[0] || new Date().getFullYear().toString() : new Date().getFullYear().toString();
                                                const day = formData.performanceDate ? formData.performanceDate.split('-')[2] || '01' : '01';
                                                setFormData(prev => ({
                                                    ...prev,
                                                    performanceDate: newMonth ? `${year}-${newMonth.padStart(2, '0')}-${day}` : ''
                                                }));
                                                setMonth(newMonth);
                                            }}
                                            className="w-full border border-gray-300 rounded-lg shadow-sm py-2 px-3 focus:ring-black focus:border-black text-sm text-center"
                                        >
                                            <option value="">월</option>
                                            {Array.from({ length: 12 }, (_, i) => {
                                                const monthOption = i + 1;
                                                return (
                                                    <option key={monthOption} value={monthOption.toString().padStart(2, '0')}>
                                                        {monthOption}월
                                                    </option>
                                                );
                                            })}
                                        </select>
                                        {/* 일 선택 */}
                                        <select
                                            name="performanceDay"
                                            value={day}
                                            onChange={(e) => {
                                                const newDay = e.target.value;
                                                const year = formData.performanceDate ? formData.performanceDate.split('-')[0] || new Date().getFullYear().toString() : new Date().getFullYear().toString();
                                                const month = formData.performanceDate ? formData.performanceDate.split('-')[1] || '01' : '01';
                                                setFormData(prev => ({
                                                    ...prev,
                                                    performanceDate: newDay ? `${year}-${month}-${newDay.padStart(2, '0')}` : ''
                                                }));
                                                setDay(newDay);
                                            }}
                                            className="w-full border border-gray-300 rounded-lg shadow-sm py-2 px-3 focus:ring-black focus:border-black text-sm text-center"
                                        >
                                            <option value="">일</option>
                                            {Array.from({ length: 31 }, (_, i) => {
                                                const dayOption = i + 1;
                                                return (
                                                    <option key={dayOption} value={dayOption.toString().padStart(2, '0')}>
                                                        {dayOption}일
                                                    </option>
                                                );
                                            })}
                                        </select>
                                    </div>
                                </div>
                            </div>
                            
                            {/* 시간 선택 */}
                            <div>
                                <label className="block text-xs font-medium text-gray-600 mb-1">
                                    시간
                                </label>
                                
                                <div className="space-y-2">
                                    {/* 시간 선택기 */}
                                    <div className="flex items-center space-x-2">
                                        {/* 시간 선택 */}
                                        <select
                                            name="performanceHours"
                                            value={hours}
                                            onChange={(e) => {
                                                const newHours = e.target.value;
                                                const currentTime = formData.performanceTime || '';
                                                const [, currentMinutes = '00'] = currentTime.split(':');
                                                setFormData(prev => ({
                                                    ...prev,
                                                    performanceTime: newHours ? `${newHours.padStart(2, '0')}:${currentMinutes}` : ''
                                                }));
                                                setHours(newHours);
                                            }}
                                            className="w-full border border-gray-300 rounded-lg shadow-sm py-2 px-3 focus:ring-black focus:border-black text-sm text-center"
                                        >
                                            <option value="">시간</option>
                                            {Array.from({ length: 24 }, (_, i) => (
                                                <option key={i} value={i.toString().padStart(2, '0')}>
                                                    {i.toString().padStart(2, '0')}시
                                                </option>
                                            ))}
                                        </select>
                                        {/* 분 선택 */}
                                        <select
                                            name="performanceMinutes"
                                            value={minutes}
                                            onChange={(e) => {
                                                const newMinutes = e.target.value;
                                                const currentTime = formData.performanceTime || '';
                                                const [currentHours = '00'] = currentTime.split(':');
                                                setFormData(prev => ({
                                                    ...prev,
                                                    performanceTime: newMinutes ? `${currentHours}:${newMinutes.padStart(2, '0')}` : ''
                                                }));
                                                setMinutes(newMinutes);
                                            }}
                                            className="w-full border border-gray-300 rounded-lg shadow-sm py-2 px-3 focus:ring-black focus:border-black text-sm text-center"
                                        >
                                            <option value="">분</option>
                                            {Array.from({ length: 60 }, (_, i) => (
                                                <option key={i} value={i.toString().padStart(2, '0')}>
                                                    {i.toString().padStart(2, '0')}분
                                                </option>
                                            ))}
                                        </select>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                
                <div className="space-y-3 mt-6">
                    {/* 삭제 버튼 */}
                    <button
                        type="button"
                        onClick={handleDelete}
                        disabled={isUploading}
                        className="w-full bg-red-600 text-white font-bold py-2 px-4 rounded-lg hover:bg-red-700 transition-all duration-200 disabled:opacity-50"
                    >
                        라인업 삭제
                    </button>
                    
                    {/* 취소/수정 버튼 */}
                    <div className="flex space-x-3">
                        <button
                            type="button"
                            onClick={handleClose}
                            disabled={isUploading}
                            className="flex-1 bg-gray-300 text-gray-700 font-bold py-2 px-4 rounded-lg hover:bg-gray-400 transition-all duration-200 disabled:opacity-50"
                        >
                            취소
                        </button>
                        <button
                            type="submit"
                            disabled={isUploading}
                            className="flex-1 bg-black text-white font-bold py-2 px-4 rounded-lg hover:bg-gray-800 transition-all duration-200 disabled:opacity-50 flex items-center justify-center"
                        >
                            {isUploading ? (
                                <>
                                    <svg className="animate-spin -ml-1 mr-2 h-4 w-4 text-white" fill="none" viewBox="0 0 24 24">
                                        <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                                        <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                                    </svg>
                                    수정 중...
                                </>
                            ) : (
                                '수정'
                            )}
                        </button>
                    </div>
                </div>
            </form>
        </Modal>
    );
};

export default LineupEditModal;

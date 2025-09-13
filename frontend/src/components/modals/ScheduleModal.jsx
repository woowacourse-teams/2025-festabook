import React, { useState, useEffect } from 'react';
import Modal from '../common/Modal';

const ScheduleModal = ({ event, onSave, onClose, availableDates, activeDate, showToast }) => {
    const [form, setForm] = useState({ 
        title: '', 
        startTime: '', 
        endTime: '', 
        location: '',
        date: activeDate || ''
    });
    
    // 시작 시간 개별 필드 관리
    const [startHours, setStartHoursState] = useState('');
    const [startMinutes, setStartMinutesState] = useState('');
    
    // 종료 시간 개별 필드 관리
    const [endHours, setEndHoursState] = useState('');
    const [endMinutes, setEndMinutesState] = useState('');

    // 시작 시간 개별 필드 업데이트 함수들
    const setStartHours = (newHours) => {
        setStartHoursState(newHours);
        setForm(prev => {
            const currentTime = prev.startTime || '';
            const [, currentMinutes = '00'] = currentTime.split(':');
            return {
                ...prev,
                startTime: `${newHours.padStart(2, '0')}:${currentMinutes}`
            };
        });
    };

    const setStartMinutes = (newMinutes) => {
        setStartMinutesState(newMinutes);
        setForm(prev => {
            const currentTime = prev.startTime || '';
            const [currentHours = '00'] = currentTime.split(':');
            return {
                ...prev,
                startTime: `${currentHours}:${newMinutes.padStart(2, '0')}`
            };
        });
    };

    // 종료 시간 개별 필드 업데이트 함수들
    const setEndHours = (newHours) => {
        setEndHoursState(newHours);
        setForm(prev => {
            const currentTime = prev.endTime || '';
            const [, currentMinutes = '00'] = currentTime.split(':');
            return {
                ...prev,
                endTime: `${newHours.padStart(2, '0')}:${currentMinutes}`
            };
        });
    };

    const setEndMinutes = (newMinutes) => {
        setEndMinutesState(newMinutes);
        setForm(prev => {
            const currentTime = prev.endTime || '';
            const [currentHours = '00'] = currentTime.split(':');
            return {
                ...prev,
                endTime: `${currentHours}:${newMinutes.padStart(2, '0')}`
            };
        });
    };
    
    useEffect(() => { 
        if (event) {
            const startTime = event.startTime || '';
            const endTime = event.endTime || '';
            const [startHoursPart = '', startMinutesPart = ''] = startTime.split(':');
            const [endHoursPart = '', endMinutesPart = ''] = endTime.split(':');
            
            setForm({ 
                eventId: event.eventId, // 수정 시 ID 포함
                title: event.title || '', 
                startTime: startTime, 
                endTime: endTime, 
                location: event.location || '',
                date: activeDate || ''
            });
            
            // 개별 시간 상태도 업데이트
            setStartHoursState(startHoursPart);
            setStartMinutesState(startMinutesPart);
            setEndHoursState(endHoursPart);
            setEndMinutesState(endMinutesPart);
        } else {
            setForm({ 
                title: '', 
                startTime: '', 
                endTime: '', 
                location: '',
                date: activeDate || ''
            });
            
            // 개별 시간 상태 초기화
            setStartHoursState('');
            setStartMinutesState('');
            setEndHoursState('');
            setEndMinutesState('');
        }
    }, [event, activeDate]);

    // ESC 키 이벤트 리스너
    useEffect(() => {
        const handleEscKey = (event) => {
            if (event.key === 'Escape') {
                onClose();
            }
        };

        document.addEventListener('keydown', handleEscKey);

        return () => {
            document.removeEventListener('keydown', handleEscKey);
        };
    }, [onClose]);

    const handleChange = e => {
        const { name, value } = e.target;
        
        // 글자 수 제한 체크
        if (name === 'title' && value.length > 100) {
            showToast('일정 제목은 100자 이내로 입력해주세요.');
            return;
        }
        if (name === 'location' && value.length > 100) {
            showToast('장소는 100자 이내로 입력해주세요.');
            return;
        }
        
        setForm(prev => ({ ...prev, [name]: value }));
    };
    
    const handleSave = () => { 
        // 새 이벤트 추가 시에만 날짜 검증
        if (!event && !form.date) {
            alert('날짜를 선택해주세요.');
            return;
        }
        onSave(form, onClose); 
    };

    const handleKeyDown = (e) => {
        if (e.key === 'Enter') {
            e.preventDefault();
            handleSave();
        }
    };

    // 모달 전체에서 엔터 키 감지
    useEffect(() => {
        const handleGlobalKeyDown = (event) => {
            if (event.key === 'Enter') {
                event.preventDefault();
                handleSave();
            }
        };

        document.addEventListener('keydown', handleGlobalKeyDown);

        return () => {
            document.removeEventListener('keydown', handleGlobalKeyDown);
        };
    }, [form]); // form이 변경될 때마다 이벤트 리스너 재등록

    return (
        <Modal isOpen={true} onClose={onClose} maxWidth="max-w-md">
            <h3 className="text-xl font-bold mb-6">{event ? '이벤트 수정' : '새 이벤트'}</h3>
            <div className="space-y-4">
                <div>
                    <div className="flex justify-between items-center mb-1">
                        <label className="block text-sm font-medium text-gray-700">제목</label>
                        <span className="text-xs text-gray-500">
                            {form.title.length}/100
                        </span>
                    </div>
                    <input 
                        name="title" 
                        type="text" 
                        value={form.title} 
                        onChange={handleChange} 
                        onKeyDown={handleKeyDown}
                        placeholder="제목을 입력하세요 (100자 이내)" 
                        className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500" 
                    />
                </div>
                {!event && (
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">날짜</label>
                        <select 
                            name="date" 
                            value={form.date} 
                            onChange={handleChange}
                            className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500"
                        >
                            <option value="">날짜를 선택하세요</option>
                            {availableDates && availableDates.map(date => (
                                <option key={date} value={date}>
                                    {new Date(date).toLocaleDateString('ko-KR', { 
                                        month: 'long', 
                                        day: 'numeric',
                                        weekday: 'long'
                                    })}
                                </option>
                            ))}
                        </select>
                    </div>
                )}
                <div className="grid grid-cols-2 gap-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">시작 시간</label>
                        
                        <div className="space-y-2">
                            <div className="flex items-center space-x-2">
                                {/* 시간 선택 */}
                                <select
                                    name="startHours"
                                    value={startHours}
                                    onChange={(e) => setStartHours(e.target.value)}
                                    className="w-full border border-gray-300 rounded-lg shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500 text-sm text-center"
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
                                    name="startMinutes"
                                    value={startMinutes}
                                    onChange={(e) => setStartMinutes(e.target.value)}
                                    className="w-full border border-gray-300 rounded-lg shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500 text-sm text-center"
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
                    <div className="end-time-section">
                        <label className="block text-sm font-medium text-gray-700 mb-2">종료 시간</label>
                        
                        <div className="space-y-2">
                            <div className="flex items-center space-x-2">
                                {/* 시간 선택 */}
                                <select
                                    name="endHours"
                                    value={endHours}
                                    onChange={(e) => setEndHours(e.target.value)}
                                    className="w-full border border-gray-300 rounded-lg shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500 text-sm text-center"
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
                                    name="endMinutes"
                                    value={endMinutes}
                                    onChange={(e) => setEndMinutes(e.target.value)}
                                    className="w-full border border-gray-300 rounded-lg shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500 text-sm text-center"
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
                <div>
                    <div className="flex justify-between items-center mb-1">
                        <label className="block text-sm font-medium text-gray-700">장소</label>
                        <span className="text-xs text-gray-500">
                            {form.location.length}/100
                        </span>
                    </div>
                    <input 
                        name="location" 
                        type="text" 
                        value={form.location} 
                        onChange={handleChange} 
                        onKeyDown={handleKeyDown}
                        placeholder="장소를 입력하세요 (100자 이내)"
                        className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500" 
                    />
                </div>
            </div>
            <div className="mt-6 flex w-full space-x-3">
                <button
                    onClick={onClose}
                    className="flex-1 bg-gray-300 text-gray-700 font-bold py-2 px-4 rounded-lg hover:bg-gray-400 transition-all duration-200"
                >
                    취소
                </button>
                <button
                    onClick={handleSave}
                    className="flex-1 bg-black text-white font-bold py-2 px-4 rounded-lg hover:bg-gray-800 transition-all duration-200"
                >
                    저장
                </button>
            </div>
        </Modal>
    );
};

export default ScheduleModal;

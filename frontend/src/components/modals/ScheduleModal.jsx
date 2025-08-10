import React, { useState, useEffect } from 'react';
import Modal from '../common/Modal';

const ScheduleModal = ({ event, onSave, onClose, availableDates, activeDate }) => {
    const [form, setForm] = useState({ 
        title: '', 
        startTime: '', 
        endTime: '', 
        location: '',
        date: activeDate || ''
    });
    
    useEffect(() => { 
        if (event) {
            setForm({ 
                eventId: event.eventId, // 수정 시 ID 포함
                title: event.title || '', 
                startTime: event.startTime || '', 
                endTime: event.endTime || '', 
                location: event.location || '',
                date: activeDate || ''
            }); 
        } else {
            setForm({ 
                title: '', 
                startTime: '', 
                endTime: '', 
                location: '',
                date: activeDate || ''
            });
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

    const handleChange = e => setForm(prev => ({ ...prev, [e.target.name]: e.target.value }));
    
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
        <Modal isOpen={true} onClose={onClose}>
            <h3 className="text-xl font-bold mb-6">{event ? '이벤트 수정' : '새 이벤트'}</h3>
            <div className="space-y-4">
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">이벤트명</label>
                    <input 
                        name="title" 
                        type="text" 
                        value={form.title} 
                        onChange={handleChange} 
                        onKeyDown={handleKeyDown}
                        placeholder="예: 동아리 버스킹 공연" 
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
                        <label className="block text-sm font-medium text-gray-700 mb-1">시작 시간</label>
                        <input 
                            name="startTime" 
                            type="time" 
                            value={form.startTime} 
                            onChange={handleChange} 
                            onKeyDown={handleKeyDown}
                            className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500" 
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">종료 시간</label>
                        <input 
                            name="endTime" 
                            type="time" 
                            value={form.endTime} 
                            onChange={handleChange} 
                            onKeyDown={handleKeyDown}
                            className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500" 
                        />
                    </div>
                </div>
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">장소</label>
                    <input 
                        name="location" 
                        type="text" 
                        value={form.location} 
                        onChange={handleChange} 
                        onKeyDown={handleKeyDown}
                        placeholder="예: 학생회관 앞" 
                        className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500" 
                    />
                </div>
            </div>
            <div className="mt-6 flex justify-between w-full">
                <div className="space-x-3">
                    <button onClick={onClose} className="bg-gray-200 hover:bg-gray-300 text-gray-800 font-bold py-2 px-4 rounded-lg">취소</button>
                    <button onClick={handleSave} className="bg-gray-800 hover:bg-gray-900 text-white font-bold py-2 px-4 rounded-lg">저장</button>
                </div>
            </div>
        </Modal>
    );
};

export default ScheduleModal;

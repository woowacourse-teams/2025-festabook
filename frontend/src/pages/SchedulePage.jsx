import React, { useState } from 'react';
import { useData } from '../hooks/useData';
import { useModal } from '../hooks/useModal';
import { getCurrentDate } from '../utils/date';

const SchedulePage = () => {
    const { schedule, addScheduleDate, addScheduleEvent, updateScheduleEvent, deleteScheduleEvent, deleteScheduleDate } = useData();
    const { openModal, showToast } = useModal();
    const [activeDate, setActiveDate] = useState(Object.keys(schedule).sort()[0] || null);
    const dayNames = ['일', '월', '화', '수', '목', '금', '토'];

    const handleSave = (eventData) => {
        if (!eventData.title || !eventData.startTime || !eventData.endTime || !eventData.location) { showToast('제목, 시간, 장소는 필수 항목입니다.'); return; }
        if (eventData.id) {
            updateScheduleEvent(activeDate, eventData.id, eventData);
            showToast('이벤트가 수정되었습니다.');
        } else {
            addScheduleEvent(activeDate, eventData);
            showToast('새 이벤트가 추가되었습니다.');
        }
    };

    const handleDelete = (event) => {
        openModal('confirm', {
            title: '이벤트 삭제 확인',
            message: `'${event.title}' 이벤트를 정말 삭제하시겠습니까?`,
            onConfirm: () => {
                deleteScheduleEvent(activeDate, event.id);
                showToast('이벤트가 삭제되었습니다.');
            }
        });
    };

    const handleAddDate = (date) => {
        if (date && !schedule[date]) {
            addScheduleDate(date);
            setActiveDate(date);
            showToast('새로운 날짜가 추가되었습니다.');
        } else if (schedule[date]) {
            showToast('이미 존재하는 날짜입니다.');
        } else {
            showToast('유효하지 않은 날짜입니다.');
        }
    };

    const handleDeleteDate = (date) => {
        openModal('confirm', {
            title: '날짜 삭제 확인',
            message: `'${date}' 날짜의 모든 이벤트가 삭제됩니다. 정말 삭제하시겠습니까?`,
            onConfirm: () => {
                deleteScheduleDate(date);
                showToast('날짜가 삭제되었습니다.');
                // 삭제 후 다른 날짜로 activeDate 이동
                const dates = Object.keys(schedule).sort().filter(d => d !== date);
                setActiveDate(dates[0] || null);
            }
        });
    };

    const getNextDefaultDate = () => {
        const sortedDates = Object.keys(schedule).sort();
        if (sortedDates.length === 0) {
            return getCurrentDate();
        }
        const lastDateStr = sortedDates[sortedDates.length - 1];
        const lastDate = new Date(lastDateStr);
        lastDate.setDate(lastDate.getDate() + 1);
        const year = lastDate.getFullYear();
        const month = String(lastDate.getMonth() + 1).padStart(2, '0');
        const day = String(lastDate.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    };

    return (
        <div>
            <div className="flex justify-between items-center mb-6"><h2 className="text-3xl font-bold">일정 관리</h2><button onClick={() => openModal('schedule', { onSave: handleSave, activeDate })} className="bg-gray-800 hover:bg-gray-900 text-white font-bold py-2 px-4 rounded-lg flex items-center font-bold" disabled={!activeDate}><i className="fas fa-plus mr-2"></i> 새 이벤트 추가</button></div>
            <div className="flex items-center space-x-2 border-b border-gray-200 mb-6 overflow-x-auto flex-wrap">
                {Object.keys(schedule).sort().map(dateStr => {
                    const dateObj = new Date(dateStr);
                    const formattedDate = `${dateObj.getMonth() + 1}/${dateObj.getDate()}(${dayNames[dateObj.getDay()]})`;
                    return (
                        <div key={dateStr} className="relative group min-w-[110px] max-w-xs flex-shrink-0">
                            <button
                                onClick={() => setActiveDate(dateStr)}
                                className={`w-full px-4 py-2 border-b-2 transition-colors duration-200 text-center relative ${dateStr === activeDate ? 'border-gray-800 text-gray-800 font-semibold' : 'border-transparent text-gray-500 hover:border-gray-300'}`}
                                style={{ paddingRight: '1.2rem' }}
                            >
                                <span className="block">{formattedDate}</span>
                                <span
                                    className="absolute right-1 top-1/2 -translate-y-1/2 opacity-100 text-red-400 hover:text-red-600 text-xs font-bold"
                                    onClick={e => { e.stopPropagation(); handleDeleteDate(dateStr); }}
                                    title="날짜 삭제"
                                >
                                    X
                                </span>
                            </button>
                        </div>
                    );
                })}
                <button onClick={() => openModal('datePrompt', { onSave: handleAddDate, defaultDate: getNextDefaultDate() })} title="새 날짜 추가" className="px-2 py-1 rounded-full text-gray-500 hover:bg-gray-200 font-bold"><i className="fas fa-plus"></i></button>
            </div>
            <div className="relative overflow-x-auto pl-2">
                <div className="absolute left-6 top-0 bottom-0 w-0.5 bg-gray-200"></div>
                <div className="flex flex-col">
                {(schedule[activeDate] || []).sort((a,b) => a.startTime.localeCompare(b.startTime)).map(event => (
                    <div key={event.id} className="relative flex items-center mb-8 w-full">
                        <div className="z-10 shrink-0">
                            <div className={`w-8 h-8 rounded-full border-4 border-gray-100 timeline-dot timeline-dot-${event.status}`}></div>
                        </div>
                        <div className="ml-4 flex-1">
                            <div className="flex justify-between items-center">
                                <div>
                                    <p className="font-bold text-indigo-600">{event.startTime} - {event.endTime}</p>
                                    <h4 className="text-xl font-bold mt-1">{event.title}</h4>
                                     <p className="text-gray-500 mt-1"><i className="fas fa-map-marker-alt mr-2"></i>{event.location}</p>
                                </div>
                                <div className="space-x-3 shrink-0 flex items-center">
                                    <button onClick={() => openModal('schedule', { event, onSave: handleSave, activeDate })} className="text-blue-600 hover:text-blue-800 font-bold">수정</button>
                                    <button onClick={() => handleDelete(event)} className="text-red-600 hover:text-red-800 font-bold">삭제</button>
                                </div>
                            </div>
                        </div>
                    </div>
                ))}
                </div>
                 {(!activeDate || schedule[activeDate]?.length === 0) && (
                    <div className="text-center py-10 text-gray-500">
                        날짜를 선택하거나 새 날짜를 추가하여 이벤트를 관리하세요.
                    </div>
                )}
            </div>
        </div>
    );
};

export default SchedulePage;

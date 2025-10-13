import React, { useState, useEffect } from 'react';
import { useData } from '../hooks/useData';
import { useModal } from '../hooks/useModal';
import { getCurrentDate } from '../utils/date';

const EventPage = () => {
    const { schedule, addScheduleDate, addScheduleEvent, updateScheduleEvent, deleteScheduleEvent, deleteScheduleDate, isLoadingDates, isLoadingEvents, loadEventsForDate } = useData();
    const { openModal, showToast } = useModal();
    const [activeDate, setActiveDate] = useState(Object.keys(schedule).sort()[0] || null);
    const dayNames = ['일', '월', '화', '수', '목', '금', '토'];

    // activeDate 변경 시 해당 날짜의 이벤트 로드
    useEffect(() => {
        if (activeDate && schedule[activeDate] === undefined && !isLoadingEvents) {
            loadEventsForDate(activeDate, showToast);
        }
    }, [activeDate, loadEventsForDate, showToast, isLoadingEvents]);

    const handleSave = async (eventData, onClose) => {
        // 필수 필드 검증
        if (!eventData.title || !eventData.startTime || !eventData.endTime || !eventData.location) { 
            showToast('제목, 시간, 장소는 필수 항목입니다.'); 
            return; // 모달을 닫지 않음
        }
        
        // 새 이벤트 추가 시 날짜 검증
        if (!eventData.eventId && !eventData.date) {
            showToast('날짜를 선택해주세요.');
            return;
        }
        
        try {
            if (eventData.eventId) {
                // 기존 이벤트 수정
                await updateScheduleEvent(activeDate, eventData.eventId, eventData, showToast);
            } else {
                // 새 이벤트 추가 - 선택된 날짜에 추가
                await addScheduleEvent(eventData.date, eventData, showToast);
            }
            onClose(); // 성공시에만 모달 닫기
        } catch (error) {
            // 에러는 이미 DataProvider에서 toast로 표시됨
            console.error('Event save failed:', error);
        }
    };

    const handleDelete = (event) => {
        openModal('confirm', {
            title: '이벤트 삭제 확인',
            message: `'${event.title}' 이벤트를 정말 삭제하시겠습니까?`,
            onConfirm: async () => {
                await deleteScheduleEvent(activeDate, event.eventId, showToast);
            }
        });
    };

    const handleAddDate = async (date) => {
        if (date && !schedule[date]) {
            try {
                await addScheduleDate(date, showToast);
                setActiveDate(date);
            } catch (error) {
                // API 실패 시에도 UI는 유지
                throw error; // 에러를 다시 던져서 DatePromptModal에서 처리
            }
        } else if (schedule[date]) {
            showToast('이미 존재하는 날짜입니다.');
            throw new Error('이미 존재하는 날짜입니다.');
        } else {
            showToast('유효하지 않은 날짜입니다.');
            throw new Error('유효하지 않은 날짜입니다.');
        }
    };

    const handleDeleteDate = (date) => {
        openModal('confirm', {
          title: '날짜 삭제 확인',
          message: (
            <>
              '{date}' 날짜를 정말 삭제하시겠습니까?
              <br />
              <div className="font-bold text-red-500 text-xs mt-2">
                날짜의 이벤트도 모두 삭제됩니다.
              </div>
            </>
          ),
          onConfirm: async () => {
            await deleteScheduleDate(date, showToast);
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
            <div className="flex justify-between items-center mb-6">
                <h2 className="text-3xl font-bold">일정 관리</h2>
                <button 
                    onClick={() => openModal('schedule', { 
                        onSave: handleSave, 
                        activeDate,
                        availableDates: Object.keys(schedule).sort()
                    })} 
                    className="bg-gray-800 hover:bg-gray-900 text-white font-bold py-2 px-4 rounded-lg flex items-center font-bold disabled:opacity-50 disabled:cursor-not-allowed" 
                    disabled={Object.keys(schedule).length === 0 || isLoadingDates || isLoadingEvents}
                >
                    <i className="fas fa-plus mr-2"></i> 새 이벤트 추가
                </button>
            </div>
            <div className="flex items-center space-x-2 border-b border-gray-200 mb-6 overflow-x-auto flex-wrap">
                {isLoadingDates && (
                    <div className="flex items-center px-4 py-2 text-gray-500">
                        <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-gray-900 mr-2"></div>
                        로딩 중...
                    </div>
                )}
                {!isLoadingDates && Object.keys(schedule).length > 0 && Object.keys(schedule).sort().map(dateStr => {
                    const dateObj = new Date(dateStr);
                    const formattedDate = `${dateObj.getMonth() + 1}/${dateObj.getDate()}(${dayNames[dateObj.getDay()]})`;
                    return (
                        <div key={dateStr} className="relative group min-w-[110px] max-w-xs flex-shrink-0">
                            <button
                                onClick={() => setActiveDate(dateStr)}
                                className={`w-full px-4 py-2 border-b-2 transition-colors duration-200 text-center relative ${dateStr === activeDate ? 'border-gray-800 text-gray-800 font-semibold' : 'border-transparent text-gray-500 hover:border-gray-300'}`}
                                style={{ paddingRight: '1.2rem' }}
                                disabled={isLoadingDates}
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
                <button 
                    onClick={() => openModal('datePrompt', { onSave: handleAddDate, defaultDate: getNextDefaultDate() })} 
                    title="새 날짜 추가" 
                    className="px-2 py-1 rounded-full text-gray-500 hover:bg-gray-200 font-bold disabled:opacity-50 disabled:cursor-not-allowed"
                    disabled={isLoadingDates}
                >
                    <i className="fas fa-plus"></i>
                </button>
            </div>
            <div className="relative overflow-x-auto pl-2">
                <div className="absolute left-6 top-0 bottom-0 w-0.5 bg-gray-200"></div>
                
                {isLoadingEvents && (
                    <div className="flex items-center justify-center py-10">
                        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-gray-900 mr-3"></div>
                        <span className="text-gray-500">일정 로딩 중...</span>
                    </div>
                )}
                
                {!isLoadingEvents && (
                    <div className="flex flex-col">
                    {/* schedule[activeDate]가 undefined이면 아직 로드되지 않은 상태 */}
                    {schedule[activeDate] && schedule[activeDate].sort((a,b) => a.startTime.localeCompare(b.startTime)).map(event => (
                        <div key={event.eventId} className="relative flex items-center mb-8 w-full">
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
                                        <button 
                                            onClick={() => openModal('schedule', { event, onSave: handleSave, activeDate })} 
                                            className="text-blue-600 hover:text-blue-800 font-bold disabled:opacity-50 disabled:cursor-not-allowed"
                                            disabled={isLoadingEvents}
                                        >
                                            수정
                                        </button>
                                        <button 
                                            onClick={() => handleDelete(event)} 
                                            className="text-red-600 hover:text-red-800 font-bold disabled:opacity-50 disabled:cursor-not-allowed"
                                            disabled={isLoadingEvents}
                                        >
                                            삭제
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    ))}
                    </div>
                )}
                
                {/* 빈 상태 UI - 로딩이 끝나고 데이터가 없을 때 또는 날짜가 없을 때 */}
                {(!isLoadingEvents && (!activeDate || (schedule[activeDate] !== undefined && (!schedule[activeDate] || schedule[activeDate].length === 0)))) && (
                    <div className="text-center py-12">
                        {!activeDate ? (
                            <>
                                <i className="fas fa-calendar-plus text-4xl text-gray-400 mb-4"></i>
                                <p className="text-gray-500 mb-4">날짜를 선택하거나 새 날짜를 추가하여 이벤트를 관리하세요.</p>
                                <button
                                    onClick={() => openModal('datePrompt', { onSave: handleAddDate, defaultDate: getNextDefaultDate() })}
                                    className="bg-gray-800 hover:bg-gray-900 text-white px-4 py-2 rounded-lg transition-colors"
                                >
                                    첫 번째 날짜 추가
                                </button>
                            </>
                        ) : (
                            <>
                                <i className="fas fa-calendar-day text-4xl text-gray-400 mb-4"></i>
                                <p className="text-gray-500 mb-4">등록된 이벤트가 없습니다</p>
                                <button
                                    onClick={() => openModal('schedule', { onSave: handleSave, activeDate, availableDates: Object.keys(schedule) })}
                                    className="bg-gray-800 hover:bg-gray-900 text-white px-4 py-2 rounded-lg transition-colors"
                                >
                                    첫 번째 이벤트 추가
                                </button>
                            </>
                        )}
                    </div>
                )}
            </div>
        </div>
    );
};

export default EventPage;

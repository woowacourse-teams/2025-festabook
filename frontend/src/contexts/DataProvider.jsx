import React, { useState, useEffect } from 'react';
import { DataContext } from './DataContext';
import { initialData } from '../data/initialData';
import { getCurrentDate } from '../utils/date';
import { scheduleAPI } from '../utils/api';

export const DataProvider = ({ children }) => {
    const [data, setData] = useState(initialData);
    const [eventDates, setEventDates] = useState([]); // 서버에서 가져온 축제 날짜들
    const [isLoadingDates, setIsLoadingDates] = useState(false);
    const [isLoadingEvents, setIsLoadingEvents] = useState(false);
    
    const fetchEventDates = async () => {
        try {
            setIsLoadingDates(true);
            const dates = await scheduleAPI.getEventDates();
            setEventDates(dates);
            
            // 서버 데이터를 기반으로 schedule 객체 재구성 (이벤트는 로드하지 않음)
            const scheduleFromServer = {};
            dates.forEach(dateObj => {
                // undefined로 설정하여 실제 로드가 필요함을 표시
                scheduleFromServer[dateObj.date] = undefined;
            });
            
            setData(prev => ({
                ...prev,
                schedule: scheduleFromServer
            }));
        } catch (error) {
            console.error('Failed to fetch event dates:', error);
            // 초기 로드 실패 시 빈 배열로 설정
            setEventDates([]);
            setData(prev => ({
                ...prev,
                schedule: {}
            }));
        } finally {
            setIsLoadingDates(false);
        }
    };
    
    // 컴포넌트 마운트 시 축제 날짜 목록 조회
    useEffect(() => {
        fetchEventDates();
    }, []);

    const getNextId = (arr) => (arr.length > 0 ? Math.max(...arr.map(item => item.id)) + 1 : 1);

    const dataActions = {
        // 기존 액션들
        addNotice: (notice) => setData(d => ({ ...d, notices: [{ id: getNextId(d.notices), ...notice, date: getCurrentDate(), pinned: false }, ...d.notices] })),
        updateNotice: (id, updated) => setData(d => ({ ...d, notices: d.notices.map(n => n.id === id ? { ...n, ...updated } : n) })),
        deleteNotice: (id) => setData(d => ({ ...d, notices: d.notices.filter(n => n.id !== id) })),
        togglePinNotice: (id, showToast) => {
            const notice = data.notices.find(n => n.id === id);
            const pinnedCount = data.notices.filter(n => n.pinned).length;
            if (!notice.pinned && pinnedCount >= 3) { showToast('고정은 최대 3개만 가능합니다.'); return; }
            setData(d => ({ ...d, notices: d.notices.map(n => n.id === id ? { ...n, pinned: !n.pinned } : n)}));
            showToast(notice.pinned ? '공지사항 고정이 해제되었습니다.' : '공지사항이 고정되었습니다.');
        },
        addLostItem: (item) => setData(d => ({ ...d, lostItems: [{ id: getNextId(d.lostItems), ...item, status: '보관중' }, ...d.lostItems] })),
        updateLostItem: (id, updated) => setData(d => ({ ...d, lostItems: d.lostItems.map(i => i.id === id ? { ...i, ...updated } : i) })),
        deleteLostItem: (id) => setData(d => ({ ...d, lostItems: d.lostItems.filter(i => i.id !== id) })),
        toggleLostItemStatus: (id, showToast) => {
            let newStatus = '';
            setData(d => ({ ...d, lostItems: d.lostItems.map(i => {
                if (i.id === id) { newStatus = i.status === '보관중' ? '인계완료' : '보관중'; return { ...i, status: newStatus }; }
                return i;
            })}));
            showToast(`상태가 [${newStatus}]으로 변경되었습니다.`);
        },
        setQnaItems: (newItems) => setData(d => ({ ...d, qnaItems: newItems })),
        addQnaItem: (item) => setData(d => ({ ...d, qnaItems: [...d.qnaItems, { id: getNextId(d.qnaItems), ...item }] })),
        updateQnaItem: (id, updated) => setData(d => ({...d, qnaItems: d.qnaItems.map(q => q.id === id ? {...q, ...updated} : q)})),
        deleteQnaItem: (id) => setData(d => ({ ...d, qnaItems: d.qnaItems.filter(q => q.id !== id) })),
        
        // 서버 연동 축제 날짜 관리 액션들
        addScheduleDate: async (date, showToast) => {
            try {
                setIsLoadingDates(true);
                const updatedDates = await scheduleAPI.addEventDate(date);
                setEventDates(updatedDates);
                
                // 새로 추가된 날짜를 schedule에 반영
                const scheduleFromServer = {};
                updatedDates.forEach(dateObj => {
                    if (data.schedule[dateObj.date] !== undefined) {
                        // 기존에 로드된 데이터가 있으면 유지
                        scheduleFromServer[dateObj.date] = data.schedule[dateObj.date];
                    } else {
                        // 새로운 날짜는 undefined로 설정
                        scheduleFromServer[dateObj.date] = undefined;
                    }
                });
                
                setData(prev => ({
                    ...prev,
                    schedule: scheduleFromServer
                }));
                
                showToast('새로운 날짜가 추가되었습니다.');
            } catch (error) {
                console.error('Failed to add event date:', error);
                showToast(error.message || '날짜 추가에 실패했습니다.');
            } finally {
                setIsLoadingDates(false);
            }
        },
        
        deleteScheduleDate: async (date, showToast) => {
            try {
                setIsLoadingDates(true);
                // 날짜에 해당하는 eventDateId 찾기
                const eventDateObj = eventDates.find(ed => ed.date === date);
                if (!eventDateObj) {
                    showToast('삭제할 날짜를 찾을 수 없습니다.');
                    return;
                }
                
                const updatedDates = await scheduleAPI.deleteEventDate(eventDateObj.id);
                setEventDates(updatedDates);
                
                // 삭제된 날짜를 schedule에서 제거
                const scheduleFromServer = {};
                updatedDates.forEach(dateObj => {
                    if (data.schedule[dateObj.date] !== undefined) {
                        // 기존에 로드된 데이터가 있으면 유지
                        scheduleFromServer[dateObj.date] = data.schedule[dateObj.date];
                    } else {
                        // 새로운 날짜는 undefined로 설정
                        scheduleFromServer[dateObj.date] = undefined;
                    }
                });
                
                setData(prev => ({
                    ...prev,
                    schedule: scheduleFromServer
                }));
                
                showToast('날짜가 삭제되었습니다.');
            } catch (error) {
                console.error('Failed to delete event date:', error);
                showToast(error.message || '날짜 삭제에 실패했습니다.');
            } finally {
                setIsLoadingDates(false);
            }
        },
        
        // 기존 이벤트 관리 액션들 (서버 연동으로 변경)
        loadEventsForDate: async (date, showToast) => {
            console.log('loadEventsForDate called for:', date); // 디버깅용
            try {
                setIsLoadingEvents(true);
                const eventDateObj = eventDates.find(ed => ed.date === date);
                console.log('Found eventDateObj:', eventDateObj); // 디버깅용
                if (!eventDateObj) return;
                
                const events = await scheduleAPI.getEventsByDateId(eventDateObj.id);
                console.log('Loaded events:', events); // 디버깅용
                
                setData(prev => ({
                    ...prev,
                    schedule: {
                        ...prev.schedule,
                        [date]: events
                    }
                }));
            } catch (error) {
                console.error('Failed to load events:', error);
                if (showToast) showToast(error.message || '일정 조회에 실패했습니다.');
            } finally {
                setIsLoadingEvents(false);
            }
        },

        addScheduleEvent: async (date, eventData, showToast) => {
            try {
                setIsLoadingEvents(true);
                const eventDateObj = eventDates.find(ed => ed.date === date);
                if (!eventDateObj) {
                    showToast('해당 날짜를 찾을 수 없습니다.');
                    return;
                }

                const eventRequest = {
                    startTime: eventData.startTime,
                    endTime: eventData.endTime,
                    title: eventData.title,
                    location: eventData.location,
                    eventDateId: eventDateObj.id
                };

                await scheduleAPI.createEvent(eventRequest);
                
                // 성공 후 해당 날짜의 일정 다시 조회
                const events = await scheduleAPI.getEventsByDateId(eventDateObj.id);
                setData(prev => ({
                    ...prev,
                    schedule: {
                        ...prev.schedule,
                        [date]: events
                    }
                }));
                
                showToast('새 이벤트가 추가되었습니다.');
            } catch (error) {
                console.error('Failed to add event:', error);
                showToast(error.message || '일정 추가에 실패했습니다.');
            } finally {
                setIsLoadingEvents(false);
            }
        },
        
        updateScheduleEvent: async (date, eventId, eventData, showToast) => {
            try {
                setIsLoadingEvents(true);
                const eventDateObj = eventDates.find(ed => ed.date === date);
                if (!eventDateObj) {
                    showToast('해당 날짜를 찾을 수 없습니다.');
                    return;
                }

                const eventRequest = {
                    startTime: eventData.startTime,
                    endTime: eventData.endTime,
                    title: eventData.title,
                    location: eventData.location,
                    eventDateId: eventDateObj.id
                };

                await scheduleAPI.updateEvent(eventId, eventRequest);
                
                // 성공 후 해당 날짜의 일정 다시 조회
                const events = await scheduleAPI.getEventsByDateId(eventDateObj.id);
                setData(prev => ({
                    ...prev,
                    schedule: {
                        ...prev.schedule,
                        [date]: events
                    }
                }));
                
                showToast('이벤트가 수정되었습니다.');
            } catch (error) {
                console.error('Failed to update event:', error);
                showToast(error.message || '일정 수정에 실패했습니다.');
            } finally {
                setIsLoadingEvents(false);
            }
        },
        
        deleteScheduleEvent: async (date, eventId, showToast) => {
            try {
                setIsLoadingEvents(true);
                const eventDateObj = eventDates.find(ed => ed.date === date);
                if (!eventDateObj) {
                    showToast('해당 날짜를 찾을 수 없습니다.');
                    return;
                }

                await scheduleAPI.deleteEvent(eventId);
                
                // 성공 후 해당 날짜의 일정 다시 조회
                const events = await scheduleAPI.getEventsByDateId(eventDateObj.id);
                setData(prev => ({
                    ...prev,
                    schedule: {
                        ...prev.schedule,
                        [date]: events
                    }
                }));
                
                showToast('이벤트가 삭제되었습니다.');
            } catch (error) {
                console.error('Failed to delete event:', error);
                showToast(error.message || '일정 삭제에 실패했습니다.');
            } finally {
                setIsLoadingEvents(false);
            }
        },
        
        addBooth: (booth) => setData(d => ({ ...d, booths: [{ id: getNextId(d.booths), ...booth}, ...d.booths] })),
        updateBooth: (id, updated) => setData(d => ({ ...d, booths: d.booths.map(b => b.id === id ? { ...b, ...updated } : b) })),
        deleteBooth: (id) => setData(d => ({ ...d, booths: d.booths.filter(b => b.id !== id) })),
        
        // 새로운 상태 제공
        fetchEventDates,
        isLoadingDates,
        isLoadingEvents,
        eventDates
    };

    return <DataContext.Provider value={{ ...data, ...dataActions }}>{children}</DataContext.Provider>;
};

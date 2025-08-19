import React, { useState, useEffect } from 'react';
import { DataContext } from './DataContext';
import { initialData } from '../data/initialData';
import { getCurrentDate } from '../utils/date';
import { scheduleAPI, qnaAPI, lostItemAPI, placeAPI } from '../utils/api';

export const DataProvider = ({ children }) => {
    const [data, setData] = useState(initialData);
    const [eventDates, setEventDates] = useState([]); // 서버에서 가져온 축제 날짜들
    const [isLoadingDates, setIsLoadingDates] = useState(false);
    const [isLoadingEvents, setIsLoadingEvents] = useState(false);
    const [isLoadingQna, setIsLoadingQna] = useState(false);
    const [isLoadingLostItems, setIsLoadingLostItems] = useState(false);
    
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
        } catch {
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

    const fetchQnaItems = async () => {
        try {
            setIsLoadingQna(true);
            const questions = await qnaAPI.getQuestions();
            setData(prev => ({
                ...prev,
                qnaItems: questions
            }));
        } catch {
            // 초기 로드 실패 시 빈 배열로 설정
            setData(prev => ({
                ...prev,
                qnaItems: []
            }));
        } finally {
            setIsLoadingQna(false);
        }
    };

    const fetchLostItems = async () => {
        try {
            setIsLoadingLostItems(true);
            const lostItems = await lostItemAPI.getLostItems();
            setData(prev => ({
                ...prev,
                lostItems: lostItems
            }));
        } catch {
            // 초기 로드 실패 시 빈 배열로 설정
            setData(prev => ({
                ...prev,
                lostItems: []
            }));
        } finally {
            setIsLoadingLostItems(false);
        }
    };
    
    // 컴포넌트 마운트 시 축제 날짜 목록, QnA, 분실물 조회
    useEffect(() => {
        fetchEventDates();
        fetchQnaItems();
        fetchLostItems();
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
        // 분실물 등록 (서버 연동) - 전체 재조회 없이 로컬 상태만 갱신
        addLostItem: async (item, showToast) => {
            try {
                const newItem = await lostItemAPI.createLostItem(item);
                setData(prev => ({
                    ...prev,
                    lostItems: [newItem, ...prev.lostItems]
                }));
                if (showToast) showToast('분실물이 등록되었습니다.');
                return newItem;
            } catch (error) {
                if (showToast) showToast(error.message || '분실물 등록에 실패했습니다.');
                throw error;
            }
        },

        // 분실물 수정 (서버 연동) - 전체 재조회 없이 해당 항목만 갱신
        updateLostItem: async (id, updated, showToast) => {
            try {
                const result = await lostItemAPI.updateLostItem(id, updated);
                setData(prev => ({
                    ...prev,
                    lostItems: prev.lostItems.map(i =>
                        i.id === id
                            ? { ...i, imageUrl: result.imageUrl, storageLocation: result.storageLocation }
                            : i
                    )
                }));
                if (showToast) showToast('분실물 정보가 수정되었습니다.');
                return result;
            } catch (error) {
                if (showToast) showToast(error.message || '분실물 수정에 실패했습니다.');
                throw error;
            }
        },

        // 분실물 삭제 (서버 연동) - 전체 재조회 없이 로컬에서 제거
        deleteLostItem: async (id, showToast) => {
            try {
                await lostItemAPI.deleteLostItem(id);
                setData(prev => ({
                    ...prev,
                    lostItems: prev.lostItems.filter(i => i.id !== id)
                }));
                if (showToast) showToast('분실물이 삭제되었습니다.');
            } catch (error) {
                if (showToast) showToast(error.message || '분실물 삭제에 실패했습니다.');
                throw error;
            }
        },

        // 분실물 상태 변경 (서버 연동) - 전체 재조회 없이 해당 항목만 갱신
        toggleLostItemStatus: async (id, showToast) => {
            try {
                const currentItem = data.lostItems.find(item => item.id === id);
                if (!currentItem) {
                    if (showToast) showToast('분실물을 찾을 수 없습니다.');
                    return;
                }
                const newStatus = currentItem.pickupStatus === 'PENDING' ? 'COMPLETED' : 'PENDING';
                const result = await lostItemAPI.updateLostItemStatus(id, newStatus);
                setData(prev => ({
                    ...prev,
                    lostItems: prev.lostItems.map(i =>
                        i.id === id ? { ...i, pickupStatus: result.pickupStatus } : i
                    )
                }));
                const statusText = newStatus === 'PENDING' ? '보관중' : '수령완료';
                if (showToast) showToast(`상태가 [${statusText}]로 변경되었습니다.`);
                return result;
            } catch (error) {
                if (showToast) showToast(error.message || '분실물 상태 변경에 실패했습니다.');
                throw error;
            }
        },
        // QnA 관련 액션들 (서버 연동)
        addQnaItem: async (item, showToast) => {
            try {
                setIsLoadingQna(true);
                await qnaAPI.createQuestion(item);
                
                // 서버에서 전체 QnA 목록을 다시 조회하여 최신 상태로 업데이트
                const questions = await qnaAPI.getQuestions();
                
                setData(prev => ({
                    ...prev,
                    qnaItems: questions
                }));
                showToast('새 QnA가 등록되었습니다.');
            } catch (error) {
                showToast(error.message || 'QnA 추가에 실패했습니다.');
            } finally {
                setIsLoadingQna(false);
            }
        },
        
        updateQnaItem: async (id, updated, showToast) => {
            try {
                setIsLoadingQna(true);
                await qnaAPI.updateQuestion(id, updated);
                
                // 서버에서 전체 QnA 목록을 다시 조회하여 최신 상태로 업데이트
                const questions = await qnaAPI.getQuestions();
                
                setData(prev => ({
                    ...prev,
                    qnaItems: questions
                }));
                showToast('QnA가 수정되었습니다.');
            } catch (error) {
                showToast(error.message || 'QnA 수정에 실패했습니다.');
            } finally {
                setIsLoadingQna(false);
            }
        },
        
        deleteQnaItem: async (questionId, showToast) => {
            try {
                setIsLoadingQna(true);
                await qnaAPI.deleteQuestion(questionId);
                setData(prev => ({
                    ...prev,
                    qnaItems: prev.qnaItems.filter(q => q.questionId !== questionId)
                }));
                showToast('QnA가 삭제되었습니다.');
            } catch (error) {
                showToast(error.message || 'QnA 삭제에 실패했습니다.');
            } finally {
                setIsLoadingQna(false);
            }
        },

        updateQnaSequences: async (sequences, showToast) => {
            try {
                setIsLoadingQna(true);
                await qnaAPI.updateQuestionSequences(sequences);
                // 서버에서 전체 QnA 목록을 다시 조회하여 최신 상태로 업데이트
                const questions = await qnaAPI.getQuestions();
                setData(prev => ({
                    ...prev,
                    qnaItems: questions
                }));
                showToast('QnA 순서가 저장되었습니다.');
            } catch (error) {
                showToast(error.message || 'QnA 순서 변경에 실패했습니다.');
            } finally {
                setIsLoadingQna(false);
            }
        },
        
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
                showToast(error.message || '날짜 추가에 실패했습니다.');
                throw error; // 에러를 다시 던져서 호출자에서 처리
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
                
                const updatedDates = await scheduleAPI.deleteEventDate(eventDateObj.eventDateId);
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
                showToast(error.message || '날짜 삭제에 실패했습니다.');
            } finally {
                setIsLoadingDates(false);
            }
        },
        
        // 기존 이벤트 관리 액션들 (서버 연동으로 변경)
                loadEventsForDate: async (date, showToast) => {
            // 이미 로딩 중이거나 이미 데이터가 있는 경우 중복 호출 방지
            if (isLoadingEvents || (data.schedule[date] !== undefined && data.schedule[date] !== null)) {
                return;
            }
            
            try {
                setIsLoadingEvents(true);
                const eventDateObj = eventDates.find(ed => ed.date === date);
                if (!eventDateObj) return;
                
                const events = await scheduleAPI.getEventsByDateId(eventDateObj.eventDateId);
                
                setData(prev => ({
                    ...prev,
                    schedule: {
                        ...prev.schedule,
                        [date]: events
                    }
                }));
            } catch (error) {
                if (showToast) showToast(error.message || '일정 조회에 실패했습니다.');
                // 실패 시 빈 배열을 설정하여 undefined 상태를 유지하지 않음
                setData(prev => ({
                    ...prev,
                    schedule: {
                        ...prev.schedule,
                        [date]: []
                    }
                }));
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
                    eventDateId: eventDateObj.eventDateId
                };

                await scheduleAPI.createEvent(eventRequest);
                
                // 성공 후 해당 날짜의 일정 다시 조회
                const events = await scheduleAPI.getEventsByDateId(eventDateObj.eventDateId);
                setData(prev => ({
                    ...prev,
                    schedule: {
                        ...prev.schedule,
                        [date]: events
                    }
                }));
                
                showToast('새 이벤트가 추가되었습니다.');
            } catch (error) {
                showToast(error.message || '일정 추가에 실패했습니다.');
                // 실패 시 기존 상태 유지 (undefined로 되돌림)
                setData(prev => ({
                    ...prev,
                    schedule: {
                        ...prev.schedule,
                        [date]: undefined
                    }
                }));
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
                    eventDateId: eventDateObj.eventDateId
                };

                await scheduleAPI.updateEvent(eventId, eventRequest);
                
                // 성공 후 해당 날짜의 일정 다시 조회
                const events = await scheduleAPI.getEventsByDateId(eventDateObj.eventDateId);
                setData(prev => ({
                    ...prev,
                    schedule: {
                        ...prev.schedule,
                        [date]: events
                    }
                }));
                
                showToast('이벤트가 수정되었습니다.');
            } catch (error) {
                showToast(error.message || '일정 수정에 실패했습니다.');
                // 실패 시 기존 상태 유지 (undefined로 되돌림)
                setData(prev => ({
                    ...prev,
                    schedule: {
                        ...prev.schedule,
                        [date]: undefined
                    }
                }));
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
                const events = await scheduleAPI.getEventsByDateId(eventDateObj.eventDateId);
                setData(prev => ({
                    ...prev,
                    schedule: {
                        ...prev.schedule,
                        [date]: events
                    }
                }));
                
                showToast('이벤트가 삭제되었습니다.');
            } catch (error) {
                showToast(error.message || '일정 삭제에 실패했습니다.');
                // 실패 시 기존 상태 유지 (undefined로 되돌림)
                setData(prev => ({
                    ...prev,
                    schedule: {
                        ...prev.schedule,
                        [date]: undefined
                    }
                }));
            } finally {
                setIsLoadingEvents(false);
            }
        },
        
        addBooth: (booth) => setData(d => ({ ...d, booths: [{ id: getNextId(d.booths), ...booth}, ...d.booths] })),
        updateBooth: (id, updated) => setData(d => ({ ...d, booths: d.booths.map(b => b.id === id ? { ...b, ...updated } : b) })),
        deleteBooth: (id) => setData(d => ({ ...d, booths: d.booths.filter(b => b.id !== id) })),
        
        // 새로운 상태 제공
        fetchEventDates,
        fetchQnaItems,
        fetchLostItems,
        isLoadingDates,
        isLoadingEvents,
        isLoadingQna,
        isLoadingLostItems,
        eventDates
    };

    return <DataContext.Provider value={{ ...data, ...dataActions }}>{children}</DataContext.Provider>;
};

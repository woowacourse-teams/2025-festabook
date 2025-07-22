import React, { useState } from 'react';
import { DataContext } from './DataContext';
import { initialData } from '../data/initialData';
import { getCurrentDate } from '../utils/date';

export const DataProvider = ({ children }) => {
    const [data, setData] = useState(initialData);
    const getNextId = (arr) => (arr.length > 0 ? Math.max(...arr.map(item => item.id)) + 1 : 1);

    const dataActions = {
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
        addScheduleDate: (date) => setData(d => ({ ...d, schedule: { ...d.schedule, [date]: [] } })),
        addScheduleEvent: (date, event) => {
            const allEvents = Object.values(data.schedule).flat();
            const newEvent = { id: getNextId(allEvents), ...event, status: '예정' };
            setData(d => ({ ...d, schedule: { ...d.schedule, [date]: [...(d.schedule[date] || []), newEvent] } }));
        },
        updateScheduleEvent: (date, id, updated) => setData(d => ({ ...d, schedule: { ...d.schedule, [date]: d.schedule[date].map(e => e.id === id ? { ...e, ...updated } : e) } })),
        deleteScheduleEvent: (date, id) => setData(d => ({ ...d, schedule: { ...d.schedule, [date]: d.schedule[date].filter(e => e.id !== id) } })),
        deleteScheduleDate: (date) => setData(d => {
            const newSchedule = { ...d.schedule };
            delete newSchedule[date];
            return { ...d, schedule: newSchedule };
        }),
        addBooth: (booth) => setData(d => ({ ...d, booths: [{ id: getNextId(d.booths), ...booth}, ...d.booths] })),
        updateBooth: (id, updated) => setData(d => ({ ...d, booths: d.booths.map(b => b.id === id ? { ...b, ...updated } : b) })),
        deleteBooth: (id) => setData(d => ({ ...d, booths: d.booths.filter(b => b.id !== id) })),
    };

    return <DataContext.Provider value={{ ...data, ...dataActions }}>{children}</DataContext.Provider>;
};

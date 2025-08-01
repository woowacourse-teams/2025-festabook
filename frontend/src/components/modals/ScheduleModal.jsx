import React, { useState, useEffect } from 'react';
import Modal from '../common/Modal';

const ScheduleModal = ({ event, onSave, onClose }) => {
    const [form, setForm] = useState({ title: '', startTime: '', endTime: '', location: '' });
    useEffect(() => { setForm(event || { title: '', startTime: '', endTime: '', location: '' }); }, [event]);
    const handleChange = e => setForm(prev => ({ ...prev, [e.target.name]: e.target.value }));
    const handleSave = () => { onSave(form, onClose); }; // onClose 콜백 전달

    return (
        <Modal isOpen={true} onClose={onClose}>
            <h3 className="text-xl font-bold mb-6">{event ? '이벤트 수정' : '새 이벤트'}</h3>
            <div className="space-y-4">
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">이벤트명</label>
                    <input name="title" type="text" value={form.title} onChange={handleChange} placeholder="예: 동아리 버스킹 공연" className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500" />
                </div>
                <div className="grid grid-cols-2 gap-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">시작 시간</label>
                        <input name="startTime" type="time" value={form.startTime} onChange={handleChange} className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500" />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">종료 시간</label>
                        <input name="endTime" type="time" value={form.endTime} onChange={handleChange} className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500" />
                    </div>
                </div>
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">장소</label>
                    <input name="location" type="text" value={form.location} onChange={handleChange} placeholder="예: 학생회관 앞" className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500" />
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

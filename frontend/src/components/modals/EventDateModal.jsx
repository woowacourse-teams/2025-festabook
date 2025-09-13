import React, { useState, useEffect } from 'react';
import Modal from '../common/Modal';
import { getCurrentDate } from '../../utils/date';

const EventDateModal = ({ onSave, onClose, defaultDate }) => {
    const [date, setDate] = useState(defaultDate || getCurrentDate());
    
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

    const handleSave = async () => {
        try {
            await onSave(date);
            onClose();
        } catch (error) {
            onClose();
        }
    };

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
    }, [date]);

    return (
        <Modal isOpen={true} onClose={onClose} maxWidth="max-w-sm">
            <h3 className="text-lg font-bold mb-4">새 날짜 추가</h3>
            <div className="space-y-2">
                <div className="grid grid-cols-3 gap-2 w-full">
                    <select
                        name="year"
                        value={date ? date.split('-')[0] : ''}
                        onChange={(e) => {
                            const newYear = e.target.value;
                            const [, m = '01', d = '01'] = date ? date.split('-') : ['', '01', '01'];
                            const newDate = `${newYear}-${m}-${d}`;
                            setDate(newDate);
                        }}
                        className="w-full border border-gray-300 rounded-lg shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500 text-sm text-center"
                    >
                        <option value="">년도</option>
                        {Array.from({ length: 51 }, (_, i) => {
                            const yearOption = 2000 + i;
                            return (
                                <option key={yearOption} value={yearOption.toString()}>
                                    {yearOption}년
                                </option>
                            );
                        })}
                    </select>
                    <select
                        name="month"
                        value={date ? date.split('-')[1] : ''}
                        onChange={(e) => {
                            const month = e.target.value;
                            const year = date ? date.split('-')[0] || new Date().getFullYear().toString() : new Date().getFullYear().toString();
                            const day = date ? date.split('-')[2] || '01' : '01';
                            const newDate = month ? `${year}-${month.padStart(2, '0')}-${day}` : '';
                            setDate(newDate);
                        }}
                        className="w-full border border-gray-300 rounded-lg shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500 text-sm text-center"
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
                    <select
                        name="day"
                        value={date ? date.split('-')[2] : ''}
                        onChange={(e) => {
                            const day = e.target.value;
                            const year = date ? date.split('-')[0] || new Date().getFullYear().toString() : new Date().getFullYear().toString();
                            const month = date ? date.split('-')[1] || '01' : '01';
                            const newDate = day ? `${year}-${month}-${day.padStart(2, '0')}` : '';
                            setDate(newDate);
                        }}
                        className="w-full border border-gray-300 rounded-lg shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500 text-sm text-center"
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
            <div className="mt-6 flex justify-end space-x-3">
                <button onClick={onClose} className="bg-gray-200 hover:bg-gray-300 text-gray-800 font-bold py-2 px-4 rounded-lg">취소</button>
                <button onClick={handleSave} className="bg-gray-800 hover:bg-gray-900 text-white font-bold py-2 px-4 rounded-lg">추가</button>
            </div>
        </Modal>
    );
};

export default EventDateModal;

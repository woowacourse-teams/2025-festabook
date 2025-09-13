import React, { useState, useEffect } from 'react';
import Modal from '../common/Modal';
import { getCurrentDate } from '../../utils/date';

const EventDateModal = ({ onSave, onClose, defaultDate }) => {
    const [date, setDate] = useState(defaultDate || getCurrentDate());
    
    // 날짜 개별 필드 관리
    const [year, setYearState] = useState('');
    const [month, setMonthState] = useState('');
    const [day, setDayState] = useState('');

    // 날짜 개별 필드 업데이트 함수들
    const setYear = (newYear) => {
        setYearState(newYear);
        const currentDate = date || '';
        const [, currentMonth = '01', currentDay = '01'] = currentDate.split('-');
        const newDate = `${newYear}-${currentMonth}-${currentDay}`;
        setDate(newDate);
    };

    const setMonth = (newMonth) => {
        setMonthState(newMonth);
        const currentDate = date || '';
        const [currentYear = new Date().getFullYear().toString()] = currentDate.split('-');
        const [, , currentDay = '01'] = currentDate.split('-');
        const newDate = `${currentYear}-${newMonth.padStart(2, '0')}-${currentDay}`;
        setDate(newDate);
    };

    const setDay = (newDay) => {
        setDayState(newDay);
        const currentDate = date || '';
        const [currentYear = new Date().getFullYear().toString(), currentMonth = '01'] = currentDate.split('-');
        const newDate = `${currentYear}-${currentMonth}-${newDay.padStart(2, '0')}`;
        setDate(newDate);
    };

    // 초기 날짜 설정 및 개별 필드 초기화
    useEffect(() => {
        const initialDate = defaultDate || getCurrentDate();
        setDate(initialDate);
        const [yearPart = '', monthPart = '', dayPart = ''] = initialDate.split('-');
        setYearState(yearPart);
        setMonthState(monthPart);
        setDayState(dayPart);
    }, [defaultDate]);

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

    const handleSave = async () => {
        try {
            await onSave(date);
            onClose();
        } catch (error) {
            onClose();
        }
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
    }, [date]); // date가 변경될 때마다 이벤트 리스너 재등록

    return (
        <Modal isOpen={true} onClose={onClose} maxWidth="max-w-md">
            <h3 className="text-lg font-bold mb-4">새 날짜 추가</h3>
            
            {/* 텍스트 입력과 선택기 조합 */}
            <div className="space-y-2">
                {/* 텍스트 입력 */}
                <div className="flex items-center space-x-2">
                    <input
                        type="text"
                        maxLength={4}
                        placeholder="YYYY"
                        className="w-full border border-gray-300 rounded-lg shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500 text-sm text-center"
                        value={year}
                        onChange={(e) => {
                            const newYear = e.target.value.replace(/[^0-9]/g, '');
                            if (newYear.length <= 4) {
                                setYear(newYear);
                            }
                        }}
                        onKeyDown={(e) => {
                            if (e.key === 'Tab' || (e.key === 'Enter' && year.length === 4)) {
                                e.preventDefault();
                                const nextInput = e.target.parentElement.querySelector('input[placeholder="MM"]');
                                nextInput?.focus();
                            }
                        }}
                    />
                    <span className="text-gray-700 font-medium">-</span>
                    <input
                        type="text"
                        maxLength={2}
                        placeholder="MM"
                        className="w-full border border-gray-300 rounded-lg shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500 text-sm text-center"
                        value={month}
                        onChange={(e) => {
                            const newMonth = e.target.value.replace(/[^0-9]/g, '');
                            if (newMonth.length <= 2) {
                                setMonth(newMonth);
                            }
                        }}
                        onKeyDown={(e) => {
                            if (e.key === 'Tab' || (e.key === 'Enter' && month.length === 2)) {
                                e.preventDefault();
                                const nextInput = e.target.parentElement.querySelector('input[placeholder="DD"]');
                                nextInput?.focus();
                            }
                        }}
                    />
                    <span className="text-gray-700 font-medium">-</span>
                    <input
                        type="text"
                        maxLength={2}
                        placeholder="DD"
                        className="w-full border border-gray-300 rounded-lg shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500 text-sm text-center"
                        value={day}
                        onChange={(e) => {
                            const newDay = e.target.value.replace(/[^0-9]/g, '');
                            if (newDay.length <= 2) {
                                setDay(newDay);
                            }
                        }}
                        onKeyDown={handleKeyDown}
                    />
                </div>
                
                {/* 날짜 선택기 */}
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

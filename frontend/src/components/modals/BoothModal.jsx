import React, { useState, useEffect } from 'react';
import { useModal } from '../../hooks/useModal';
import Modal from '../common/Modal';
import { placeCategories } from '../../data/categories';

const BoothModal = ({ booth, onSave, onClose }) => {
    const { openModal, showToast } = useModal();
    const isEditMode = !!booth;

    const [form, setForm] = useState({});

    useEffect(() => {
        setForm(booth || { title: '', category: Object.keys(placeCategories)[0] });
    }, [booth]);
    
    useEffect(() => {
        if (!isEditMode) { // Only for new booths
            if (form.category === 'SMOKING' || form.category === 'TRASH_CAN') {
                setForm(prev => ({
                    ...prev,
                    title: placeCategories[prev.category]
                }));
            }
        }
    }, [form.category, isEditMode]);

    const handleChange = e => setForm(prev => ({ ...prev, [e.target.name]: e.target.value }));
    


    const handleSave = () => { onSave(form); onClose(); };
    
    return (
        <Modal isOpen={true} onClose={onClose} maxWidth="max-w-2xl">
            <h3 className="text-xl font-bold mb-6">{isEditMode ? '플레이스 수정' : '새 플레이스 추가'}</h3>
            <div className={`space-y-4 ${isEditMode ? 'max-h-[60vh] overflow-y-auto pr-2' : ''}`}>
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">카테고리</label>
                    <select name="category" value={form.category || ''} onChange={handleChange} className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500 bg-white">
                        {Object.entries(placeCategories).map(([key, value]) => (
                            <option key={key} value={key}>{value}</option>
                        ))}
                    </select>
                </div>
                {isEditMode && (
                    <>
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">설명</label>
                            <textarea name="description" value={form.description} onChange={handleChange} rows="3" placeholder="플레이스에 대한 상세 설명을 입력해주세요." className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500" />
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">위치</label>
                            <input name="location" type="text" value={form.location} onChange={handleChange} placeholder="예: 학생회관 앞" className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500" />
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">운영 주체</label>
                            <input name="host" type="text" value={form.host} onChange={handleChange} placeholder="예: 컴퓨터공학과 학생회" className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500" />
                        </div>
                        <div className="grid grid-cols-2 gap-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">운영 시작 시간</label>
                                <input name="startTime" type="time" value={form.startTime} onChange={handleChange} className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500" />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">운영 종료 시간</label>
                                <input name="endTime" type="time" value={form.endTime} onChange={handleChange} className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500" />
                            </div>
                        </div>

                    </>
                )}
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

export default BoothModal;

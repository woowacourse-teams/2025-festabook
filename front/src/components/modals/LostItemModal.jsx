import React, { useState, useEffect } from 'react';
import Modal from '../common/Modal';
import { getCurrentDate } from '../../utils/date';

const LostItemModal = ({ item, onSave, onClose }) => {
    const [name, setName] = useState('');
    const [location, setLocation] = useState('');
    const [date, setDate] = useState('');
    const [photoPreview, setPhotoPreview] = useState('');

    useEffect(() => {
        setName(item?.name || '');
        setLocation(item?.location || '');
        setDate(item?.date || getCurrentDate());
        setPhotoPreview(item?.photo || '');
    }, [item]);

    const handlePhotoChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onloadend = () => setPhotoPreview(reader.result);
            reader.readAsDataURL(file);
        }
    };
    
    const handleSave = () => { onSave({ name, location, date: date || getCurrentDate(), photo: photoPreview }); onClose(); };

    return (
        <Modal isOpen={true} onClose={onClose}>
            <h3 className="text-xl font-bold mb-6">{item ? '분실물 정보 수정' : '새 분실물 등록'}</h3>
            <div className="space-y-4">
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">분실물명</label>
                    <input type="text" value={name} onChange={e => setName(e.target.value)} placeholder="예: 검정색 카드 지갑" className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500" />
                </div>
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">습득 장소</label>
                    <input type="text" value={location} onChange={e => setLocation(e.target.value)} placeholder="예: 학생회관 1층" className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500" />
                </div>
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">습득일</label>
                    <input type="date" value={date} onChange={e => setDate(e.target.value)} className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500" />
                </div>
                <div>
                    <label className="block text-sm font-medium text-gray-700">사진</label>
                    {photoPreview && <img src={photoPreview} alt="Preview" className="w-24 h-24 object-cover rounded-md my-2"/>}
                    <input type="file" accept="image/*" onChange={handlePhotoChange} className="mt-1 block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-indigo-50 file:text-indigo-700 hover:file:bg-indigo-100"/>
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

export default LostItemModal;

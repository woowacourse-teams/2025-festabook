import React, { useState, useEffect } from 'react';
import { useModal } from '../../hooks/useModal';
import Modal from '../common/Modal';
import { placeCategories } from '../../constants/categories';

const BoothModal = ({ booth, onSave, onClose }) => {
    const { openModal, showToast } = useModal();
    const isEditMode = !!booth;

    const [form, setForm] = useState({});
    const [editingNotice, setEditingNotice] = useState({ id: null, text: '' });

    useEffect(() => {
        setForm(booth || { title: '', category: Object.keys(placeCategories)[0], notices: [], images: [], mainImageIndex: -1 });
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
    
    const handlePhotoChange = (e) => {
        const files = Array.from(e.target.files);
        if (files.length === 0) return;

        const fileReadPromises = files.map(file => {
            return new Promise((resolve, reject) => {
                const reader = new FileReader();
                reader.onloadend = () => resolve(reader.result);
                reader.onerror = reject;
                reader.readAsDataURL(file);
            });
        });

        Promise.all(fileReadPromises).then(newImages => {
            const updatedImages = [...(form.images || []), ...newImages];
            setForm(prev => ({
                ...prev, 
                images: updatedImages,
                mainImageIndex: prev.mainImageIndex === -1 && updatedImages.length > 0 ? 0 : prev.mainImageIndex
            }));
        });
    };
    
    const handleSetMainImage = (index) => {
        setForm(prev => ({...prev, mainImageIndex: index}));
    };

    const handleStartEditNotice = (notice) => {
        setEditingNotice({ id: notice.id, text: notice.text });
    };

    const handleCancelEditNotice = () => {
        setEditingNotice({ id: null, text: '' });
    };

    const handleSaveNoticeEdit = () => {
        const newNotices = form.notices.map(n => 
            n.id === editingNotice.id ? { ...n, text: editingNotice.text } : n
        );
        setForm(prev => ({...prev, notices: newNotices}));
        handleCancelEditNotice();
    };


    const handleDeleteNotice = (id, text) => {
        openModal('confirm', {
            title: '공지 삭제 확인',
            message: `'${text}' 공지를 정말 삭제하시겠습니까?`,
            onConfirm: () => {
                const newNotices = form.notices.filter(n => n.id !== id);
                setForm(prev => ({...prev, notices: newNotices}));
                showToast('공지가 삭제되었습니다.');
            }
        });
    };

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
                        <div>
                            <label className="block text-sm font-medium text-gray-700">사진 (클릭하여 대표 사진 변경)</label>
                            <div className="mt-2 grid grid-cols-3 gap-4">
                               {form.images && form.images.map((img, index) => (
                                   <div key={index} className="relative cursor-pointer" onClick={() => handleSetMainImage(index)}>
                                       <img src={img} alt={`booth-${index}`} className={`w-full h-24 object-cover rounded-md border-2 ${form.mainImageIndex === index ? 'border-blue-500' : 'border-transparent'}`}/>
                                       {form.mainImageIndex === index && <span className="main-image-indicator">대표</span>}
                                   </div>
                               ))}
                            </div>
                            <input type="file" accept="image/*" multiple onChange={handlePhotoChange} className="mt-2 block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-indigo-50 file:text-indigo-700 hover:file:bg-indigo-100"/>
                        </div>
                         <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">개별 공지사항</label>
                            <ul className="mt-2 space-y-1">
                                {form.notices && form.notices.map(notice => (
                                    <li key={notice.id} className="flex justify-between items-center text-sm bg-gray-100 p-2 rounded-md">
                                        {editingNotice.id === notice.id ? (
                                            <>
                                                <input type="text" value={editingNotice.text} onChange={(e) => setEditingNotice(prev => ({...prev, text: e.target.value}))} className="flex-1 block w-full border border-gray-300 rounded-md shadow-sm py-1 px-2 mr-2"/>
                                                <div className="flex items-center space-x-3">
                                                    <button onClick={handleSaveNoticeEdit} className="text-green-600 hover:text-green-800"><i className="fas fa-check"></i></button>
                                                    <button onClick={handleCancelEditNotice} className="text-gray-500 hover:text-gray-700"><i className="fas fa-times"></i></button>
                                                    <button onClick={() => handleDeleteNotice(notice.id, notice.text)} className="text-red-500 hover:text-red-700"><i className="fas fa-trash"></i></button>
                                                </div>
                                            </>
                                        ) : (
                                            <>
                                                <span>{notice.text}</span>
                                                <div className="space-x-3">
                                                    <button onClick={() => handleStartEditNotice(notice)} className="text-blue-600 hover:text-blue-800 text-sm">수정</button>
                                                </div>
                                            </>
                                        )}
                                    </li>
                                ))}
                            </ul>
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

import React, { useState, useEffect } from 'react';
import { useModal } from '../../hooks/useModal';
import Modal from '../common/Modal';
import { placeCategories } from '../../constants/categories';
// import api from '../utils/api';
import { isMainPlace, getDefaultValueIfNull } from '../../utils/booth';

// 새 플레이스 생성용 모달 (카테고리만 선택)
const CreateBoothModal = ({ onSave, onClose }) => {
    const [form, setForm] = useState({ category: Object.keys(placeCategories)[0] });

    const handleChange = e => setForm(prev => ({ ...prev, [e.target.name]: e.target.value }));

    const handleSave = () => {
        onSave(form);
        onClose();
    };

    return (
        <Modal isOpen={true} onClose={onClose} maxWidth="max-w-md">
            <h3 className="text-xl font-bold mb-6">새 플레이스 추가</h3>
            <div className="space-y-4">
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">카테고리</label>
                    <select
                        name="category"
                        value={form.category || ''}
                        onChange={handleChange}
                        className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500 bg-white"
                    >
                        {Object.entries(placeCategories).map(([key, value]) => (
                            <option key={key} value={key}>{value}</option>
                        ))}
                    </select>
                </div>
            </div>
            <div className="mt-6 flex justify-between w-full">
                <div className="space-x-3">
                    <button onClick={onClose} className="bg-gray-200 hover:bg-gray-300 text-gray-800 font-bold py-2 px-4 rounded-lg">취소</button>
                    <button onClick={handleSave} className="bg-gray-800 hover:bg-gray-900 text-white font-bold py-2 px-4 rounded-lg">생성</button>
                </div>
            </div>
        </Modal>
    );
};

// 기존 플레이스 수정용 모달 (모든 필드 편집 가능)
const EditBoothModal = ({ booth, onSave, onClose, isMainPlace }) => {
    const { openModal, showToast } = useModal();
    const [form, setForm] = useState({});
    const [editingNotice, setEditingNotice] = useState({ id: null, text: '' });

    useEffect(() => {
        setForm(booth || { title: '', category: Object.keys(placeCategories)[0], notices: [], images: [], mainImageIndex: -1 });
    }, [booth]);

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

    // form이 수정된 값
    const handleSave = () => {
        onSave(form);

        // 모달 닫기
        onClose();
    };

    // 메인 플레이스 카테고리만 필터링
    const filteredCategories = Object.entries(placeCategories).filter(([key, value]) => {
        return isMainPlace(key);
    });

    // 이미지 렌더링
    const renderImage = (mainIdx, title, image, idx) => {
        const alt = `${title} ${idx + 1}`;
        const isMainImage = (image.id === mainIdx);


        return <div key={image.imageUrl} className="relative">
            <img src={image.imageUrl} alt={alt} className="w-full h-24 object-cover rounded-md" />
            {isMainImage && <span className="main-image-indicator">대표</span>}
        </div>
    }

    // 공지 렌더링
    const renderAnnouncement = (announcement) => {
        let content = announcement.content;

        return <li key={announcement.id}>{announcement.title} - {content}</li>
    }

    return (
        <Modal isOpen={true} onClose={onClose} maxWidth="max-w-2xl">
            <h3 className="text-xl font-bold mb-6">플레이스 수정</h3>
            <div className="space-y-4 max-h-[60vh] overflow-y-auto pr-2">
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">카테고리</label>
                    <select
                        name="category"
                        value={form.category || ''}
                        onChange={handleChange}
                        className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500 bg-white"
                    >
                        {filteredCategories.map(([key, value]) => (
                            <option key={key} value={key}>{value}</option>
                        ))}
                    </select>
                </div>
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">플레이스명</label>
                    <input
                        name="title"
                        type="text"
                        value={form.title || ''}
                        onChange={handleChange}
                        placeholder="플레이스 이름을 입력해주세요."
                        className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500"
                    />
                </div>
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">설명</label>
                    <textarea
                        name="description"
                        value={form.description || ''}
                        onChange={handleChange}
                        rows="3"
                        placeholder="플레이스에 대한 상세 설명을 입력해주세요."
                        className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500"
                    />
                </div>
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">위치</label>
                    <input
                        name="location"
                        type="text"
                        value={form.location || ''}
                        onChange={handleChange}
                        placeholder="예: 학생회관 앞"
                        className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500"
                    />
                </div>
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">운영 주체</label>
                    <input
                        name="host"
                        type="text"
                        value={form.host || ''}
                        onChange={handleChange}
                        placeholder="예: 컴퓨터공학과 학생회"
                        className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500"
                    />
                </div>
                <div className="grid grid-cols-2 gap-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">운영 시작 시간</label>
                        <input
                            name="startTime"
                            type="time"
                            value={form.startTime || ''}
                            onChange={handleChange}
                            className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500"
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">운영 종료 시간</label>
                        <input
                            name="endTime"
                            type="time"
                            value={form.endTime || ''}
                            onChange={handleChange}
                            className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500"
                        />
                    </div>
                </div>
                <div>
                    <label className="block text-sm font-medium text-gray-700">사진 (클릭하여 대표 사진 변경)</label>
                    <div className="mt-2 grid grid-cols-3 gap-4">
                        {form.placeImages && form.placeImages.map((img, index) => (
                            renderImage(form.placeImages[0].id, form.title, img, index)
                        ))}
                    </div>
                    <input type="file" accept="image/*" multiple onChange={handlePhotoChange} className="mt-2 block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-indigo-50 file:text-indigo-700 hover:file:bg-indigo-100" />
                </div>
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">개별 공지사항</label>
                    <ul className="list-disc list-inside text-sm mt-2 space-y-1">
                        {form.placeAnnouncements.map(announcement => renderAnnouncement(announcement))}
                    </ul>
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

// 기존 BoothModal을 CreateBoothModal과 EditBoothModal로 분리
const BoothModal = ({ booth, onSave, onClose, isMainPlace }) => {
    const isEditMode = !!booth;
    console.log(booth, isEditMode);

    if (isEditMode) {
        return (
            <EditBoothModal
                booth={booth}
                onSave={onSave}
                onClose={onClose}
                isMainPlace={isMainPlace}
            />
        );
    } else {
        return (
            <CreateBoothModal
                onSave={onSave}
                onClose={onClose}
            />
        );
    }
};

export default BoothModal;

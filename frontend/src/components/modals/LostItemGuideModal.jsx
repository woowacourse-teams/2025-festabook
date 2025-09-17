import React, { useEffect, useState } from 'react';
import Modal from '../common/Modal';

const LostItemGuideModal = ({ initialGuide, onSave, onClose }) => {
    const [guide, setGuide] = useState('');

    useEffect(() => {
        setGuide(initialGuide || '');
    }, [initialGuide]);

    return (
        <Modal isOpen={true} onClose={onClose} maxWidth="max-w-xl">
            <div className="p-6">
                <h2 className="text-2xl font-bold text-gray-900 mb-4">분실물 안내 수정</h2>
                <div className="flex items-center justify-between mb-3">
                    <p className="text-gray-500 text-sm">분실물 수령/제보 가이드를 입력하세요.</p>
                    <span className="text-xs text-gray-400">{guide.length}/1000</span>
                </div>
                <textarea
                    value={guide}
                    onChange={(e) => setGuide(e.target.value)}
                    rows={6}
                    placeholder="예) 분실물을 발견하시면 운영본부로 제출해 주세요..."
                    maxLength={1000}
                    className="w-full border border-gray-300 rounded-md p-3 focus:outline-none focus:ring-2 focus:ring-gray-800"
                />
                <div className="flex justify-end gap-2 mt-4">
                    <button onClick={onClose} className="px-4 py-2 bg-gray-200 rounded-lg hover:bg-gray-300">취소</button>
                    <button
                        onClick={() => { onSave(guide); onClose(); }}
                        className="px-4 py-2 bg-gray-800 text-white rounded-lg hover:bg-gray-900"
                        disabled={!guide.trim() || guide.length > 1000}
                    >저장</button>
                </div>
            </div>
        </Modal>
    );
};

export default LostItemGuideModal;



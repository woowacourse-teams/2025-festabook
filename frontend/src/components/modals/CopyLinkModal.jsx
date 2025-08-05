import React, { useRef } from 'react';
import { useModal } from '../../hooks/useModal';
import Modal from '../common/Modal';

const CopyLinkModal = ({ link, onClose }) => {
    const { showToast } = useModal();
    const inputRef = useRef(null);

    const copyToClipboard = () => {
        inputRef.current.select();
        try {
            document.execCommand('copy');
            showToast('링크가 복사되었습니다!');
        } catch {
            showToast('링크 복사에 실패했습니다.');
        }
    };

    return (
        <Modal isOpen={true} onClose={onClose} maxWidth="max-w-md">
            <h3 className="text-xl font-bold mb-4">권한 링크 복사</h3>
            <p className="text-sm text-gray-600 mb-4">이 링크를 가진 사람은 플레이스 정보를 수정할 수 있습니다. 신중하게 공유해주세요.</p>
            <div className="flex space-x-2">
                <input ref={inputRef} type="text" readOnly value={link} className="flex-1 block w-full bg-gray-100 border border-gray-300 rounded-md shadow-sm py-2 px-3" />
                <button onClick={copyToClipboard} className="bg-indigo-600 hover:bg-indigo-700 text-white font-bold py-2 px-4 rounded-lg">복사</button>
            </div>
        </Modal>
    );
};

export default CopyLinkModal;

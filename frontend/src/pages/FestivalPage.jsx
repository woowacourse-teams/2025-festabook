import React, { useState } from 'react';
import Modal from '../components/common/Modal';

const FestivalPage = () => {
    const [festivalId, setFestivalId] = useState('');
    const [error, setError] = useState('');

    const handleSubmit = (e) => {
        e.preventDefault();
        if (!festivalId.trim()) {
            setError('Festival ID를 입력해주세요.');
            return;
        }
        localStorage.setItem('festivalId', festivalId.trim());
        window.location.reload(); // force re-render to hide modal
    };

    return (
        <Modal isOpen={true} onClose={() => {}} maxWidth="max-w-md">
            <form onSubmit={handleSubmit}>
                <h2 className="text-2xl font-bold mb-6 text-center">Festival ID를 입력해주세요</h2>
                <input
                    type="text"
                    value={festivalId}
                    onChange={e => { setFestivalId(e.target.value); setError(''); }}
                    placeholder="festival id를 입력하여주세요!"
                    className="block w-full border border-gray-300 rounded-lg shadow-sm py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500 mb-4"
                    autoFocus
                />
                {error && <div className="text-red-500 text-sm mb-2">{error}</div>}
                <button
                    type="submit"
                    className="w-full bg-black text-white font-bold py-2 px-4 rounded-lg hover:bg-gray-800 transition-colors duration-200"
                >
                    확인
                </button>
            </form>
        </Modal>
    );
};

export default FestivalPage;

import React, { useEffect } from 'react';
import Modal from '../common/Modal';

const PushNotificationConfirmModal = ({ notice, onConfirm, onCancel }) => {
    useEffect(() => {
        const handleKeyPress = (e) => {
            if (e.key === 'Enter') {
                e.preventDefault();
                onConfirm();
            } else if (e.key === 'Escape') {
                e.preventDefault();
                onCancel();
            }
        };

        document.addEventListener('keydown', handleKeyPress);
        return () => {
            document.removeEventListener('keydown', handleKeyPress);
        };
    }, [onConfirm, onCancel]);

    return (
        <Modal isOpen={true} onClose={onCancel} maxWidth="max-w-2xl">
            <div className="space-y-6">
                <div>
                    <h3 className="text-xl font-bold text-gray-800 mb-2">푸시 알림 전송 확인</h3>
                    <p className="text-gray-600">다음 내용으로 푸시 알림을 전송하시겠습니까?</p>
                </div>

                {/* 알림 미리보기 */}
                <div className="bg-gradient-to-b from-gray-50 to-gray-100 p-4 rounded-xl border border-gray-200">
                    <div className="bg-white rounded-xl p-4 shadow-lg border border-gray-200">
                        <div className="flex items-start space-x-3">
                            <div className="relative flex-shrink-0">
                                <img 
                                    src="/festabook-logo.svg" 
                                    alt="festabook" 
                                    className="w-10 h-10 rounded-xl bg-blue-50 p-1"
                                />
                            </div>
                            <div className="flex-1 min-w-0">
                                <div className="flex items-center justify-between mb-1">
                                    <span className="text-sm font-bold text-gray-900">festabook</span>
                                    <span className="text-xs text-gray-500">지금</span>
                                </div>
                                <div className="text-sm font-semibold text-gray-800 mb-1 leading-snug">
                                    {notice.title}
                                </div>
                                <div className="text-sm text-gray-600 leading-relaxed">
                                    {notice.content.length > 120 
                                        ? `${notice.content.substring(0, 120)}...` 
                                        : notice.content
                                    }
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="text-center mt-2">
                        <span className="text-xs text-gray-500 bg-gray-200 px-2 py-1 rounded-full">
                            알림 미리보기
                        </span>
                    </div>
                </div>

                {/* 주의사항 */}
                <div className="bg-yellow-50 border border-yellow-200 rounded-xl p-4">
                    <div className="flex items-start space-x-3">
                        <i className="fas fa-exclamation-triangle text-yellow-600 text-lg mt-0.5"></i>
                        <div className="flex-1">
                            <div className="text-base font-medium text-yellow-800 mb-2">
                                알림 전송 시 주의사항
                            </div>
                            <ul className="grid grid-cols-1 gap-2 text-sm text-yellow-700">
                                <li className="flex items-center space-x-2">
                                    <i className="fas fa-clock text-yellow-600"></i>
                                    <span>전송 후 3분간 재전송이 불가능합니다</span>
                                </li>
                                <li className="flex items-center space-x-2">
                                    <i className="fas fa-users text-yellow-600"></i>
                                    <span>앱 사용하는 학우들에게 알림이 전송됩니다</span>
                                </li>
                                <li className="flex items-center space-x-2">
                                    <i className="fas fa-bell-slash text-yellow-600"></i>
                                    <span>반복 알림은 학우가 알림을 꺼둘 수 있으니, 필요한 경우에만 사용하는 것이 좋습니다.</span>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>

            <div className="mt-6 flex justify-end space-x-3">
                <button 
                    onClick={onCancel} 
                    className="bg-gray-200 hover:bg-gray-300 text-gray-800 font-bold py-2.5 px-5 rounded-lg transition-colors"
                >
                    아니요
                </button>
                <button 
                    onClick={onConfirm}
                    className="bg-emerald-600 hover:bg-emerald-700 text-white font-bold py-2.5 px-5 rounded-lg transition-colors flex items-center space-x-2"
                >
                    <i className="fas fa-paper-plane text-sm"></i>
                    <span>예, 전송합니다</span>
                </button>
            </div>
        </Modal>
    );
};

export default PushNotificationConfirmModal;
import React, { useState, useEffect, useRef, useCallback } from 'react';

const Modal = ({ isOpen, onClose, children, maxWidth = 'max-w-lg', showCloseButton = true }) => {
    const [visible, setVisible] = useState(false);
    const [showContent, setShowContent] = useState(false);
    const modalRef = useRef(null);
    const backdropRef = useRef(null);

    useEffect(() => {
        if (isOpen) {
            setVisible(true);
            setTimeout(() => setShowContent(true), 10);
        } else {
            setShowContent(false);
        }
    }, [isOpen]);

    // 트랜지션이 끝났을 때만 visible을 false로 변경
    const handleBackdropTransitionEnd = useCallback((e) => {
        if (!showContent && e.target === backdropRef.current && e.propertyName === 'opacity') {
            setVisible(false);
        }
    }, [showContent]);

    // 바깥 클릭으로 닫히지 않도록 제거
    // const handleBackdropClick = (e) => {
    //     if (modalRef.current && !modalRef.current.contains(e.target)) {
    //         onClose();
    //     }
    // };

    if (!visible) return null;

    return (
        <div
            ref={backdropRef}
            className={`modal-backdrop fixed inset-0 flex justify-center items-center transition-opacity duration-300 z-50 ${showContent ? 'bg-black/50' : 'bg-black/0'}`}
            onTransitionEnd={handleBackdropTransitionEnd}
        >
            <div
                ref={modalRef}
                className={`modal-content ${showContent ? 'modal-content-end' : 'modal-content-start'} bg-white rounded-lg shadow-xl w-full ${maxWidth} p-6 relative`}
                onClick={e => e.stopPropagation()}
            >
                {showCloseButton && (
                    <button
                        onClick={onClose}
                        className="absolute top-4 right-4 text-gray-400 hover:text-gray-600 transition-colors duration-200"
                        aria-label="모달 닫기"
                    >
                        <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                        </svg>
                    </button>
                )}
                {children}
            </div>
        </div>
    );
};

export default Modal;

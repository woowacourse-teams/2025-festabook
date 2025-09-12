import React, { useState, useEffect, useRef, useCallback } from 'react';

const Modal = ({ isOpen, onClose, children, maxWidth = 'max-w-lg' }) => {
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

    const handleBackdropClick = (e) => {
        if (modalRef.current && !modalRef.current.contains(e.target)) {
            onClose();
        }
    };

    if (!visible) return null;

    return (
        <div
            ref={backdropRef}
            className={`modal-backdrop fixed inset-0 flex justify-center items-center transition-opacity duration-300 z-50 ${showContent ? 'bg-black/50' : 'bg-black/0'}`}
            onClick={handleBackdropClick}
            onTransitionEnd={handleBackdropTransitionEnd}
        >
            <div
                ref={modalRef}
                className={`modal-content ${showContent ? 'modal-content-end' : 'modal-content-start'} bg-white rounded-lg shadow-xl w-full ${maxWidth} p-6`}
                onClick={e => e.stopPropagation()}
            >
                {children}
            </div>
        </div>
    );
};

export default Modal;

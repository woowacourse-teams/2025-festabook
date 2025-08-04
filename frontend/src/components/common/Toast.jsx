import React, { useState, useEffect } from 'react';

const Toast = ({ message, onEnd }) => {
    const [show, setShow] = useState(false);
    useEffect(() => {
        const showTimer = setTimeout(() => setShow(true), 10);
        const hideTimer = setTimeout(() => {
            setShow(false);
            const removeTimer = setTimeout(onEnd, 400);
            return () => clearTimeout(removeTimer);
        }, 3000);
        return () => { clearTimeout(showTimer); clearTimeout(hideTimer); };
    }, [onEnd]);
    return <div className={`toast ${show ? 'show' : ''}`}>{message}</div>;
};

export default Toast;

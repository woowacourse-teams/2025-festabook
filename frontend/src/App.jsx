import React, { useState, useCallback } from 'react';

// Contexts & Providers
import { DataProvider } from './contexts/DataProvider';
import { PageContext } from './contexts/PageContext';
import { ModalContext } from './contexts/ModalContext';

// Layout
import Sidebar from './components/layout/Sidebar';

// Pages

import HomePage from './pages/HomePage';
import SchedulePage from './pages/SchedulePage';
import BoothsPage from './pages/BoothsPage';
import MapSettingsPage from './pages/MapSettingsPage';
import NoticesPage from './pages/NoticesPage';
import LostFoundPage from './pages/LostFoundPage';
import FaqPage from './pages/FaqPage';

// Modals
import NoticeModal from './components/modals/NoticeModal';
import LostItemModal from './components/modals/LostItemModal';
import FaqModal from './components/modals/FaqModal';
import ScheduleModal from './components/modals/ScheduleModal';
import DatePromptModal from './components/modals/DatePromptModal';
import BoothModal from './components/modals/BoothModal';
import CopyLinkModal from './components/modals/CopyLinkModal';
import FestivalInfoModal from './components/modals/FestivalInfoModal';
import FestivalImagesModal from './components/modals/FestivalImagesModal';
import PlaceImagesModal from './components/modals/PlaceImagesModal';
import PlaceManageModal from './components/modals/PlaceManageModal';
import PlaceEditModal from './components/modals/PlaceEditModal';
import AddImageModal from './components/modals/AddImageModal';
import ConfirmModal from './components/common/ConfirmModal';
import Modal from './components/common/Modal';
import { NoticeDetailModal } from './components/modals/NoticeModal';
import LineupAddModal from './components/modals/LineupAddModal';
import LineupEditModal from './components/modals/LineupEditModal';
import PushNotificationConfirmModal from './components/modals/PushNotificationConfirmModal';

// Common Components
import Toast from './components/common/Toast';
import FestivalPage from "./pages/FestivalPage.jsx";

function App() {
    const [page, setPage] = useState('home');
    const [modalState, setModalState] = useState({ type: null, props: {} });
    const [toasts, setToasts] = useState([]);
    const [sidebarOpen, setSidebarOpen] = useState(true); // 사이드바 열림/닫힘 상태 추가

    const showToast = useCallback((message) => {
        const id = Date.now() + Math.random();
        setToasts(prev => [...prev, { id, message, onEnd: () => setToasts(p => p.filter(t => t.id !== id)) }]);
    }, []);

    const openModal = (type, props = {}) => setModalState({ type, props });
    const closeModal = () => setModalState({ type: null, props: {} });


    

    const renderPage = () => {
        switch (page) {

            case 'home': return <HomePage />;
            case 'schedule': return <SchedulePage />;
            case 'booths': return <BoothsPage />;
            case 'map-settings': return <MapSettingsPage />;
            case 'notices': return <NoticesPage />;
            case 'lost-found': return <LostFoundPage />;
            case 'faq': return <FaqPage />;
            default: return <HomePage />;
        }
    };

    const renderModal = () => {
        const { type, props } = modalState;
        const allProps = { ...props, onClose: closeModal, showToast, openModal };
        switch (type) {
            case 'notice': return <NoticeModal {...allProps} />;
            case 'notice-detail': return <NoticeDetailModal {...allProps} />;
            case 'lostItem': return <LostItemModal {...allProps} />;
            case 'faq': return <FaqModal {...allProps} />;
            case 'schedule': return <ScheduleModal {...allProps} />;
            case 'datePrompt': return <DatePromptModal {...allProps} />;
            case 'booth': return <BoothModal {...allProps} />;
            case 'copyLink': return <CopyLinkModal {...allProps} />;
            case 'festival-info': return <FestivalInfoModal isOpen={true} {...allProps} />;
            case 'festival-images': return <FestivalImagesModal isOpen={true} {...allProps} />;
            case 'festivalImages': return <FestivalImagesModal isOpen={true} {...allProps} />;
            case 'placeImages': return <PlaceImagesModal {...allProps} />;
            case 'placeEdit': return <PlaceEditModal {...allProps} />;
            case 'confirm': return <ConfirmModal {...allProps} onConfirm={() => { props.onConfirm(); closeModal(); }} onCancel={closeModal} />;
            case 'image': return <Modal isOpen={true} onClose={closeModal} maxWidth="max-w-4xl"><div className="relative"><img src={props.src} className="max-w-full max-h-[80vh] rounded-lg mx-auto" alt="상세 이미지" /><button onClick={closeModal} className="absolute top-2 right-2 text-white text-3xl bg-black bg-opacity-50 rounded-full w-8 h-8 flex items-center justify-center">&times;</button></div></Modal>;
            case 'festival': return <FestivalPage {...allProps} />;
            case 'lineup-add': return <LineupAddModal isOpen={true} {...allProps} />;
            case 'lineup-edit': return <LineupEditModal isOpen={true} {...allProps} />;
            case 'add-image': return <AddImageModal isOpen={true} {...allProps} />;
            case 'pushNotificationConfirm': return <PushNotificationConfirmModal {...allProps} onConfirm={() => { props.onConfirm(); closeModal(); }} onCancel={closeModal} />;
            
            default: return null;
        }
    };

    return (
        <DataProvider>
            <PageContext.Provider value={{ page, setPage }}>
                <ModalContext.Provider value={{ openModal, closeModal, showToast }}>
                    <div className="bg-gray-100 text-gray-800 flex h-screen">

                        {localStorage.getItem('festivalId') === null ? <FestivalPage/> : <></>}
                        <Sidebar open={sidebarOpen} setOpen={setSidebarOpen} />
                        <main className="flex-1 p-8 overflow-y-auto">
                            {renderPage()}
                        </main>
                        {renderModal()}
                        <div className="toast-container">
                           {toasts.map(toast => <Toast key={toast.id} {...toast} />)}
                        </div>
                    </div>
                </ModalContext.Provider>
            </PageContext.Provider>
        </DataProvider>
    );
}

export default App;

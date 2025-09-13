import React, { useState, useCallback } from 'react';

// Contexts & Providers
import { DataProvider } from './contexts/DataProvider';
import { PageContext } from './contexts/PageContext';
import { ModalContext } from './contexts/ModalContext';

// Layout
import Sidebar from './components/layout/Sidebar';

// Pages

import FestivalPage from './pages/FestivalPage';
import EventPage from './pages/EventPage';
import PlacePage from './pages/PlacePage';
import PlaceGeographyPage from './pages/PlaceGeographyPage';
import AnnouncementPage from './pages/AnnouncementPage';
import LostItemPage from './pages/LostItemPage';
import FaqPage from './pages/FaqPage';

// Modals
import AnnouncementModal from './components/modals/AnnouncementModal';
import LostItemModal from './components/modals/LostItemModal';
import FaqModal from './components/modals/FaqModal';
import EventModal from './components/modals/EventModal';
import EventDateModal from './components/modals/EventDateModal';
import PlaceModal from './components/modals/PlaceModal';
import FestivalInfoModal from './components/modals/FestivalInfoModal';
import FestivalImagesModal from './components/modals/FestivalImagesModal';
import PlaceImagesModal from './components/modals/PlaceImagesModal';
import PlaceEditModal from './components/modals/PlaceEditModal';
import AddImageModal from './components/modals/AddImageModal';
import ConfirmModal from './components/common/ConfirmModal';
import Modal from './components/common/Modal';
import { AnnouncementDetailModal } from './components/modals/AnnouncementModal';
import LineupAddModal from './components/modals/LineupAddModal';
import LineupEditModal from './components/modals/LineupEditModal';
import PushNotificationConfirmModal from './components/modals/PushNotificationConfirmModal';
import PasswordChangeModal from './components/modals/PasswordChangeModal';

// Common Components
import Toast from './components/common/Toast';

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

            case 'home': return <FestivalPage />;
            case 'schedule': return <EventPage />;
            case 'booths': return <PlacePage />;
            case 'place': return <PlacePage />;
            case 'map-settings': return <PlaceGeographyPage />;
            case 'notices': return <AnnouncementPage />;
            case 'lost-found': return <LostItemPage />;
            case 'faq': return <FaqPage />;
            default: return <FestivalPage />;
        }
    };

    const renderModal = () => {
        const { type, props } = modalState;
        const allProps = { ...props, onClose: closeModal, showToast, openModal };
        switch (type) {
            case 'notice': return <AnnouncementModal {...allProps} />;
            case 'notice-detail': return <AnnouncementDetailModal {...allProps} />;
            case 'lostItem': return <LostItemModal {...allProps} />;
            case 'faq': return <FaqModal {...allProps} />;
            case 'schedule': return <EventModal {...allProps} />;
            case 'datePrompt': return <EventDateModal {...allProps} />;
            case 'booth': return <PlaceModal {...allProps} />;
            case 'festival-info': return <FestivalInfoModal isOpen={true} {...allProps} />;
            case 'festival-images': return <FestivalImagesModal isOpen={true} {...allProps} />;
            case 'festivalImages': return <FestivalImagesModal isOpen={true} {...allProps} />;
            case 'placeImages': return <PlaceImagesModal {...allProps} />;
            case 'placeEdit': return <PlaceEditModal {...allProps} />;
            case 'confirm': return <ConfirmModal {...allProps} onConfirm={() => { props.onConfirm(); closeModal(); }} onCancel={closeModal} />;
            case 'image': return <Modal isOpen={true} onClose={closeModal} maxWidth="max-w-4xl"><div className="relative"><img src={props.src} className="max-w-full max-h-[80vh] rounded-lg mx-auto" alt="상세 이미지" /><button onClick={closeModal} className="absolute top-2 right-2 text-white text-3xl bg-black bg-opacity-50 rounded-full w-8 h-8 flex items-center justify-center">&times;</button></div></Modal>;
            case 'lineup-add': return <LineupAddModal isOpen={true} {...allProps} />;
            case 'lineup-edit': return <LineupEditModal isOpen={true} {...allProps} />;
            case 'add-image': return <AddImageModal isOpen={true} {...allProps} />;
            case 'pushNotificationConfirm': return <PushNotificationConfirmModal {...allProps} onConfirm={() => { props.onConfirm(); closeModal(); }} onCancel={closeModal} />;
            case 'passwordChange': return <PasswordChangeModal isOpen={true} {...allProps} />;
            
            default: return null;
        }
    };

    return (
        <DataProvider>
            <PageContext.Provider value={{ page, setPage }}>
                <ModalContext.Provider value={{ openModal, closeModal, showToast }}>
                    <div className="bg-gray-100 text-gray-800 flex h-screen">

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

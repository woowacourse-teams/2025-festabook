import React, { useState, useRef, useContext } from 'react';
import { usePage } from '../../hooks/usePage';
import { ModalContext } from '../../contexts/ModalContext';

const Sidebar = ({ open, setOpen }) => {
    const { page, setPage } = usePage();
    const { openModal } = useContext(ModalContext);
    const [openMenus, setOpenMenus] = useState([false, false]);
    const [textVisible, setTextVisible] = useState(open);
    const handleNav = (targetPage) => setPage(targetPage);
    const handleSubMenuToggle = (idx) => {
        setOpenMenus((prev) => {
            const next = [...prev];
            next[idx] = !next[idx];
            return next;
        });
    };

    // 텍스트 표시 지연 처리
    React.useEffect(() => {
        if (open) {
            // 열 때: 사이드바 너비 변화 후 텍스트 표시
            const timer = setTimeout(() => setTextVisible(true), 150);
            return () => clearTimeout(timer);
        } else {
            // 닫을 때: 텍스트 먼저 숨기고 사이드바 축소
            setTextVisible(false);
        }
    }, [open]);
    // NavLink 컴포넌트도 open prop을 받도록 수정
    const NavLink = ({ target, icon, children, open }) => (
        <a
            href="#"
            onClick={(e) => { e.preventDefault(); handleNav(target); }}
            className={`sidebar-link flex items-center py-3 rounded-lg transition duration-200 hover:bg-gray-700 hover:text-white ${page === target ? 'active' : 'text-gray-600'} px-4`}
            style={{ justifyContent: 'flex-start' }}
        >
            <i className={`fas ${icon} w-6 text-gray-500`}></i>
            {open && (
                <span 
                className={`ml-2 transition-all duration-300 ease-in-out whitespace-nowrap overflow-hidden ${textVisible ? 'opacity-100 visible' : 'opacity-0 invisible'}`}
                >
                    {children}
                </span>
            )}
        </a>
    );
    // SubMenu는 사이드바가 닫혀있으면 아이콘만, 열려있으면 텍스트와 하위 메뉴까지 보이도록 수정
    const SubMenu = ({ icon, title, links, open, onToggle, sidebarOpen }) => {
        const isActive = links.some(l => l.target === page);
        const contentRef = useRef(null);
        return (
            <div className="relative">
                <a
                    href="#"
                    onClick={e => {
                        e.preventDefault();
                        e.stopPropagation(); // 중복 방지
                        if (sidebarOpen) onToggle();
                    }}
                    className={`sidebar-link flex items-center justify-between py-3 px-4 rounded-lg transition duration-200 hover:bg-gray-700 hover:text-white cursor-pointer ${isActive ? 'active' : 'text-gray-600'}`}
                >
                    <div className="flex items-center">
                        <i className={`fas ${icon} w-6 text-gray-500`}></i>
                        {sidebarOpen && (
                            <span 
                                className={`ml-2 transition-opacity duration-200 whitespace-nowrap overflow-hidden ${textVisible ? 'opacity-100' : 'opacity-0'}`}
                            >
                                {title}
                            </span>
                        )}
                    </div>
                    {/* 사이드바가 열려있을 때만 토글(chevron) 아이콘 노출 */}
                    {sidebarOpen && (
                        <i className={`fas fa-chevron-down text-xs transition-transform duration-300 ${open ? 'rotate-180' : ''}`}></i>
                    )}
                </a>
                {/* 사이드바가 열려있을 때만 하위 메뉴 렌더링 */}
                {sidebarOpen && (
                    <div
                        ref={contentRef}
                        style={{
                            maxHeight: open ? '200px' : '0px',
                            overflow: 'hidden'
                        }}
                        className={"mt-2 pl-8 space-y-1"}
                    >
                        {links.map(link => (
                            <a
                                key={link.target}
                                href="#"
                                onClick={(e) => {
                                    e.preventDefault();
                                    e.stopPropagation();
                                    handleNav(link.target);
                                }}
                                className={`sidebar-link sub-link block py-2 px-2 rounded-lg transition duration-200 hover:bg-indigo-500 hover:text-white ${page === link.target ? 'active' : 'text-gray-500'}`}
                            >
                                {link.title}
                            </a>
                        ))}
                    </div>
                )}
            </div>
        );
    };
    return (
        <aside
            className={
                `${open ? 'w-64 p-6' : 'w-16 p-2'} bg-gray-50 shrink-0 flex flex-col border-r border-gray-200 h-full transition-all duration-300 relative`
            }
            style={{ minHeight: '100vh' }}
        >
            {/* 상단: Festabook, 자물쇠, 열기/닫기 버튼을 한 줄에 배치 */}
            {open ? (
                <div className={`flex flex-row items-center mb-4 mt-2 shrink-0 transition-all duration-300 justify-between gap-2`}>
                    <div className="flex flex-row items-center gap-2 min-w-0">
                        <h1
                            className={`text-xl font-bold cursor-pointer transition-all duration-300 whitespace-nowrap overflow-hidden text-ellipsis ${textVisible ? 'opacity-100' : 'opacity-0'}`}
                            onClick={() => setPage('dashboard')}
                        >
                            Festabook
                        </h1>
                        <button
                            className="text-gray-500 hover:text-gray-800 focus:outline-none"
                            title="Organization 변경"
                            onClick={() => openModal('organization')}
                        >
                            <i className="fas fa-lock text-lg" />
                        </button>
                    </div>
                    {/* 열기/닫기 버튼: 항상 가장 오른쪽 */}
                    <button
                        className="text-gray-500 hover:text-gray-800 focus:outline-none flex-shrink-0"
                        title="탭 닫기"
                        onClick={() => {
                            setOpen(false);
                            // 지도에 사이드바 변화 알림
                            setTimeout(() => {
                                window.dispatchEvent(new CustomEvent('sidebarToggle'));
                            }, 100);
                        }}
                    >
                        <i className="fas fa-angle-left text-lg" />
                    </button>
                </div>
            ) : (
                <div className="flex flex-col items-center justify-center mb-4 mt-2 shrink-0 transition-all duration-300" style={{height: '56px'}}>
                    <button
                        className="text-gray-500 hover:text-gray-800 focus:outline-none flex-shrink-0"
                        title="탭 열기"
                        onClick={() => {
                            setOpen(true);
                            // 지도에 사이드바 변화 알림
                            setTimeout(() => {
                                window.dispatchEvent(new CustomEvent('sidebarToggle'));
                            }, 100);
                        }}
                    >
                        <i className="fas fa-angle-right text-lg" />
                    </button>
                </div>
            )}
            <div className={`w-full h-px bg-gray-300 ${open ? '' : 'hidden'}`} />
            <nav className="flex flex-col space-y-2 mt-4">
                <NavLink target="dashboard" icon="fa-tachometer-alt" open={open}>대시보드</NavLink>
                <NavLink target="home" icon="fa-home" open={open}>홈</NavLink>
                <NavLink target="schedule" icon="fa-calendar-alt" open={open}>일정</NavLink>
                <SubMenu icon="fa-map-marked-alt" title="지도" links={[{ target: 'booths', title: '플레이스 관리' }, { target: 'map-settings', title: '지도 설정' }]} open={openMenus[0]} onToggle={() => handleSubMenuToggle(0)} sidebarOpen={open} />
                <SubMenu icon="fa-bullhorn" title="소식" links={[{ target: 'notices', title: '공지 사항' }, { target: 'lost-found', title: '분실물' }, { target: 'qna', title: 'QnA' }]} open={openMenus[1]} onToggle={() => handleSubMenuToggle(1)} sidebarOpen={open} />
            </nav>
        </aside>
    );
};

export default Sidebar;

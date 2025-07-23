import React, { useState, useRef, useContext } from 'react';
import { usePage } from '../../hooks/usePage';
import { ModalContext } from '../../contexts/ModalContext';

const Sidebar = () => {
    const { page, setPage } = usePage();
    const { openModal } = useContext(ModalContext);

    // SubMenu가 몇 개인지에 따라 open 상태 배열 생성 (지도, 소식: 2개)
    const [openMenus, setOpenMenus] = useState([false, false]);

    const handleNav = (targetPage) => setPage(targetPage);

    // openMenus의 특정 인덱스만 토글
    const handleSubMenuToggle = (idx) => {
        setOpenMenus((prev) => {
            const next = [...prev];
            next[idx] = !next[idx];
            return next;
        });
    };

    const NavLink = ({ target, icon, children }) => (
        <a href="#" onClick={(e) => { e.preventDefault(); handleNav(target); }} className={`sidebar-link flex items-center py-3 px-4 rounded-lg transition duration-200 hover:bg-gray-700 hover:text-white ${page === target ? 'active' : 'text-gray-600'}`}><i className={`fas ${icon} w-6 text-gray-500`}></i> {children}</a>
    );

    // open, onToggle을 props로 받도록 수정
    const SubMenu = ({ icon, title, links, open, onToggle }) => {
        const isActive = links.some(l => l.target === page);
        const contentRef = useRef(null);
        // const [maxHeight, setMaxHeight] = useState('0px'); // 제거

        // useEffect(() => { ... }); // 제거

        return (
            <div className="relative">
                <a href="#" onClick={e => { e.preventDefault(); onToggle(); }} className={`sidebar-link flex items-center justify-between py-3 px-4 rounded-lg transition duration-200 hover:bg-gray-700 hover:text-white cursor-pointer ${isActive ? 'active' : 'text-gray-600'}`}>                    <div className="flex items-center"><i className={`fas ${icon} w-6 text-gray-500`}></i> {title}</div>
                    <i className={`fas fa-chevron-down text-xs transition-transform duration-300 ${open ? 'rotate-180' : ''}`}></i>
                </a>
                <div
                    ref={contentRef}
                    style={{
                        maxHeight: open ? (contentRef.current ? contentRef.current.scrollHeight + 'px' : 'none') : '0px',
                        overflow: 'hidden',
                        transition: 'none', // 애니메이션 제거
                        opacity: open ? 1 : 0, // 필요시 opacity도 즉시 적용
                    }}
                    className={"mt-2 pl-8 space-y-1"}
                >
                    {links.map(link => (
                        <a
                            key={link.target}
                            href="#"
                            onClick={(e) => {
                                e.preventDefault();
                                e.stopPropagation(); // 부모 onClick 방지!
                                handleNav(link.target);
                            }}
                            className={`sidebar-link sub-link block py-2 px-2 rounded-lg transition duration-200 hover:bg-indigo-500 hover:text-white ${page === link.target ? 'active' : 'text-gray-500'}`}
                        >
                            {link.title}
                        </a>
                    ))}
                </div>
            </div>
        );
    };

    return (
        <aside className="w-64 bg-gray-50 p-6 shrink-0 flex flex-col border-r border-gray-200">
            <div className="flex flex-col mb-4 mt-2 shrink-0">
                <div className="flex items-center">
                    <h1 className="text-xl font-bold cursor-pointer" onClick={() => setPage('dashboard')}>Festabook</h1>
                    <button
                        className="ml-2 text-gray-500 hover:text-gray-800 focus:outline-none"
                        title="Organization 변경"
                        onClick={() => openModal('organization')}
                    >
                        <i className="fas fa-lock text-lg" />
                    </button>
                </div>
                <div className="w-full h-px bg-gray-200 mt-3" />
            </div>
            <nav className="flex flex-col space-y-2">
                <NavLink target="dashboard" icon="fa-tachometer-alt">대시보드</NavLink>
                <NavLink target="home" icon="fa-home">홈</NavLink>
                <NavLink target="schedule" icon="fa-calendar-alt">일정</NavLink>
                <SubMenu icon="fa-map-marked-alt" title="지도" links={[{ target: 'booths', title: '플레이스 관리' }, { target: 'map-settings', title: '지도 설정' }]} open={openMenus[0]} onToggle={() => handleSubMenuToggle(0)} />
                <SubMenu icon="fa-bullhorn" title="소식" links={[{ target: 'notices', title: '공지 사항' }, { target: 'lost-found', title: '분실물' }, { target: 'qna', title: 'QnA' }]} open={openMenus[1]} onToggle={() => handleSubMenuToggle(1)} />
            </nav>
        </aside>
    );
};

export default Sidebar;

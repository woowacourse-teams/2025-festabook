export const initialData = {
    notices: [
        { id: 1, title: '2025 대학 축제에 오신 것을 환영합니다!', content: '축제 기간 동안 다양한 이벤트와 공연이 준비되어 있으니 마음껏 즐겨주세요!', date: '2025-07-18', pinned: true },
        { id: 2, title: '분실물 센터 안내', content: '분실물은 학생회관 101호에서 보관하고 있습니다.', date: '2025-07-17', pinned: true },
        { id: 3, title: '푸드트럭 존 운영 시간 변경 안내', content: '금일 우천 예보로 인해 푸드트럭 존 운영이 1시간 단축됩니다.', date: '2025-07-18', pinned: false },
    ],
    lostItems: [
        { id: 1, name: '검정색 카드지갑', location: '대운동장 스탠드', date: '2025-07-18', photo: 'https://placehold.co/100x100/e0e0e0/757575?text=Photo', status: '보관중' },
        { id: 2, name: '에어팟 프로', location: '본관 화장실', date: '2025-07-17', photo: 'https://placehold.co/100x100/e2e8f0/4a5568?text=Photo', status: '인계완료' },
    ],
    qnaItems: [
        { id: 1, question: '초청가수 공연은 몇시부터 하나요?', answer: '안녕하세요! 초청가수 공연은 금일 21시부터 대운동장에서 시작될 예정입니다. 많은 관심 부탁드립니다.' },
        { id: 2, question: '흡연구역은 어디에 있나요?', answer: '흡연구역은 학생회관 뒤편과 공대 3호관 옆에 마련되어 있습니다. 지정된 장소에서만 흡연 부탁드립니다.' },
        { id: 3, question: '주점은 외부인도 이용 가능한가요?', answer: '네, 가능합니다. 다만, 주류 구매 시에는 신분증 확인이 필요할 수 있습니다.' },
        { id: 4, question: '축제 기간 동안 도서관은 운영하나요?', answer: '축제 기간 동안 도서관은 단축 운영됩니다. 자세한 시간은 도서관 홈페이지 공지를 확인해주세요.' },
        { id: 5, question: '주차는 어디에 할 수 있나요?', answer: '방문객 주차는 정문 지하주차장을 이용해주시기 바랍니다. 주차 공간이 협소하니 가급적 대중교통 이용을 부탁드립니다.' },
        { id: 6, question: '응급 의료 지원 부스는 어디에 있나요?', answer: '의료 지원 부스는 학생회관 1층 로비에 마련되어 있습니다.' },
        { id: 7, question: '화장실은 어디에 있나요?', answer: '각 건물 1층 및 축제 행사장 곳곳에 이동식 화장실이 비치되어 있습니다.' },
    ],
    schedule: {
        "2025-07-18": [
            { id: 1, startTime: "14:00", endTime: "16:00", title: "동아리 연합 공연", location: "소극장", status: '종료' },
            { id: 2, startTime: "16:00", endTime: "17:00", title: "총학생회장단 인사", location: "대운동장 특설무대", status: '종료' },
            { id: 3, startTime: "18:00", endTime: "21:00", title: "학과별 주점 운영", location: "캠퍼스 전역", status: '진행중' },
            { id: 4, startTime: "21:00", endTime: "22:00", title: "초청가수 공연: 아이브", location: "대운동장 특설무대", status: '예정' }
        ],
        "2025-07-19": [
            { id: 5, startTime: "13:00", endTime: "17:00", title: "플리마켓 & 체험부스", location: "학생회관 앞", status: '예정' },
        ]
    },
    booths: [
        { id: 1, editKey: 'key123', title: '컴퓨터공학과 주점', category: 'BAR', description: '시원한 맥주와 맛있는 파전!', images: ['https://placehold.co/400x300/e2e8f0/4a5568?text=Image+1'], mainImageIndex: 0, location: '학생회관 앞', host: '컴퓨터공학과 학생회', startTime: '18:00', endTime: '23:00', notices: [{id: 1, text: '오늘의 추천 메뉴: 해물파전!'}], coords: null },
        { id: 2, editKey: 'key456', title: '타코야끼 푸드트럭', category: 'FOOD_TRUCK', description: '오사카 정통 타코야끼', images: ['https://placehold.co/400x300/e2e8f0/4a5568?text=Image+1', 'https://placehold.co/400x300/e2e8f0/4a5568?text=Image+2', 'https://placehold.co/400x300/e2e8f0/4a5568?text=Image+3'], mainImageIndex: 1, location: '농구장 옆', host: '타코야끼 사장님', startTime: '12:00', endTime: '22:00', notices: [], coords: null },
        { id: 3, editKey: 'key789', title: '중앙 흡연구역', category: 'SMOKING', description: '학생회관 뒤편에 위치해있습니다.', images: ['https://placehold.co/400x300/cbd5e1/475569?text=Image+1'], mainImageIndex: 0, location: '학생회관 뒤편', host: '총학생회', startTime: '00:00', endTime: '23:59', notices: [], coords: null },
        { id: 4, editKey: 'key101', title: '페이스페인팅 부스', category: 'BOOTH', description: '무료로 예쁜 그림을 그려드려요!', images: [], mainImageIndex: -1, location: '도서관 앞', host: '미술 동아리', startTime: '13:00', endTime: '17:00', notices: [{id: 1, text: '재료 소진 시 조기 마감될 수 있습니다.'}], coords: null },
        { id: 5, editKey: 'key112', title: '경영학과 주점', category: 'BAR', description: '칵테일과 함께하는 즐거운 밤!', images: ['https://placehold.co/400x300/fecaca/991b1b?text=Image+1', 'https://placehold.co/400x300/fecaca/991b1b?text=Image+2'], mainImageIndex: 0, location: '중앙 광장', host: '경영학과 학생회', startTime: '18:00', endTime: '00:00', notices: [{id: 1, text: '신분증 미지참 시 주류 구매가 불가합니다.'}, {id: 2, text: '외부 음식 반입 금지'}], coords: null },
        { id: 6, editKey: 'key131', title: '분리수거 쓰레기통', category: 'TRASH_CAN', description: '깨끗한 축제를 위해 분리수거에 동참해주세요.', images: [], mainImageIndex: -1, location: '각 건물 입구', host: '총학생회', startTime: '00:00', endTime: '23:59', notices: [], coords: null },
    ],
};

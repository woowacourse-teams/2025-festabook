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
};

export const initialData = {
    lostItemGuide: '분실물을 발견하신 경우 가까운 운영본부 또는 안내 데스크에 제출해주세요. 수령을 원하시는 분은 신분증을 지참하시고 보관 장소로 방문해 주세요.',
    notices: [
        { id: 1, title: '2025 대학 축제에 오신 것을 환영합니다!', content: '축제 기간 동안 다양한 이벤트와 공연이 준비되어 있으니 마음껏 즐겨주세요!', date: '2025-07-18', pinned: true },
        { id: 2, title: '분실물 센터 안내', content: '분실물은 학생회관 101호에서 보관하고 있습니다.', date: '2025-07-17', pinned: true },
        { id: 3, title: '푸드트럭 존 운영 시간 변경 안내', content: '금일 우천 예보로 인해 푸드트럭 존 운영이 1시간 단축됩니다.', date: '2025-07-18', pinned: false },
    ],
    lostItems: [
        { id: 1, storageLocation: '대운동장 스탠드', createdAt: '2025-07-20', imageUrl: 'https://img1.daumcdn.net/thumb/R720x0.q80/?scode=mtistory2&fname=https%3A%2F%2Ft1.daumcdn.net%2Fcfile%2Ftistory%2F1576494B4EE4AB8320', pickupStatus: 'PENDING' },
        { id: 2, storageLocation: '본관 화장실', createdAt: '2025-07-17', imageUrl: 'https://media.themoviedb.org/t/p/w500/fpDesSzDRMFGIJbcuC2WVCveC2P.jpg', pickupStatus: 'COMPLETED' },
        { id: 3, storageLocation: '대운동장 스탠드', createdAt: '2025-07-18', imageUrl: 'https://image.tmdb.org/t/p/w1280/ndDYIa9VXFbWpC2pWj9j5OaZsfE.jpg', pickupStatus: 'PENDING' },
        { id: 4, storageLocation: '본관 화장실', createdAt: '2025-07-17', imageUrl: 'https://image.tving.com/ntgs/contents/CTC/caip/CAIP0900/ko/20230303/P001700944.jpg/dims/resize/480', pickupStatus: 'COMPLETED' },
        { id: 5, storageLocation: '대운동장 스탠드', createdAt: '2025-07-21', imageUrl: 'https://image.tmdb.org/t/p/original/5AijOLYBqFbkAsmYM1Mv8D3i63F.jpg', pickupStatus: 'PENDING' },
        { id: 6, storageLocation: '본관 화장실', createdAt: '2025-07-22', imageUrl: 'https://www.themoviedb.org/t/p/w1280/wwuFdCsFbcOGPdt1UEcadHxA1Gd.jpg', pickupStatus: 'COMPLETED' },
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

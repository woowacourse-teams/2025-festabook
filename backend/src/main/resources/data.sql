-- ========================
-- 1. 조직 (Organization)
-- ========================
INSERT INTO organization (name, zoom, latitude, longitude)
VALUES ('서울시립대학교', 15, 37.583585, 127.0588862);

INSERT INTO organization_polygon_hole_boundary (organization_id, latitude, longitude)
VALUES (1, 37.5850814, 127.0593583),
       (1, 37.5844352, 127.0555388),
       (1, 37.5805582, 127.0573842),
       (1, 37.5811704, 127.0600879),
       (1, 37.5826668, 127.0630061),
       (1, 37.5846053, 127.0639073),
       (1, 37.5853875, 127.0619761);

-- ========================
-- 2. 일정 날짜 (EventDate)
-- ========================
INSERT INTO event_date (organization_id, date)
VALUES (1, CURRENT_DATE - 1),
       (1, CURRENT_DATE),
       (1, CURRENT_DATE + 1);

-- ========================
-- 3. 타임라인 (Event)
-- ========================

INSERT INTO event (start_time, end_time, title, location, event_date_id)
VALUES ('10:00:00', '11:30:00', '개막식', '운동장', 1),
       ('13:00:00', '14:00:00', '동아리 공연', '야외무대', 1),
       ('14:30:00', '15:30:00', '코스프레 퍼레이드', '본관 앞', 1),
       ('16:00:00', '17:00:00', '전시회 오픈', '미술관', 1),
       ('18:00:00', '19:00:00', '개발자 토크콘서트', 'IT관', 1),
       ('19:30:00', '20:30:00', '야간 영화 상영', '운동장', 1);

INSERT INTO event (start_time, end_time, title, location, event_date_id)
VALUES ('10:00:00', '11:00:00', '오케스트라 공연', '음악홀', 2),
       ('11:00:00', '12:00:00', '버스킹 공연', '중앙광장', 2),
       ('12:30:00', '13:30:00', '문학 낭독회', '도서관 앞', 2),
       ('14:00:00', '15:30:00', '먹거리 페스티벌', '푸드트럭 존', 2),
       ('16:00:00', '17:00:00', '사진전 관람', '아트센터', 2),
       ('17:30:00', '18:30:00', '유튜버 팬미팅', '강의동 B103', 2),
       ('19:00:00', '20:00:00', '마술 공연', '강당', 2),
       ('20:30:00', '22:00:00', '야간 EDM 파티', '운동장', 2);

INSERT INTO event (start_time, end_time, title, location, event_date_id)
VALUES ('09:30:00', '10:30:00', '플리마켓', '체육관 앞', 3),
       ('11:00:00', '12:00:00', '창업 아이디어 경진대회', '창업카페', 3),
       ('12:30:00', '13:30:00', '청춘 토크쇼', '대강당', 3),
       ('14:00:00', '15:00:00', '보드게임 대회', '학생회관 2층', 3),
       ('15:30:00', '16:00:00', '퀴즈쇼 결승전', '야외무대', 3),
       ('16:00:00', '17:00:00', '폐막식', '강당', 3),
       ('17:30:00', '18:30:00', '무대 뒤 인터뷰', '무대 대기실', 3),
       ('19:00:00', '20:30:00', '대형 불꽃놀이', '운동장', 3),
       ('21:00:00', '22:00:00', '아티스트 엔딩 무대', '운동장', 3);

-- ========================
-- 4. 전체 공지사항 (Announcement)
-- ========================
INSERT INTO announcement (title, content, is_pinned, organization_id, created_at)
VALUES ('페스타북 축제에 오신 것을 환영합니다!', '3일간의 즐거움을 함께하세요!', true, 1, '2025-07-16 10:00:00'),
       ('우천 시 대피 안내', '비가 올 경우 모든 야외 행사는 체육관으로 이동합니다.', false, 1, '2025-07-16 11:00:00');

-- ========================
-- 5. 자주 묻는 질문 (QuestionAnswer)
-- ========================
INSERT INTO question_answer (organization_id, title, question, answer, created_at)
VALUES (1, '주차 가능한가요?', '차를 가져가도 될까요?', '동문 주차장을 이용하실 수 있습니다.', '2025-07-16 09:30:00'),
       (1, '음식 판매는 어디서 하나요?', '부스 위치가 궁금해요.', '푸드트럭은 운동장 오른쪽 라인에 위치합니다.', '2025-07-16 09:40:00');

-- ========================
-- 6. 플레이스 (Place)
-- ========================
INSERT INTO place (organization_id, category, latitude, longitude)
VALUES (1, 'FOOD_TRUCK', 37.5837, 127.0592),
       (1, 'BOOTH', 37.5842, 127.0569),
       (1, 'BOOTH', 37.5815, 127.0611),
       (1, 'BAR', 37.5821, 127.0583),
       (1, 'BOOTH', 37.5853, 127.0627),
       (1, 'FOOD_TRUCK', 37.5827, 127.0609),
       (1, 'BOOTH', 37.5848, 127.0577),
       (1, 'BOOTH', 37.5819, 127.0589);


INSERT INTO place_detail (place_id, title, description, location, host, start_time, end_time)
VALUES (1, '핫도그 푸드트럭', '즉석에서 튀긴 바삭한 핫도그', '푸드존 A', '맛있는핫도그', '10:00:00', '20:00:00'),
       (2, '타로 점 부스', '미래가 궁금한 당신을 위한 점괘 부스', '부스 B-2', '타로동아리', '10:00:00', '20:00:00'),
       (3, '수공예 마켓', '학생들의 수공예 작품 전시 및 판매', '부스 A-1', '수공예동아리', '10:00:00', '20:00:00'),
       (4, '야외 바', '시원한 음료와 간단한 안주 제공', '푸드존 B', '칵테일클럽', '10:00:00', '20:00:00'),
       (5, '사진 스튜디오', '기념 촬영 부스', '포토존', '사진부', '10:00:00', '20:00:00'),
       (6, '빙수 푸드트럭', '시원한 과일 빙수 판매', '푸드존 C', '빙수친구들', '10:00:00', '20:00:00'),
       (7, '페이스 페인팅', '얼굴에 그림 그려주는 체험 부스', '체험존 A', '미술동아리', '10:00:00', '20:00:00'),
       (8, 'VR 게임존', 'VR 체험 부스', '체험존 B', '게임동아리', '10:00:00', '20:00:00');

-- ========================
-- 7. 플레이스 공지사항 (PlaceAnnouncement)
-- ========================
INSERT INTO place_announcement (place_id, title, content, created_at)
VALUES (1, '이벤트 참여 방법', '줄서기 및 거리두기를 지켜주세요.', '2025-07-16 12:01:00'),
       (1, '운영 시간 변경 안내', '오늘 준비된 수량이 모두 소진되었습니다.', '2025-07-16 12:02:00'),
       (1, '운영 시간 변경 안내', '더운 날씨로 인한 잠시 휴식이 예정되어 있습니다.', '2025-07-16 12:03:00'),
       (1, '이벤트 참여 방법', '더운 날씨로 인한 잠시 휴식이 예정되어 있습니다.', '2025-07-16 12:04:00'),
       (2, '운영 시간 변경 안내', '오늘 준비된 수량이 모두 소진되었습니다.', '2025-07-16 12:04:00'),
       (2, '이벤트 참여 방법', 'SNS 인증 시 기념품 제공됩니다.', '2025-07-16 12:10:00'),
       (3, '물품 소진 안내', '오늘 준비된 수량이 모두 소진되었습니다.', '2025-07-16 12:03:00'),
       (3, '주의 사항', '오늘 준비된 수량이 모두 소진되었습니다.', '2025-07-16 12:06:00'),
       (4, '운영 시간 변경 안내', '내부 사정으로 인해 시간 변경이 있습니다.', '2025-07-16 12:04:00'),
       (4, '물품 소진 안내', '내부 사정으로 인해 시간 변경이 있습니다.', '2025-07-16 12:12:00'),
       (5, '이벤트 참여 방법', '오늘 준비된 수량이 모두 소진되었습니다.', '2025-07-16 12:10:00'),
       (5, '운영 시간 변경 안내', 'SNS 인증 시 기념품 제공됩니다.', '2025-07-16 12:25:00'),
       (6, '임시 휴식 시간', '오늘 준비된 수량이 모두 소진되었습니다.', '2025-07-16 12:06:00'),
       (7, '임시 휴식 시간', 'SNS 인증 시 기념품 제공됩니다.', '2025-07-16 12:07:00'),
       (7, '물품 소진 안내', 'SNS 인증 시 기념품 제공됩니다.', '2025-07-16 12:21:00'),
       (8, '주의 사항', '더운 날씨로 인한 잠시 휴식이 예정되어 있습니다.', '2025-07-16 12:08:00'),
       (8, '물품 소진 안내', '더운 날씨로 인한 잠시 휴식이 예정되어 있습니다.', '2025-07-16 12:24:00');

-- ========================
-- 8. 플레이스 이미지 (PlaceImage)
-- ========================
INSERT INTO place_image (place_id, image_url, sequence)
VALUES (1, 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?auto=format&fit=crop&w=500&q=80', 1),
       (1, 'https://images.unsplash.com/photo-1464306076886-debca5e8a6b0?auto=format&fit=crop&w=500&q=80', 2),
       (2, 'https://images.unsplash.com/photo-1519125323398-675f0ddb6308?auto=format&fit=crop&w=500&q=80', 1),
       (2, 'https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=500&q=80', 2),
       (2, 'https://images.unsplash.com/photo-1519710164239-da123dc03ef4?auto=format&fit=crop&w=500&q=80', 3),
       (3, 'https://images.unsplash.com/photo-1515378791036-0648a3ef77b2?auto=format&fit=crop&w=500&q=80', 1),
       (3, 'https://images.unsplash.com/photo-1503602642458-232111445657?auto=format&fit=crop&w=500&q=80', 2),
       (4, 'https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=500&q=80', 1),
       (4, 'https://images.unsplash.com/photo-1520880867055-1e30d1cb001c?auto=format&fit=crop&w=500&q=80', 2),
       (4, 'https://images.unsplash.com/photo-1464306076886-debca5e8a6b0?auto=format&fit=crop&w=500&q=80', 3),
       (5, 'https://images.unsplash.com/photo-1454023492550-5696f8ff10e1?auto=format&fit=crop&w=500&q=80', 1),
       (5, 'https://images.unsplash.com/photo-1465101046530-73398c7f28ca?auto=format&fit=crop&w=500&q=80', 2),
       (6, 'https://images.unsplash.com/photo-1502741338009-cac2772e18bc?auto=format&fit=crop&w=500&q=80', 1),
       (7, 'https://images.unsplash.com/photo-1519125323398-675f0ddb6308?auto=format&fit=crop&w=500&q=80', 1),
       (7, 'https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=500&q=80', 2),
       (7, 'https://images.unsplash.com/photo-1454023492550-5696f8ff10e1?auto=format&fit=crop&w=500&q=80', 3),
       (8, 'https://images.unsplash.com/photo-1519125323398-675f0ddb6308?auto=format&fit=crop&w=500&q=80', 1),
       (8, 'https://images.unsplash.com/photo-1465101046530-73398c7f28ca?auto=format&fit=crop&w=500&q=80', 2),
       (8, 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?auto=format&fit=crop&w=500&q=80', 3),
       (8, 'https://images.unsplash.com/photo-1464306076886-debca5e8a6b0?auto=format&fit=crop&w=500&q=80', 4),
       (8, 'https://images.unsplash.com/photo-1519710164239-da123dc03ef4?auto=format&fit=crop&w=500&q=80', 5);

-- ========================
-- 9. 디바이스 (Device)
-- ========================
INSERT INTO device (device_identifier, fcm_token)
VALUES ('android-uuid-1234', 'fcm-token-1234'),
       ('android-uuid-5678', 'fcm-token-5678');

-- ========================
-- 10. 플레이스 북마크 (PlaceBookmark)
-- ========================
INSERT INTO place_bookmark (place_id, device_id)
VALUES (1, 1),
       (2, 1),
       (3, 2),
       (4, 2);

-- ========================
-- 11. 조직 북마크 (OrganizationBookmark)
-- ========================
INSERT INTO organization_bookmark (organization_id, device_id)
VALUES (1, 1),
       (1, 2);

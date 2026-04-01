-- --------------------------------------------------
-- users
-- --------------------------------------------------
INSERT INTO users (email, password, nickname, role)
VALUES 
('admin@medireview.com', 'encoded_admin_password', 'admin', 'ADMIN'),
('user1@medireview.com', 'encoded_user1_password', 'alice', 'USER'),
('user2@medireview.com', 'encoded_user2_password', 'bob', 'USER'),
('user3@medireview.com', 'encoded_user3_password', 'charlie', 'USER');

-- --------------------------------------------------
-- hospitals
-- --------------------------------------------------
INSERT INTO hospitals (name, address, phone, category, status)
VALUES
('서울정형외과', '서울특별시 강남구 테헤란로 101', '02-1111-1111', '정형외과', 'ACTIVE'),
('연세내과의원', '서울특별시 서초구 서초대로 202', '02-2222-2222', '내과', 'ACTIVE'),
('밝은피부과', '서울특별시 송파구 올림픽로 303', '02-3333-3333', '피부과', 'ACTIVE');

-- --------------------------------------------------
-- departments
-- --------------------------------------------------
INSERT INTO departments (name)
VALUES
('정형외과'),
('내과'),
('피부과'),
('소아청소년과'),
('이비인후과');

-- --------------------------------------------------
-- doctors
-- --------------------------------------------------
INSERT INTO doctors (hospital_id, department_id, name, intro, status)
VALUES
(1, 1, '김정형', '무릎/어깨 관절 진료를 전문으로 합니다.', 'ACTIVE'),
(1, 1, '박관절', '척추 및 관절 통증 치료 경험이 풍부합니다.', 'ACTIVE'),
(2, 2, '이내과', '고혈압, 당뇨 등 만성질환 진료를 담당합니다.', 'ACTIVE'),
(3, 3, '최피부', '여드름 및 피부질환 진료를 전문으로 합니다.', 'ACTIVE');

-- --------------------------------------------------
-- reviews
-- --------------------------------------------------
INSERT INTO reviews (
    doctor_id, user_id, rating, bedside_manner, explanation, wait_time, revisit_intention, content
)
VALUES
(1, 2, 5, 5, 5, 4, 5, '정말 친절하고 설명도 자세해서 좋았습니다.'),
(1, 3, 4, 4, 5, 3, 4, '무릎 통증으로 방문했는데 설명이 이해하기 쉬웠어요.'),
(2, 2, 4, 4, 4, 4, 4, '대기가 길지 않았고 전반적으로 만족했습니다.'),
(3, 3, 5, 5, 4, 5, 5, '만성질환 상담을 꼼꼼하게 해주셔서 신뢰가 갔습니다.'),
(4, 4, 3, 4, 3, 3, 3, '진료는 무난했지만 대기 시간이 조금 있었습니다.'),
(1, 2, 5, 5, 5, 5, 5, '재방문했는데 이번에도 만족스러웠습니다.');

-- --------------------------------------------------
-- hospital_requests
-- --------------------------------------------------
INSERT INTO hospital_requests (
    requester_user_id, name, address, phone, category, status, approved_hospital_id, rejection_reason, approved_at
)
VALUES
(2, '강남소아과의원', '서울특별시 강남구 역삼로 404', '02-4444-4444', '소아청소년과', 'PENDING', NULL, NULL, NULL),
(3, '서울정형외과', '서울특별시 강남구 테헤란로 101', '02-1111-1111', '정형외과', 'REJECTED', NULL, '이미 등록된 병원입니다.', NULL),
(4, '튼튼이비인후과', '서울특별시 마포구 월드컵로 505', '02-5555-5555', '이비인후과', 'APPROVED', 1, NULL, '2026-03-30 14:00:00');

-- --------------------------------------------------
-- doctor_requests
-- --------------------------------------------------
INSERT INTO doctor_requests (
    requester_user_id, hospital_id, department_id, name, intro, status, approved_doctor_id, rejection_reason, approved_at
)
VALUES
(2, 1, 1, '한정형', '관절 및 스포츠 손상 진료를 전문으로 합니다.', 'PENDING', NULL, NULL, NULL),
(3, 2, 2, '이내과', '고혈압 및 당뇨 진료 경험이 많습니다.', 'REJECTED', NULL, '이미 등록된 의사입니다.', NULL),
(4, 3, 3, '윤피부', '피부 트러블 및 알레르기 진료를 담당합니다.', 'APPROVED', 4, NULL, '2026-03-30 15:00:00');
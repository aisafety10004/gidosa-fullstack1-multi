-- 건설현장관리 더미데이터
insert into `constructions`(location, name, status, start_date, end_date) values ('A 건설공사', '홍길동', 'stop', now(), now());
--insert into `construction`(location, name, status, start_date, end_date) values ('A 건설공사', '홍길동', 'stop', now(), now())^;
insert into `constructions`(location, name, status, start_date, end_date) values ('B 건설공사', '유관순', 'pending', now(), now());

-- 어드민 및 매니저 계정 추가
insert into member_admin (username, password, name, email, phone, location, role, is_active, created_at, updated_at)
values
('admin', '$2a$10$PifVL8yNHfm6x48bZQwgF.VW79vPIZSBSgk4nRrH7up7vuXx/2Dvq', '관리자1', 'admin1@example.com', '010-1234-5678', '서울', 'ROLE_ADMIN', true, NOW(), NOW()),
('manager1', '$2a$10$PifVL8yNHfm6x48bZQwgF.VW79vPIZSBSgk4nRrH7up7vuXx/2Dvq', '매니저1', 'manager1@example.com', '010-1234-5678', '서울', 'ROLE_MANAGER', true, NOW(), NOW()),
('manager2', '$2a$10$PifVL8yNHfm6x48bZQwgF.VW79vPIZSBSgk4nRrH7up7vuXx/2Dvq', '매니저2', 'manager2@example.com', '010-2345-6789', '부산', 'ROLE_MANAGER', true, NOW(), NOW());

-- 일반 계정 추가
insert into member_general (username, password, name, email, phone, location, role, created_at, updated_at, construction_id)
values
('test1', '$2a$10$PifVL8yNHfm6x48bZQwgF.VW79vPIZSBSgk4nRrH7up7vuXx/2Dvq', '홍길동', 'test@example.com', '010-1234-5678', '서울', 'ROLE_USER', NOW(), NOW(), 1),
('test2', '$2a$10$PifVL8yNHfm6x48bZQwgF.VW79vPIZSBSgk4nRrH7up7vuXx/2Dvq', '강감찬', 'test2@example.com', '010-1234-5678', '서울', 'ROLE_USER', NOW(), NOW(), 1),
('test3', '$2a$10$PifVL8yNHfm6x48bZQwgF.VW79vPIZSBSgk4nRrH7up7vuXx/2Dvq', '유관순', 'test3@example.com', '010-2345-6789', '부산', 'ROLE_USER', NOW(), NOW(), 2);

-- 관할구분(후입력) 데이터
insert into master_authority_division(name) values ('경기교육지원청');
insert into master_authority_division(name) values ('수원교육지원청');

-- 기관명(후입력) 데이터
insert into master_division_detail(name) values ('수원교육지원청');
insert into master_division_detail(name) values ('가온누리유치원');
insert into master_division_detail(name) values ('꿈누리유치원');
insert into master_division_detail(name) values ('능실유치원');
insert into master_division_detail(name) values ('수원중학교');
insert into master_division_detail(name) values ('수원고등학교');
insert into master_division_detail(name) values ('장안중학교');
insert into master_division_detail(name) values ('장안초등학교');
insert into master_division_detail(name) values ('매교초등학교');
insert into master_division_detail(name) values ('매교중학교');
insert into master_division_detail(name) values ('매교고등학교');



-- 건설공사현장
drop table if exists `constructions`
-- ^;
;
CREATE TABLE `constructions` (
	`id` BIGINT(19) NOT NULL AUTO_INCREMENT,
	`created_at` DATETIME(6) NULL DEFAULT NULL,
	`description` VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8mb4_unicode_ci',
	`end_date` DATETIME(6) NULL DEFAULT NULL,
	`location` VARCHAR(255) NOT NULL COLLATE 'utf8mb4_unicode_ci',
	`name` VARCHAR(255) NOT NULL COLLATE 'utf8mb4_unicode_ci',
	`start_date` DATETIME(6) NULL DEFAULT NULL,
	`status` VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8mb4_unicode_ci',
	`updated_at` DATETIME(6) NULL DEFAULT NULL,
	PRIMARY KEY (`id`) USING BTREE
)
COLLATE='utf8mb4_unicode_ci'
ENGINE=InnoDB
-- ^;
;

-- -- 관리자 메뉴 테이블
-- CREATE TABLE IF NOT EXISTS admin_menu (
--     id BIGINT AUTO_INCREMENT PRIMARY KEY,
--     name VARCHAR(100) NOT NULL,
--     url VARCHAR(255) NOT NULL,
--     icon VARCHAR(100),
--     parent_id BIGINT,
--     sort_order INT DEFAULT 0,
--     is_active BOOLEAN DEFAULT TRUE,
--     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
--     FOREIGN KEY (parent_id) REFERENCES admin_menu(id) ON DELETE CASCADE
-- );

-- -- 관리자 권한 테이블
-- CREATE TABLE IF NOT EXISTS admin_role (
--     id BIGINT AUTO_INCREMENT PRIMARY KEY,
--     name VARCHAR(100) NOT NULL,
--     description VARCHAR(255),
--     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
-- );

-- -- Notice 테이블에 construction_id 컬럼 추가
-- ALTER TABLE notice ADD COLUMN construction_id BIGINT NULL;
-- ALTER TABLE notice ADD CONSTRAINT fk_notice_construction FOREIGN KEY (construction_id) REFERENCES construction(id);

-- Notice 테이블에 mermaidCode 컬럼 추가
-- ALTER TABLE notice ADD COLUMN mermaid_code TEXT NULL;

-- 자동 로그인(remember-me) 기능을 위한 테이블
create table persistent_logins (
	username varchar(64) not null,
	series varchar(64) primary key,
	token varchar(64) not null,
	last_used timestamp not null
);



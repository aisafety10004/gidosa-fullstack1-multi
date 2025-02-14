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





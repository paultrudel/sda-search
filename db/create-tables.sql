DROP SCHEMA IF EXISTS `sda-search`;
CREATE SCHEMA `sda-search`;
USE `sda-search`;

CREATE TABLE IF NOT EXISTS `sda-search`.`sda_document` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`url` VARCHAR(1023) DEFAULT NULL,
	`title` VARCHAR(1023) DEFAULT NULL,
	`document_summary` MEDIUMTEXT DEFAULT NULL,
	`page_rank` DECIMAL(65, 30) DEFAULT NULL,
	`parent_id` BIGINT DEFAULT NULL,
	PRIMARY KEY (`id`)
)
ENGINE=InnoDB
AUTO_INCREMENT = 1;

CREATE TABLE IF NOT EXISTS `sda-search`.`document_heading` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`text` VARCHAR(1023) DEFAULT NULL,
	`document_id` BIGINT NOT NULL,
	PRIMARY KEY (`id`),
	KEY `fk_heading_document` (`document_id`),
	CONSTRAINT `fk_heading_document` FOREIGN KEY (`document_id`) REFERENCES `sda_document` (`id`)
)
ENGINE=InnoDB
AUTO_INCREMENT = 1;

CREATE TABLE IF NOT EXISTS `sda-search`.`document_paragraph` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`text` MEDIUMTEXT DEFAULT NULL,
	`document_id` BIGINT NOT NULL,
	PRIMARY KEY (`id`),
	KEY `fk_paragraph_document` (`document_id`),
	CONSTRAINT `fk_paragraph_document` FOREIGN KEY (`document_id`) REFERENCES `sda_document` (`id`)
)
ENGINE=InnoDB
AUTO_INCREMENT = 1;

CREATE TABLE IF NOT EXISTS `sda-search`.`document_link` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`text` VARCHAR(1023) DEFAULT NULL,
	`document_id` BIGINT NOT NULL,
	PRIMARY KEY (`id`),
	KEY `fk_link_document` (`document_id`),
	CONSTRAINT `fk_link_document` FOREIGN KEY (`document_id`) REFERENCES `sda_document` (`id`)
)
ENGINE=InnoDB
AUTO_INCREMENT = 1;

CREATE TABLE IF NOT EXISTS `sda-search`.`document_metadata` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(1023) DEFAULT NULL,
	`content` MEDIUMTEXT DEFAULT NULL,
	`document_id` BIGINT NOT NULL,
	PRIMARY KEY (`id`),
	KEY `fk_metadata_document` (`document_id`),
	CONSTRAINT `fk_metadata_document` FOREIGN KEY (`document_id`) REFERENCES `sda_document` (`id`)
)
ENGINE=InnoDB
AUTO_INCREMENT = 1;

CREATE TABLE IF NOT EXISTS `sda-search`.`document_image` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`link` VARCHAR(1023) DEFAULT NULL,
	`alt_text` VARCHAR(1023) DEFAULT NULL,
	`document_id` BIGINT NOT NULL,
	PRIMARY KEY (`id`),
	KEY `fk_image_document` (`document_id`),
	CONSTRAINT `fk_image_document` FOREIGN KEY (`document_id`) REFERENCES `sda_document` (`id`)
)
ENGINE=InnoDB
AUTO_INCREMENT = 1;
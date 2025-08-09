DROP DATABASE IF EXISTS bibliophile;

CREATE DATABASE bibliophile;
USE bibliophile;

CREATE TABLE `t_ai_reader_file`
(
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '文件唯一标识',
    `title`         VARCHAR(255) NOT NULL COMMENT '文件标题',
    `file_name`    VARCHAR(255) NOT NULL COMMENT '原始文件名（含扩展名）',
    `file_size`    BIGINT       NOT NULL COMMENT '文件大小（字节）',
    `ai_processed` TINYINT(1)            DEFAULT 0 COMMENT '是否已完成AI处理（0否1是）',
    `created_at`   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_ai_processed` (`ai_processed`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='AI阅读器文件表';
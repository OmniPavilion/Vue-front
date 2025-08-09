DROP TABLE IF EXISTS `spring_ai_chat_memory`;

CREATE TABLE `spring_ai_chat_memory`
(
    `id`              bigint       NOT NULL AUTO_INCREMENT,
    `conversation_id` varchar(255) NOT NULL,
    `content`         text         NOT NULL,
    `type`            varchar(20)  NOT NULL, -- 缺失的列
    `timestamp`       timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_conversation_id` (`conversation_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
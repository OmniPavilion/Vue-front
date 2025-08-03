DROP DATABASE IF EXISTS article;

CREATE DATABASE article;
USE article;



-- 创建分类表（允许文章不关联分类）
CREATE TABLE t_category
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    name       VARCHAR(50) NOT NULL UNIQUE,
    created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 创建文章表（独立存在，不强制关联分类）
CREATE TABLE t_article
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    title      VARCHAR(255) NOT NULL,
    file_name  VARCHAR(255) NOT NULL,
    weather    VARCHAR(255) NOT NULL,
    written_at  TIMESTAMP    NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 创建可选的分类关联表
CREATE TABLE t_article_category
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    article_id  BIGINT    NOT NULL,
    category_id BIGINT    NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (article_id) REFERENCES t_article (id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES t_category (id) ON DELETE CASCADE,
    UNIQUE KEY (article_id, category_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 插入分类数据（可选）
INSERT INTO t_category (name)
VALUES ('技术文章'),
       ('生活随笔'),
       ('旅行日记'),
       ('读书笔记'),
       ('未分类');
-- 默认分类

# -- 插入文章数据（可不关联分类）
# INSERT INTO t_article (title, file_name, weather, written_at)
# VALUES ('MySQL优化技巧', 'mysql_optimization.md', '晴天', '2023-10-15 09:30:00'),
#        ('春日游记', 'spring_travel.md', '多云', '2023-04-05 14:15:00'),
#        ('《百年孤独》读后感', 'one_hundred_years.md', '阴天', '2023-08-22 20:00:00'),
#        ('Python入门指南', 'python_guide.md', '雨天', '2023-11-10 10:45:00'),
#        ('日常随想', 'daily_thoughts.md', '晴朗', CURRENT_TIMESTAMP);
# -- 不关联分类的文章

# -- 选择性关联分类（不是必须操作）
# INSERT INTO t_article_category (article_id, category_id)
# VALUES (1, 1), -- MySQL优化技巧 -> 技术文章
#        (2, 2), -- 春日游记 -> 生活随笔
#        (2, 3), -- 春日游记 -> 旅行日记（多分类）
#        (3, 4); -- 百年孤独 -> 读书笔记
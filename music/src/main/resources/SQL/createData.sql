DROP DATABASE IF EXISTS music;

CREATE DATABASE IF NOT EXISTS music;
USE music;

-- 创建歌手表 singer
CREATE TABLE t_singer
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_name (name)
);

-- 创建类型表 category
CREATE TABLE t_category
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建音乐表 music
CREATE TABLE t_music
(
    id          INTEGER AUTO_INCREMENT PRIMARY KEY,
    title       TEXT      NOT NULL,
    file_name   TEXT      NOT NULL,
    duration    INTEGER            DEFAULT 0,
    play_count  INTEGER            DEFAULT 0,
    last_played TIMESTAMP NULL,
    singer_id   BIGINT,
    category_id BIGINT,
    created_at DATETIME  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_favorite BOOLEAN            DEFAULT 0,
    updated_at DATETIME  NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    file_size   INTEGER            DEFAULT 0,
    INDEX idx_title (title(255)),
    INDEX idx_last_played (last_played),
    FOREIGN KEY (singer_id) REFERENCES t_singer (id),
    FOREIGN KEY (category_id) REFERENCES t_category (id)
);

-- 1. 添加歌手数据
INSERT INTO t_singer (name)
VALUES ('默认歌手'),
       ('周杰伦'),
       ('林俊杰'),
       ('Taylor Swift'),
       ('陈奕迅'),
       ('邓紫棋');

-- 2. 添加音乐类别数据
INSERT INTO t_category (name)
VALUES ('默认分类'),
       ('流行'),
       ('摇滚'),
       ('R&B'),
       ('电子'),
       ('民谣'),
       ('嘻哈');

-- 3. 添加音乐数据
INSERT INTO t_music (title, file_name, duration, singer_id, category_id, is_favorite, file_size)
VALUES ('七里香', 'qilixiang.mp3', 298, 1, 1, 1, 5200000),
       ('夜曲', 'nocturne.mp3', 213, 1, 3, 1, 3800000),
       ('江南', 'jiangnan.mp3', 245, 2, 1, 0, 4100000),
       ('她说', 'shesaid.mp3', 198, 2, 1, 1, 3400000),
       ('Love Story', 'lovestory.mp3', 235, 3, 1, 1, 4200000),
       ('Blank Space', 'blankspace.mp3', 231, 3, 1, 0, 3900000),
       ('K歌之王', 'ksongking.mp3', 223, 4, 1, 1, 4000000),
       ('富士山下', 'fuji.mp3', 267, 4, 1, 0, 4700000),
       ('泡沫', 'bubble.mp3', 256, 5, 1, 1, 4500000),
       ('光年之外', 'lightyears.mp3', 238, 5, 1, 1, 4100000),
       ('双截棍', 'nunchucks.mp3', 187, 1, 6, 0, 3300000),
       ('曹操', 'caocao.mp3', 203, 2, 2, 0, 3600000);

DROP DATABASE IF EXISTS music;

CREATE DATABASE IF NOT EXISTS music;
USE music;

-- 创建歌手表 singer
CREATE TABLE singer
(
    singer_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    cover_path  VARCHAR(512),
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_name (name)
);

-- 创建类型表 category
CREATE TABLE category
(
    category_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建音乐表 music
CREATE TABLE music
(
    id          INTEGER AUTO_INCREMENT PRIMARY KEY,
    title       TEXT      NOT NULL,
    file_name   TEXT      NOT NULL,
    duration    INTEGER            DEFAULT 0,
    play_count  INTEGER            DEFAULT 0,
    last_played TIMESTAMP NULL,
    singer_id   BIGINT,
    category_id BIGINT,
    create_time DATETIME  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_favorite BOOLEAN            DEFAULT 0,
    update_time DATETIME  NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_title (title(255)),
    INDEX idx_last_played (last_played),
    FOREIGN KEY (singer_id) REFERENCES singer (singer_id),
    FOREIGN KEY (category_id) REFERENCES category (category_id)
);

-- 1. 添加歌手数据
INSERT INTO singer (name, cover_path)
VALUES ('周杰伦', '/covers/jaychou.jpg'),
       ('林俊杰', '/covers/jjlin.jpg'),
       ('Taylor Swift', '/covers/taylorswift.jpg'),
       ('陈奕迅', '/covers/easonchan.jpg'),
       ('邓紫棋', '/covers/gem.jpg');

-- 2. 添加音乐类别数据
INSERT INTO category (name)
VALUES ('流行'),
       ('摇滚'),
       ('R&B'),
       ('电子'),
       ('民谣'),
       ('嘻哈');

-- 3. 添加音乐数据
INSERT INTO music (title, file_name, duration, singer_id, category_id, is_favorite)
VALUES ('七里香', 'qilixiang.mp3', 298, 1, 1, 1),
       ('夜曲', 'nocturne.mp3', 213, 1, 3, 1),
       ('江南', 'jiangnan.mp3', 245, 2, 1, 0),
       ('她说', 'shesaid.mp3', 198, 2, 1, 1),
       ('Love Story', 'lovestory.mp3', 235, 3, 1, 1),
       ('Blank Space', 'blankspace.mp3', 231, 3, 1, 0),
       ('K歌之王', 'ksongking.mp3', 223, 4, 1, 1),
       ('富士山下', 'fuji.mp3', 267, 4, 1, 0),
       ('泡沫', 'bubble.mp3', 256, 5, 1, 1),
       ('光年之外', 'lightyears.mp3', 238, 5, 1, 1),
       ('双截棍', 'nunchucks.mp3', 187, 1, 6, 0),
       ('曹操', 'caocao.mp3', 203, 2, 2, 0);
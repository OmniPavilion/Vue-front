-- 创建日记数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS diary;
USE diary;

-- 删除已存在的表（如果有）
DROP TABLE IF EXISTS t_log;
DROP TABLE IF EXISTS t_weather;
DROP TABLE IF EXISTS t_category;
DROP TABLE IF EXISTS t_note;
DROP TABLE IF EXISTS t_plan_task;
DROP TABLE IF EXISTS t_plan;


-- -------------------------
-- 表结构定义
-- -------------------------

-- 创建天气表
CREATE TABLE t_weather
(
    id   INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(30) NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 创建分类表
CREATE TABLE t_category
(
    id   INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(30) NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 创建日志表（修正语法问题）
CREATE TABLE t_log
(
    id          INT PRIMARY KEY AUTO_INCREMENT,
    record_date DATE         NOT NULL DEFAULT (CURRENT_DATE()),
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    activity    VARCHAR(100) NOT NULL,
    category_id INT,
    weather_id  INT          NOT NULL DEFAULT 1,
    FOREIGN KEY (category_id) REFERENCES t_category (id),
    FOREIGN KEY (weather_id) REFERENCES t_weather (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 创建记事表
CREATE TABLE t_note
(
    id         INT PRIMARY KEY AUTO_INCREMENT,
    content    VARCHAR(255) NOT NULL,
    due_date   DATE,
    due_time   TIME,
    status     VARCHAR(20)  NOT NULL DEFAULT 'pending',
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    notes      TEXT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 创建计划表
CREATE TABLE t_plan
(
    id         INT PRIMARY KEY AUTO_INCREMENT,
    title      VARCHAR(100) NOT NULL,
    start_date DATE         NOT NULL,
    end_date   DATE         NOT NULL,
    status     VARCHAR(20)  NOT NULL DEFAULT 'draft',
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 创建计划任务表
CREATE TABLE t_plan_task
(
    id          INT PRIMARY KEY AUTO_INCREMENT,
    plan_id     INT          NOT NULL,
    title       VARCHAR(100) NOT NULL,
    description TEXT,
    due_date    DATE,
    status      VARCHAR(20)  NOT NULL DEFAULT 'pending',
    FOREIGN KEY (plan_id) REFERENCES t_plan (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;


-- -------------------------
-- 初始数据插入
-- -------------------------

-- 插入天气数据
INSERT INTO t_weather (name)
VALUES ('晴'),
       ('多云'),
       ('阴'),
       ('小雨'),
       ('大雨'),
       ('雪'),
       ('雾');

-- 插入分类数据
INSERT INTO t_category (name)
VALUES ('学习'),
       ('工作'),
       ('生活'),
       ('运动'),
       ('娱乐'),
       ('社交');

-- 插入日志数据
INSERT INTO t_log (record_date, activity, category_id, weather_id)
VALUES ('2023-05-01', '学习了SQL数据库设计', 1, 1),
       ('2023-05-02', '完成了项目需求分析', 2, 2),
       ('2023-05-03', '晨跑5公里', 4, 1),
       ('2023-05-04', '与朋友聚餐', 6, 3),
       ('2023-05-05', '阅读《深入理解计算机系统》', 1, 4),
       ('2023-05-06', '整理房间', 3, 2),
       ('2023-05-07', '看电影《肖申克的救赎》', 5, 5);

-- 插入记事数据
INSERT INTO t_note (content, due_date, due_time, status, notes)
VALUES ('完成季度报告', '2023-05-15', '17:00:00', 'pending', '需要包含市场分析部分'),
       ('购买生日礼物', '2023-05-10', NULL, 'completed', '已购买书籍作为礼物'),
       ('预约牙医', NULL, NULL, 'pending', '需要确认诊所营业时间'),
       ('准备项目演示', '2023-05-20', '14:30:00', 'in-progress', '制作PPT和演讲稿'),
       ('缴纳水电费', '2023-05-08', NULL, 'completed', NULL),
       ('学习React框架', NULL, NULL, 'pending', '计划每天学习2小时');

-- 插入计划数据
INSERT INTO t_plan (title, start_date, end_date, status)
VALUES ('夏季健身计划', '2023-06-01', '2023-08-31', 'active'),
       ('项目开发计划', '2023-05-01', '2023-07-31', 'active'),
       ('读书计划', '2023-05-01', '2023-05-31', 'pending');

-- 插入计划任务数据
INSERT INTO t_plan_task (plan_id, title, description, due_date, status)
VALUES (1, '制定健身方案', '规划每周锻炼内容和目标', '2023-06-05', 'completed'),
       (1, '购买健身器材', '购买哑铃和瑜伽垫', '2023-06-10', 'pending'),
       (2, '需求分析', '收集并分析用户需求', '2023-05-10', 'completed'),
       (2, 'UI设计', '设计用户界面原型', '2023-05-15', 'in-progress'),
       (3, '阅读《代码大全》', '完成第1-5章阅读', '2023-05-15', 'pending'),
       (3, '撰写读书笔记', '整理阅读收获', '2023-05-20', 'pending');
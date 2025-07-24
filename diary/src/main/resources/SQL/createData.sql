-- 删除数据库
DROP DATABASE IF EXISTS diary;

-- 创建日记数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS diary;
USE diary;

-- -------------------------
-- 表结构定义（拆分日志表）
-- -------------------------

-- 创建状态枚举类型
CREATE TABLE IF NOT EXISTS t_status
(
    id   TINYINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(20) NOT NULL UNIQUE COMMENT '状态名称：draft(草稿)/pending(待办)/in-progress(进行中)/completed(已完成)/cancelled(已取消)'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT '系统状态枚举表';

-- 创建天气表（固定枚举值）
CREATE TABLE t_weather
(
    id   TINYINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(30) NOT NULL UNIQUE COMMENT '天气类型名称'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT '天气类型枚举表';

-- 创建分类表（固定枚举值）
CREATE TABLE t_category
(
    id   TINYINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(30) NOT NULL UNIQUE COMMENT '日志分类名称'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT '日志分类枚举表';

-- 创建日期表（包含日期和天气）
CREATE TABLE t_daily_time
(
    id          INT PRIMARY KEY AUTO_INCREMENT,
    record_date DATE      NOT NULL UNIQUE COMMENT '记录日期',
    weather     TINYINT   NOT NULL DEFAULT 1 COMMENT '天气ID',
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (weather) REFERENCES t_weather (id),
    INDEX idx_record_date (record_date), -- 日期索引
    INDEX idx_weather (weather)          -- 天气索引
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT '日期表（存储日期和天气）';

-- 创建日志表（活动详情，关联日期表）
CREATE TABLE t_daily_activity
(
    id         INT PRIMARY KEY AUTO_INCREMENT,
    date_id    INT          NOT NULL COMMENT '日期表ID',
    activity   VARCHAR(100) NOT NULL COMMENT '活动内容',
    category   TINYINT      NOT NULL COMMENT '分类ID',
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (date_id) REFERENCES t_daily_time (id),
    FOREIGN KEY (category) REFERENCES t_category (id),
    INDEX idx_date_id (date_id),       -- 日期ID索引
    INDEX idx_category (category),     -- 分类索引
    INDEX idx_create_time (created_at) -- 创建时间索引
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT '日志表（存储活动详情，关联日期表）';

-- 创建记事表（使用状态枚举）
CREATE TABLE t_note
(
    id         INT PRIMARY KEY AUTO_INCREMENT,
    content    VARCHAR(255) NOT NULL COMMENT '记事内容',
    due_date   DATE         NOT NULL COMMENT '截止日期（必填）',
    due_time   TIME         NOT NULL COMMENT '截止时间（必填，默认设为全天）',
    status     TINYINT      NOT NULL DEFAULT 2 COMMENT '状态ID，默认待办',
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    notes      TEXT COMMENT '备注信息（可选）',
    FOREIGN KEY (status) REFERENCES t_status (id),
    INDEX idx_due_datetime (due_date, due_time), -- 联合索引提高日期时间查询效率
    INDEX idx_status (status)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT '待办记事表（强制时间约束）';

-- 创建计划表（使用状态枚举）
CREATE TABLE t_plan
(
    id         INT PRIMARY KEY AUTO_INCREMENT,
    title      VARCHAR(100) NOT NULL COMMENT '计划标题',
    start_date DATE         NOT NULL COMMENT '开始日期',
    end_date   DATE         NOT NULL COMMENT '结束日期',
    status     TINYINT      NOT NULL DEFAULT 1 COMMENT '状态ID，默认草稿',
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (status) REFERENCES t_status (id),
    INDEX idx_start_end_date (start_date, end_date), -- 按时间范围查询索引
    INDEX idx_status (status)                        -- 按状态查询索引
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT '计划表';

-- 创建计划任务表（使用状态枚举）
CREATE TABLE t_plan_task
(
    id          INT PRIMARY KEY AUTO_INCREMENT,
    plan_id     INT          NOT NULL COMMENT '所属计划ID',
    title       VARCHAR(100) NOT NULL COMMENT '任务标题',
    description TEXT COMMENT '任务描述',
    status      TINYINT      NOT NULL DEFAULT 2 COMMENT '状态ID，默认待办',
    FOREIGN KEY (plan_id) REFERENCES t_plan (id),
    FOREIGN KEY (status) REFERENCES t_status (id),
    INDEX idx_plan_id (plan_id),   -- 按计划ID查询索引
    INDEX idx_status (status)      -- 按状态查询索引
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT '计划任务表';

-- -------------------------
-- 初始数据插入
-- -------------------------

-- 插入状态数据（枚举值，5条）
INSERT INTO t_status (name)
VALUES ('draft'),
       ('pending'),
       ('in-progress'),
       ('completed'),
       ('cancelled'),
       ('expired');

-- 插入天气数据（枚举值，7条）
INSERT INTO t_weather (name)
VALUES ('Sunny'),
       ('Cloudy'),
       ('Overcast'),
       ('Light Rain'),
       ('Heavy Rain'),
       ('Snow'),
       ('Fog'),
       ('Lightning');

-- 插入分类数据（枚举值，6条）
INSERT INTO t_category (name)
VALUES ('Study'),
       ('Work'),
       ('Life'),
       ('Exercise'),
       ('Entertainment'),
       ('Social');

-- -------------------------
-- 日期表数据插入
-- -------------------------
# INSERT INTO t_daily_time (record_date, weather)
# VALUES ('2023-05-01', 1),
#        ('2023-05-02', 2),
#        ('2023-05-03', 1),
#        ('2023-05-04', 3),
#        ('2023-05-05', 4),
#        ('2023-05-06', 2),
#        ('2023-05-07', 5),
#        ('2023-06-01', 1),
#        ('2023-06-02', 2),
#        ('2023-06-03', 3),
#        ('2023-06-04', 1),
#        ('2023-06-05', 2),
#        ('2023-06-06', 3),
#        ('2023-06-07', 1),
#        ('2023-06-08', 4),
#        ('2023-06-09', 2),
#        ('2023-06-10', 1),
#        ('2023-06-11', 3),
#        ('2023-06-12', 2),
#        ('2023-06-13', 1);

-- -------------------------
-- 日志表数据插入
-- -------------------------
# INSERT INTO t_daily_activity (date_id, activity, category)
# VALUES (1, '学习了SQL数据库设计', 1),
#        (1, '完成了项目需求分析', 2),
#        (3, '晨跑5公里', 4),
#        (4, '与朋友聚餐', 6),
#        (5, '阅读《深入理解计算机系统》', 1),
#        (5, '整理房间', 3),
#        (5, '观看电影《肖申克的救赎》', 5),
#        (8, '参加技术研讨会', 1),
#        (9, '编写后端接口文档', 2),
#        (10, '制作家庭晚餐', 3),
#        (11, '篮球友谊赛', 4),
#        (11, '观看音乐会', 5),
#        (11, '拜访亲戚', 6),
#        (11, '学习Kotlin编程', 1),
#        (15, '优化数据库查询', 2),
#        (16, '采购生活用品', 3),
#        (17, '健身房锻炼', 4),
#        (18, '观看话剧表演', 5),
#        (19, '组织团队建设活动', 6),
#        (20, '阅读《设计模式》', 1);

-- -------------------------
-- 插入记事数据（20条）
-- -------------------------
-- 插入记事数据
INSERT INTO t_note (content, due_date, due_time, status, notes)
VALUES
    -- 明确指定日期和时间
    ('完成季度报告', '2023-05-15', '17:00:00', 2, '需要包含市场分析部分'),
    ('购买生日礼物', '2023-05-10', '00:00:00', 4, '已购买书籍作为礼物'),
    ('预约牙医', '2023-05-31', '00:00:00', 2, '需要确认诊所营业时间'),
    ('准备项目演示', '2023-05-20', '14:30:00', 3, '制作PPT和演讲稿'),
    ('缴纳水电费', '2023-05-08', '00:00:00', 4, NULL),

    -- 学习计划设置默认日期和时间
    ('学习React框架', '2023-06-30', '00:00:00', 2, '计划每天学习2小时'),
    ('提交年度总结', '2023-06-30', '18:00:00', 2, '包含工作成果和未来计划'),
    ('预订度假酒店', '2023-07-15', '00:00:00', 2, '选择海滨度假村'),
    ('车辆保养', '2023-06-20', '09:00:00', 3, '已预约4S店'),
    ('办理签证', '2023-06-10', '00:00:00', 2, '准备护照和照片'),

    -- 培训课程指定具体时间
    ('参加线上培训', '2023-06-05', '20:00:00', 1, 'Java高级特性课程'),
    ('修剪花园草坪', '2023-06-07', '10:00:00', 4, '已完成'),
    ('申请信用卡', '2023-06-30', '00:00:00', 2, '比较不同银行优惠'),
    ('报名马拉松比赛', '2023-09-01', '00:00:00', 3, '半程马拉松项目'),

    -- 职业相关事项
    ('整理个人简历', '2023-06-15', '00:00:00', 2, '更新工作经历'),
    ('购买办公家具', '2023-06-25', '00:00:00', 2, '书桌和椅子'),
    ('观看教育讲座', '2023-06-09', '19:30:00', 1, '人工智能发展趋势'),

    -- 家庭维护
    ('维修厨房电器', '2023-06-12', '00:00:00', 3, '联系维修师傅'),
    ('参加行业会议', '2023-06-22', '00:00:00', 2, '准备演讲内容'),
    ('预订餐厅聚餐', '2023-06-18', '19:00:00', 4, '已预订包间');
-- -------------------------
-- 插入计划数据（10条）
-- -------------------------
INSERT INTO t_plan (title, start_date, end_date, status)
VALUES ('夏季健身计划', '2023-06-01', '2023-08-31', 3),
       ('项目开发计划', '2023-05-01', '2023-07-31', 3),
       ('读书计划', '2023-05-01', '2023-05-31', 2),
       ('英语学习计划', '2023-06-01', '2023-12-31', 2),
       ('旅行计划', '2023-07-15', '2023-07-30', 1),
       ('职业技能提升计划', '2023-06-10', '2023-09-10', 3),
       ('家居装修计划', '2023-08-01', '2023-10-31', 2),
       ('理财规划', '2023-06-01', '2023-12-31', 3),
       ('健康饮食计划', '2023-06-01', '2023-06-30', 4),
       ('社交活动计划', '2023-06-15', '2023-06-30', 2);

-- -------------------------
-- 插入计划任务数据（25条）
-- -------------------------
INSERT INTO t_plan_task (plan_id, title, description, status)
VALUES (1, '制定健身方案', '规划每周锻炼内容和目标', 4),
       (1, '购买健身器材', '购买哑铃和瑜伽垫', 2),
       (2, '需求分析', '收集并分析用户需求', 4),
       (2, 'UI设计', '设计用户界面原型', 3),
       (3, '阅读《代码大全》', '完成第1-5章阅读', 2),
       (3, '撰写读书笔记', '整理阅读收获', 2),
       (4, '背单词', '每天背诵50个单词', 3),
       (4, '听力练习', '每周完成3篇听力材料', 2),
       (4, '阅读英文原著', '每月阅读1本英文书籍', 2),
       (4, '写作练习', '每周写1篇英语作文', 1),
       (5, '确定旅行目的地', '选择东南亚国家', 4),
       (5, '预订机票', '比较航空公司价格', 2),
       (5, '安排行程', '制定每日活动计划', 3),
       (5, '预订住宿', '选择评分高的酒店', 2),
       (6, '学习云计算', '完成AWS认证课程', 2),
       (6, '参加技术会议', '报名行业峰会', 3),
       (6, '实践项目', '开发一个小型云应用', 1),
       (7, '设计装修风格', '确定现代简约风格', 4),
       (7, '选择装修公司', '对比3家装修公司报价', 2),
       (7, '采购装修材料', '列出材料清单', 2),
       (8, '制定预算表', '记录每月收支', 4),
       (8, '投资规划', '研究基金和股票', 3),
       (8, '应急资金储备', '存入3个月生活费', 2),
       (9, '制定食谱', '规划每日三餐', 4),
       (9, '采购食材', '购买新鲜蔬菜水果', 4),
       (10, '组织朋友聚会', '确定聚会时间和地点', 3),
       (10, '参加兴趣小组', '报名摄影俱乐部', 2);
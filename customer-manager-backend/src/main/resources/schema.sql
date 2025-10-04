-- 删除已存在的表
--DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS dynamic_table_metadata;
--DROP TABLE IF EXISTS dynamic_table_record;

-- 创建新表
CREATE TABLE IF NOT EXISTS user (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    nickname TEXT,
    email TEXT,
    enabled BOOLEAN DEFAULT TRUE,
    userface TEXT,
    regTime DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 动态表元数据表
CREATE TABLE IF NOT EXISTS dynamic_table_metadata (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    table_key TEXT NOT NULL,
    field_name TEXT NOT NULL,
    field_label TEXT,
    field_type TEXT,
    placeholder TEXT,
    required BOOLEAN DEFAULT FALSE,
    regex TEXT,
    options TEXT,
    sort_order INTEGER,
    default_value TEXT
);

-- 动态表记录表
CREATE TABLE IF NOT EXISTS dynamic_table_record (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    table_key TEXT NOT NULL,
    data TEXT, -- JSON格式存储动态字段数据
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

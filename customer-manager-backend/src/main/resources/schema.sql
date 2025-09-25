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

CREATE TABLE IF NOT EXISTS customer (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    phone TEXT NOT NULL,
    address TEXT,
    email TEXT,
    id_card TEXT,
    birthday DATETIME,
    height REAL,
    weight REAL,
    shoe_size TEXT,
    clothes_size TEXT,
    athlete_level TEXT,
    referee_level TEXT,
    family_heritage TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 动态客户表
CREATE TABLE IF NOT EXISTS dynamic_customer (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    phone TEXT NOT NULL
);

-- 动态客户字段表
CREATE TABLE IF NOT EXISTS dynamic_customer_field (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    customer_id INTEGER NOT NULL,
    field_name TEXT NOT NULL,
    field_value TEXT,
    field_type TEXT,
    FOREIGN KEY (customer_id) REFERENCES dynamic_customer(id) ON DELETE CASCADE
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
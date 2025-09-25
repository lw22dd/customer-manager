-- 插入默认用户（用户名: user, 密码: 123456）
-- 注意：这里应该存储加密后的密码，示例中使用明文仅作演示
INSERT OR IGNORE INTO user (username, password, nickname, email)
VALUES ('user', '123456', '默认用户', 'user@example.com');

-- 插入示例客户数据
INSERT OR IGNORE INTO customer (name, phone, address, email, id_card, birthday, height, weight, shoe_size, clothes_size, athlete_level, referee_level, family_heritage)
VALUES ('张三', '13800138000', '北京市朝阳区', 'zhangsan@example.com', '110101199001011234', '1990-01-01', 175.5, 65.0, '42', 'L', '一级运动员', NULL, '无');

INSERT OR IGNORE INTO customer (name, phone, address, email, id_card, birthday, height, weight, shoe_size, clothes_size, athlete_level, referee_level, family_heritage)
VALUES ('李四', '13900139000', '上海市浦东新区', 'lisi@example.com', '310101199202021234', '1992-02-02', 168.0, 55.5, '38', 'M', NULL, '一级裁判员', '祖父是武术教练');
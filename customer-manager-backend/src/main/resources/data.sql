-- 插入默认用户（用户名: user, 密码: 123456）
-- 注意：这里应该存储加密后的密码，示例中使用明文仅作演示
INSERT OR IGNORE INTO user (username, password, nickname, email)
VALUES ('user', '123456', '默认用户', 'user@example.com');

-- 插入默认用户（用户名: user, 密码: 123456）
-- 注意：这里应该存储加密后的密码，示例中使用明文仅作演示
INSERT OR IGNORE INTO user (username, password, nickname, email)
VALUES ('user', '123456', '默认用户', 'user@example.com');

-- 注释掉对已删除的customer表的引用
-- INSERT OR IGNORE INTO customer (name, phone, address, email, id_card, birthday, height, weight, shoe_size, clothes_size, athlete_level, referee_level, family_heritage)
-- VALUES ('张三', '13800138000', '北京市朝阳区', 'zhangsan@example.com', '110101199001011234', '1990-01-01', 175.5, 65.0, '42', 'L', '一级运动员', NULL, '无');

-- INSERT OR IGNORE INTO customer (name, phone, address, email, id_card, birthday, height, weight, shoe_size, clothes_size, athlete_level, referee_level, family_heritage)
-- VALUES ('李四', '13900139000', '上海市浦东新区', 'lisi@example.com', '310101199202021234', '1992-02-02', 168.0, 55.5, '38', 'M', NULL, '一级裁判员', '祖父是武术教练');


-- 插入客户表的默认字段元数据
INSERT INTO dynamic_table_metadata (
    table_key,
    field_name,
    field_label,
    field_type,
    placeholder,
    required,
    regex,
    options,
    sort_order,
    default_value
) VALUES
-- 姓名字段
('customer', 'name', '姓名', 'text', '请输入姓名', 1, NULL, NULL, 1, NULL),
-- 电话字段
('customer', 'phone', '电话', 'text', '请输入电话号码', 1, NULL, NULL, 2, NULL),
-- 收件信息字段
('customer', 'address', '收件信息', 'text', '请输入收件地址', 0, NULL, NULL, 3, NULL),
-- 身份证号字段
('customer', 'idCard', '身份证号', 'text', '请输入身份证号', 0, NULL, NULL, 4, NULL),
-- 生日字段
('customer', 'birthday', '生日', 'date', '请选择生日', 0, NULL, NULL, 5, NULL),
-- 身高字段
('customer', 'height', '身高(cm)', 'number', '请输入身高', 0, NULL, NULL, 6, NULL),
-- 体重字段
('customer', 'weight', '体重(kg)', 'number', '请输入体重', 0, NULL, NULL, 7, NULL),
-- 鞋码字段
('customer', 'shoeSize', '常用鞋码', 'text', '请输入鞋码', 0, NULL, NULL, 8, NULL),
-- 衣服尺码字段
('customer', 'clothesSize', '常用服装尺码', 'text', '请输入服装尺码', 0, NULL, NULL, 9, NULL),
-- 运动员等级字段
('customer', 'athleteLevel', '运动员等级', 'text', '请输入运动员等级', 0, NULL, NULL, 10, NULL),
-- 裁判员等级字段
('customer', 'refereeLevel', '裁判员等级', 'text', '请输入裁判员等级', 0, NULL, NULL, 11, NULL),
-- 家族传承字段
('customer', 'familyHeritage', '家族传承', 'textarea', '请输入家族传承信息', 0, NULL, NULL, 12, NULL);

-- 插入客户表的默认记录
INSERT INTO dynamic_table_record (table_key, data) VALUES
('customer', '{"name":"张三","phone":"13800138000","address":"北京市朝阳区","idCard":"110101199001011234","birthday":"1990-01-01","height":175.5,"weight":65.0,"shoeSize":"42","clothesSize":"L","athleteLevel":"一级运动员","refereeLevel":null,"familyHeritage":"无"}'),
('customer', '{"name":"李四","phone":"13900139000","address":"上海市浦东新区","idCard":"310101199202021234","birthday":"1992-02-02","height":168.0,"weight":55.5,"shoeSize":"38","clothesSize":"M","athleteLevel":null,"refereeLevel":"一级裁判员","familyHeritage":"祖父是武术教练"}');

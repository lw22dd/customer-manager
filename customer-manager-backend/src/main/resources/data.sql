-- 插入默认用户 17600445657 wuyuetongren
INSERT INTO user (username, password, nickname, email)
VALUES ('user', '123456', '默认用户', 'example@qq.com'),
('17600445657', 'wuyuetongren', '管理员', '734739907@qq.com');



-- 插入客户表元数据
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
('customer', 'name', '姓名', 'text', '请输入姓名', 1, NULL, NULL, 1, NULL),
('customer', 'phone', '电话', 'text', '请输入电话号码', 1, NULL, NULL, 2, NULL),
('customer', 'address', '收件信息', 'text', '请输入收件地址', 0, NULL, NULL, 3, NULL),
('customer', 'idCard', '身份证号', 'text', '请输入身份证号', 0, NULL, NULL, 4, NULL),
('customer', 'birthday', '生日', 'date', '请选择生日', 0, NULL, NULL, 5, NULL),
('customer', 'height', '身高(cm)', 'number', '请输入身高', 0, NULL, NULL, 6, NULL),
('customer', 'weight', '体重(kg)', 'number', '请输入体重', 0, NULL, NULL, 7, NULL),
('customer', 'shoeSize', '常用鞋码', 'text', '请输入鞋码', 0, NULL, NULL, 8, NULL),
('customer', 'clothesSize', '常用服装尺码', 'text', '请输入服装尺码', 0, NULL, NULL, 9, NULL),
('customer', 'athleteLevel', '运动员等级', 'text', '请输入运动员等级', 0, NULL, NULL, 10, NULL),
('customer', 'refereeLevel', '裁判员等级', 'text', '请输入裁判员等级', 0, NULL, NULL, 11, NULL),
('customer', 'familyHeritage', '家族传承', 'textarea', '请输入家族传承信息', 0, NULL, NULL, 12, NULL),
('customer', 'avatar', '头像', 'image', '请上传头像', 0, NULL, NULL, 13, NULL);
-- 插入30条客户表记录

INSERT INTO dynamic_table_record (table_key, data) VALUES
('customer', '{"name":"测试别删1","phone":"13800138001","address":"测试地址","idCard":"110101199010051234","birthday":"1990-10-06","height":170.0,"weight":60.0,"shoeSize":"40","clothesSize":"M","athleteLevel":null,"refereeLevel":null,"familyHeritage":"测试数据","avatar":"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg=="}'),
('customer', '{"name":"测试别删2","phone":"13800138001","address":"测试地址","idCard":"110101199010051234","birthday":"1990-10-06","height":170.0,"weight":60.0,"shoeSize":"40","clothesSize":"M","athleteLevel":null,"refereeLevel":null,"familyHeritage":"测试数据","avatar":"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg=="}'),
('customer', '{"name":"测试别删3","phone":"13800138001","address":"测试地址","idCard":"110101199010051234","birthday":"1990-10-06","height":170.0,"weight":60.0,"shoeSize":"40","clothesSize":"M","athleteLevel":null,"refereeLevel":null,"familyHeritage":"测试数据","avatar":"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg=="}');

INSERT INTO dynamic_table_record (table_key, data) VALUES
('customer', '{"name":"张三","phone":"13800138000","address":"北京市朝阳区","idCard":"110101199001011234","birthday":"1990-01-01","height":175.5,"weight":65.0,"shoeSize":"42","clothesSize":"L","athleteLevel":"一级运动员","refereeLevel":null,"familyHeritage":"无","avatar":"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg=="}'),
('customer', '{"name":"李四","phone":"13900139000","address":"上海市浦东新区","idCard":"310101199202021234","birthday":"1992-02-02","height":168.0,"weight":55.5,"shoeSize":"38","clothesSize":"M","athleteLevel":null,"refereeLevel":"一级裁判员","familyHeritage":"祖父是武术教练","avatar":"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg=="}');

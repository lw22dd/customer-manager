# customer-manager
SpringBoot + SpringWeb + Java Mail Sender + MyBatis  + SQLite + Vue 管理可自定义的动态客户表，发送邮件。
项目详细设计与实现说明请查看项目根目录下的《客户管理系统 临时文件 (1).pdf》文件，本README仅提供核心信息与快速上手指引。


# 客户管理系统 README


临时用户名和密码
user 123456

注意Java命名规范

包名（目录名） → 全小写 + 点号分隔（如 com.example.project）。

类名/接口名 → 帕斯卡命名法（如 UserService）。

方法/变量名 → 驼峰命名法（如 getUserName()）。

## 一、项目简介
基于前后端分离架构的客户管理系统，支持客户信息动态配置管理、生日自动提醒、数据导出等功能，可灵活适配客户数据的增删改查需求，降低业务变更时的代码修改成本。


## 二、核心技术栈
| 模块   | 技术选型                                  |
|--------|-------------------------------------------|
| 后端   | SpringBoot 3.5.6、SpringWeb、MyBatis、SQLite、JWT、Spring Security |
| 前端   | Vue 3、Pinia、Axios、Vite、Tailwind CSS   |
| 部署   | Linux 服务器（推荐 2 核 4G 配置）         |


## 三、主要功能
1. **账号登录系统**：基于 JWT 实现身份鉴权，登录成功后生成 Token，支持 Token 过期自动跳转登录页；
2. **客户信息动态管理**：采用“动态表”设计，支持字段增删改、排序调整，可动态渲染表格与表单，支持关键字搜索和多条件筛选；
3. **生日提醒功能**：每天凌晨 1 点通过定时任务查询当日生日客户，自动发送 QQ 邮箱提醒；
4. **其他优化功能**：重复姓名/手机号提醒、地址输入联想、头像上传、Excel 表格导出、二次确认防误操作。


## 四、快速启动
### 1. 后端启动（SpringBoot）
- **环境准备**：安装 JDK 11+、Maven 3.6+；
- **配置修改**：
  1. 打开 `src/main/resources/application.properties`；
  2. 替换 `spring.mail.username` 和 `spring.mail.password` 为自己的 QQ 邮箱账号及 16 位授权码；
- **启动项目**：运行项目主类（如 `CustomerManagerApplication.java`），系统自动执行 `schema.sql`（建表）和 `data.sql`（初始化默认数据）。

### 2. 前端启动（Vue 3）
- **环境准备**：安装 Node.js 16+；
- **依赖安装**：进入前端项目目录，执行 `npm install`；
- **启动项目**：执行 `npm run dev`，访问终端输出的本地地址（如 `http://localhost:5173`）即可。


## 五、注意事项
1. 邮箱授权码需从 QQ 邮箱“设置-账户-POP3/IMAP/SMTP/Exchange/CardDAV/CalDAV 服务”中开启 SMTP 后获取；
2. 系统初始账号：`user`，初始密码：`123456`；
3. 若需存储头像文件，可在后端配置阿里云 OSS 路径（详见 PDF 中“上传头像功能”说明）。


## 六、测试相关
- 后端单元测试：运行 `src/test/java` 下的 `DynamicTableServiceTest.java`，可自动化测试动态表核心接口；
- 前端测试：执行 `npm run test-api` 测试 API 调用，执行 `npx tsc --noEmit` 校验 TS 语法。
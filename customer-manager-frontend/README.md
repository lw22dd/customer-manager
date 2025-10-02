# 客户管理系统 - 前端

基于 Vue 3 + TypeScript + Vite 构建的客户管理系统前端项目。

## 项目环境配置

本项目使用环境变量来管理不同环境的配置。项目包含以下环境配置文件：

- `.env.development`: 开发环境配置
- `.env.production`: 生产环境配置

### 主要环境变量

| 环境变量 | 说明 | 开发环境默认值 | 生产环境建议值 |
|---------|------|--------------|-------------|
| VITE_APP_TITLE | 应用标题 | 客户管理系统 - 开发环境 | 客户管理系统 |
| VITE_APP_API_BASE_URL | API基础地址 | http://localhost:8080 | http://api.yourdomain.com |
| VITE_APP_ENV | 当前环境 | development | production |

### 如何修改环境配置

1. 打开对应环境的配置文件
2. 修改需要的环境变量值

**注意**：生产环境中的 `VITE_APP_API_BASE_URL` 需要替换为实际的后端API地址。

## 项目脚本

```bash
# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 构建生产版本
npm run build

# 预览生产版本
npm run preview
```

## 生产环境构建

执行以下命令构建生产版本：

```bash
npm run build
```

构建后的文件将位于 `dist` 目录，可以部署到任何静态文件服务器。

## 技术栈

- Vue 3
- TypeScript
- Vite
- Element Plus
- Pinia
- Vue Router

## 开发说明

1. 项目使用 TypeScript，请确保代码符合 TypeScript 规范
2. 组件采用 Vue 3 的 `<script setup>` 语法
3. 样式使用 Tailwind CSS 和 Element Plus 组件库

## 其他

- 详细的 Vue 3 + TypeScript 开发指南请参考 [Vue Docs TypeScript Guide](https://vuejs.org/guide/typescript/overview.html#project-setup)
- Vite 配置文档请参考 [Vite 官方文档](https://vitejs.dev/config/)

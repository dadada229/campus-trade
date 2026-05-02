# 校园二手交易平台 - 前端项目

## 项目概述

校园二手交易平台是一个基于 Vue 3 + Vite 构建的全功能 Web 应用，旨在为校园用户提供便捷的二手商品交易服务。平台支持商品发布、浏览、搜索、收藏、聊天、订单管理以及管理员后台等核心功能。

### 主要功能

- **用户认证系统**：手机号注册、登录、个人信息编辑
- **商品管理**：商品发布、编辑、浏览、搜索、分类、收藏
- **聊天系统**：实时聊天、会话管理、未读消息
- **订单系统**：下单、订单详情、我的订单
- **个人中心**：我的商品、我的订单、我的收藏、地址管理
- **管理员后台**：分类管理、轮播图管理、公告管理、用户管理

## 技术栈

### 核心框架
- **Vue 3**：使用 Composition API 和 `<script setup>` 语法
- **Vite**：现代化前端构建工具
- **Vue Router**：单页应用路由管理
- **Pinia**：Vue 3 状态管理

### UI 组件库
- **Element Plus**：Vue 3 组件库
- **@element-plus/icons-vue**：Element Plus 图标库

### 工具库
- **Axios**：HTTP 请求库
- **element-china-area-data**：中国省市区数据
- **sortablejs**：拖拽排序库
- **vuedraggable**：Vue 3 拖拽组件

### 开发工具
- **mockjs**：Mock 数据生成
- **vite-plugin-mock**：Vite Mock 插件
- **sass**：CSS 预处理器

## 项目结构

```
campus-app/
├── public/                 # 静态资源
│   ├── favicon.svg
│   └── icons.svg
├── src/
│   ├── api/               # API 模块
│   │   ├── modules/      # API 接口模块
│   │   │   ├── admin.js
│   │   │   ├── auth.js
│   │   │   ├── chat.js
│   │   │   ├── order.js
│   │   │   └── product.js
│   │   ├── config.js     # API 配置
│   │   └── request.js    # Axios 封装
│   ├── assets/           # 资源文件
│   │   ├── styles/       # 全局样式
│   │   └── ...
│   ├── components/       # 组件
│   │   ├── business/     # 业务组件
│   │   └── common/       # 通用组件
│   ├── router/           # 路由
│   │   ├── guards.js     # 路由守卫
│   │   ├── index.js      # 路由配置
│   │   └── routes.js     # 路由定义
│   ├── stores/           # Pinia 状态管理
│   │   ├── chat.js       # 聊天状态
│   │   └── user.js       # 用户状态
│   ├── utils/            # 工具函数
│   │   ├── errorHandler.js  # 错误处理
│   │   ├── errorLogger.js   # 错误日志
│   │   ├── format.js        # 格式化
│   │   ├── image.js         # 图片处理
│   │   ├── mockData.js      # Mock 数据
│   │   ├── storage.js       # 本地存储
│   │   ├── validate.js      # 验证
│   │   └── websocket.js     # WebSocket 管理
│   ├── views/            # 页面组件
│   │   ├── admin/        # 管理员后台页面
│   │   ├── auth/         # 认证页面
│   │   ├── chat/         # 聊天页面
│   │   ├── home/         # 首页
│   │   ├── product/      # 商品页面
│   │   └── profile/      # 个人中心页面
│   ├── App.vue           # 根组件
│   ├── main.js           # 入口文件
│   └── style.css         # 全局样式
├── mock/                 # Mock 数据目录
├── index.html            # HTML 模板
├── package.json          # 项目配置
├── vite.config.js        # Vite 配置
└── README.md             # 项目文档
```

## 功能模块说明

### 1. 用户认证模块
- **登录**：手机号 + 密码登录
- **注册**：用户注册并完善信息
- **个人信息**：查看和编辑个人资料

### 2. 商品模块
- **商品列表**：按分类浏览商品
- **商品详情**：查看商品详细信息
- **发布商品**：上传图片、填写信息发布商品
- **搜索**：按关键词搜索商品
- **分类**：分类筛选商品
- **收藏**：收藏喜欢的商品

### 3. 聊天模块
- **会话列表**：查看所有聊天会话
- **聊天详情**：发送和接收消息
- **实时通信**：基于 WebSocket 的实时聊天
- **未读消息**：统计和显示未读消息数

### 4. 订单模块
- **下单**：购买商品创建订单
- **订单详情**：查看订单详细信息
- **订单列表**：查看我的订单

### 5. 个人中心模块
- **我的商品**：我发布的商品
- **我的订单**：订单管理
- **我的收藏**：收藏的商品
- **地址管理**：收货地址管理

### 6. 管理员后台模块
- **分类管理**：商品分类增删改
- **轮播图管理**：首页轮播图
- **公告管理**：系统公告发布
- **用户管理**：用户信息查看

## 架构设计

### 状态管理
- **user store**：管理用户登录状态、用户信息
- **chat store**：管理聊天会话、消息列表

### API 设计
- 统一请求封装（request.js）
- 拦截器处理认证和错误
- 按模块划分 API 接口

### WebSocket
- 实时聊天通信
- 自动重连机制
- 消息事件处理

## 部署说明

### 环境要求
- Node.js 16+
- npm 或 yarn

### 安装依赖
```bash
npm install
```

### 开发模式运行
```bash
npm run dev
```

### 生产构建
```bash
npm run build
```

### 预览构建结果
```bash
npm run preview
```

## 配置说明

### API 配置
- 配置文件：`src/api/config.js`
- 支持开发/生产环境切换
- WebSocket 地址动态生成

### 环境变量
- 使用 Vite 的环境变量系统
- `.env` 文件配置环境变量

## 浏览器支持
- Chrome (推荐)
- Firefox
- Safari
- Edge

## 开发规范

### 代码风格
- 使用 Composition API
- `<script setup>` 语法
- 无多余空行（方法间除外）
- 保留关键功能注释

### 组件规范
- 业务组件放在 `components/business/`
- 通用组件放在 `components/common/`
- 页面组件放在 `views/` 对应目录

### 路由规范
- 路由懒加载
- 路由守卫处理权限
- meta 字段配置页面标题和权限

## 注意事项

1. 后端 API 地址需在 `vite.config.js` 或 `src/api/config.js` 中配置
2. WebSocket 端口默认 8080，可根据实际情况修改
3. 图片上传功能需后端配合
4. 开发环境可使用 Mock 数据进行测试

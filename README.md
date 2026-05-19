# 售后工单处理系统

## 项目简介

售后工单处理系统用于演示售后工单从提交到处理完成的基本流程。系统支持三类角色：客户、客服、处理人，实现了工单创建、分派、接单、处理、确认、退回、关闭等完整流程。

## 技术栈

- **后端**: SpringBoot 2.7.18 + Spring Data JPA
- **前端**: Vue 2.7 + Element UI
- **数据库**: H2（内存数据库）
- **构建工具**: Maven（后端）、Vue CLI（前端）

## 核心流程

### 正常流程
1. 客户创建工单 → 状态：待分派
2. 客服分派处理人 → 状态：已分派
3. 处理人接单 → 状态：处理中
4. 处理人提交处理结果 → 状态：待确认
5. 客户确认完成 → 状态：已完成

### 退回流程
1. 客户创建工单 → 状态：待分派
2. 客服分派处理人 → 状态：已分派
3. 处理人接单 → 状态：处理中
4. 处理人提交处理结果 → 状态：待确认
5. 客户退回工单（填写退回原因）→ 状态：已退回
6. 处理人重新提交处理结果 → 状态：待确认
7. 客户确认完成 → 状态：已完成

### 关闭流程
客服可以关闭待分派、已分派、处理中、已退回状态的工单，关闭时需要填写关闭原因。

## 目录结构

```
customer-server/
├── backend/                    # 后端项目
│   ├── src/
│   │   └── main/
│   │       ├── java/com/customer/server/
│   │       │   ├── controller/    # 控制器层
│   │       │   ├── service/       # 服务层
│   │       │   ├── repository/    # 数据访问层
│   │       │   ├── entity/        # 实体类
│   │       │   ├── dto/           # 数据传输对象
│   │       │   ├── config/        # 配置类
│   │       │   └── CustomerServerApplication.java
│   │       └── resources/
│   │           └── application.yml
│   └── pom.xml
├── frontend/                   # 前端项目
│   ├── public/
│   ├── src/
│   │   ├── components/          # Vue组件
│   │   ├── api/                 # API接口
│   │   ├── router/              # 路由配置
│   │   ├── App.vue
│   │   └── main.js
│   ├── package.json
│   └── vue.config.js
└── README.md
```

## 关键代码模块

### 后端模块

| 模块 | 说明 | 文件 |
|------|------|------|
| WorkOrderController | 工单API控制器 | controller/WorkOrderController.java |
| WorkOrderService | 工单业务逻辑 | service/WorkOrderService.java |
| WorkOrder | 工单实体类 | entity/WorkOrder.java |
| OperationLog | 操作日志实体类 | entity/OperationLog.java |
| WorkOrderStatus | 工单状态枚举 | entity/WorkOrderStatus.java |
| GlobalExceptionHandler | 全局异常处理 | config/GlobalExceptionHandler.java |

### 前端模块

| 模块 | 说明 | 文件 |
|------|------|------|
| WorkOrderList.vue | 工单列表主页面 | components/WorkOrderList.vue |
| workOrder.js | API接口定义 | api/workOrder.js |
| request.js | HTTP请求封装 | api/request.js |
| App.vue | 根组件（含角色切换） | App.vue |

## 环境要求

- **JDK**: 1.8 或更高版本
- **Maven**: 3.6 或更高版本
- **Node.js**: 14.x 或更高版本
- **npm**: 6.x 或更高版本

## 后端启动命令

### Windows 快速启动（推荐）
双击 `backend/start.bat` 脚本自动启动

### 手动启动
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

或在 IDE 中直接运行主类：
```
com.customer.server.CustomerServerApplication
```

## 前端启动命令

### Windows 快速启动（推荐）
双击 `frontend/start.bat` 脚本自动启动

### 手动启动
```bash
cd frontend
npm install
npm run serve
```

## 访问地址

- **前端页面**: http://localhost:8081
- **后端接口**: http://localhost:8080/api/workorders
- **H2数据库控制台**: http://localhost:8080/h2-console

### H2控制台配置
- JDBC URL: `jdbc:h2:mem:customerdb`
- 用户名: `sa`
- 密码: （空）

## 常见问题

### 1. Maven 命令找不到
- 请先安装 Maven 并配置环境变量
- 下载地址: https://maven.apache.org/download.cgi

### 2. Node.js 命令找不到
- 请先安装 Node.js
- 下载地址: https://nodejs.org/

### 3. 前端启动后页面空白
- 检查后端服务是否已启动
- 检查浏览器控制台是否有报错信息

### 4. 中文用户名乱码
- 系统已自动处理编码问题，如仍有乱码请使用英文用户名测试

## 角色切换说明

系统通过页面顶部的角色选择器来模拟不同用户角色，点击"应用"按钮后页面会刷新以应用新角色。

### 预置测试角色

| 角色 | 用户名 | 权限说明 |
|------|--------|----------|
| 客户 | 张三 | 创建工单、查看自己的工单、确认完成、退回工单 |
| 客服 | 客服小王 | 查看所有工单、分派处理人、关闭工单 |
| 处理人 | 李工 | 查看分派给自己的工单、接单、提交处理结果 |

也可以手动修改用户名为其他值进行测试。

## API接口列表

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/workorders | 创建工单 |
| PUT | /api/workorders/{id}/assign | 分派工单 |
| PUT | /api/workorders/{id}/accept | 接单 |
| PUT | /api/workorders/{id}/submit | 提交处理结果 |
| PUT | /api/workorders/{id}/confirm | 确认完成 |
| PUT | /api/workorders/{id}/return | 退回工单 |
| PUT | /api/workorders/{id}/close | 关闭工单 |
| GET | /api/workorders | 查询工单列表 |
| GET | /api/workorders/{id} | 获取工单详情 |
| GET | /api/workorders/{id}/logs | 获取操作日志 |
| GET | /api/workorders/statistics | 获取统计数据 |

所有接口需要在请求头中携带：
- `X-Role`: 角色（CUSTOMER/SERVICE/HANDLER）
- `X-User`: 用户名

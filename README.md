# core-openapi

> Core Platform 的开放 API 平台 — 将内部平台能力标准化、产品化，变成任何应用、开发者、企业都可以调用的基础能力。

[![Java](https://img.shields.io/badge/Java-21-blue)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.4-brightgreen)](https://vuejs.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)

---

## 📂 项目结构

```
core-openapi/
├── backend/                Spring Boot 3.2 后端
│   ├── src/main/java/      Java 源码（三层架构）
│   ├── src/main/resources/ 配置 + Flyway 数据库迁移
│   └── pom.xml             Maven 构建
│
├── web/                    Vue3 + TypeScript 前端管理后台
│   ├── src/api/            API Client 层
│   ├── src/pages/          各模块页面
│   ├── src/router/         路由配置
│   └── src/layouts/        AdminLayout
│
├── sdk/                    SDK Generator 模块（未来独立演进）
│   └── pom.xml
│
├── design-docs/            设计文档（Phase 0–10）
├── pom.xml                 根聚合 POM
├── CHANGELOG.md
└── README.md
```

## 🎯 定位

Core Platform 的所有内部能力（User、AI、Storage、Workflow 等）通过 `core-openapi` 对外暴露为标准化 API，并提供完整的开发者体验：

| Phase | Runtime | 状态 |
|-------|---------|------|
| 0 | API Foundation | ✅ 已完成 |
| 1 | API Gateway | ✅ 已完成 |
| 2 | API Definition | ✅ 已完成 |
| 3 | API Key | ✅ 已完成 |
| 4 | SDK | ✅ 已完成 |
| 5–10 | Security / Rate Limit / Portal / Marketplace / Enterprise | 📋 设计中 |

## 🚀 快速开始

### 环境要求

- **JDK 21**
- **Maven 3.9+**
- **Node.js 20+**（仅前端开发需要）

### 启动后端

```bash
cd backend
mvn spring-boot:run
```

默认使用 SQLite 数据库，端口 `8107`。

### 启动前端（开发模式）

```bash
cd web
npm install
npm run dev
```

前端运行在 `http://localhost:5173`，自动代理后端 API。

### 聚合构建（生产模式）

```bash
# 构建全部模块 + 前端，输出可运行 JAR
mvn clean package -f pom.xml
java -jar backend/target/core-openapi-backend-1.0.0-SNAPSHOT.jar
```

## 📡 API 端点

管理 API 前缀：`/api/v1/openapi`

| 端点 | 说明 |
|------|------|
| `GET /services` | 服务列表 |
| `GET /definitions` | API 定义列表 |
| `GET /definitions/{id}` | API 定义详情 |
| `POST /definitions` | 创建 API 定义 |
| `POST /definitions/{id}/publish` | 发布 API |
| `GET /definitions/{id}/docs` | API 自动文档 |
| `GET /users` | 开发者列表 |
| `GET /applications` | 应用列表 |
| `POST /applications` | 创建应用 |
| `POST /applications/{id}/keys` | 生成 API Key |
| `POST /sdk/generate` | 生成 SDK |
| `GET /sdk/projects` | SDK 项目列表 |
| `GET /sdk/generations/{id}/download` | 下载 SDK |

API 文档（Swagger UI）：`http://localhost:8107/swagger-ui.html`

## 🧱 技术栈

| 层级 | 技术 |
|------|------|
| 后端框架 | Spring Boot 3.2 + Java 21 |
| 数据库 | SQLite（默认）/ MySQL（生产） |
| ORM | MyBatis-Plus 3.5.7 |
| 数据库迁移 | Flyway |
| API 文档 | SpringDoc OpenAPI |
| SDK 生成 | OpenAPI Generator 7.8 |
| 前端框架 | Vue 3.4 |
| 构建工具 | Vite 5 + TypeScript |
| 状态管理 | Pinia |
| HTTP Client | Axios |

## 🏗️ 架构

```
                    Developer
                        │
                  core-openapi
                        │
        ┌───────────────┼───────────────┐
        │               │               │
   API Gateway     API Definition    API Key
        │               │               │
        └───────────────┼───────────────┘
                        │
                  SDK Generator
                        │
               ┌────────┴────────┐
           Java SDK         Python SDK
```

后端采用三层架构 + 六边形架构模式：
- **api/** — Controller、DTO、Request/Response、异常处理
- **application/** — Service、Domain、Command、Port 接口
- **infrastructure/** — Entity、Mapper、Repository 实现、配置

## 🔑 API Key 体系

- Key 格式：`sk_live_` 或 `sk_test_` + 43 位随机字符
- Key 原文仅生成时返回一次，存储 SHA-256 哈希
- 请求头：`Authorization: Bearer <key>`
- Gateway 层自动校验：Key 有效性 → 状态检查 → 过期检查 → 权限匹配

## 📖 设计文档

详见 [design-docs/](design-docs/) 目录：

- [000-tech.md](design-docs/000-tech.md) — 项目技术规范
- [000-roadMap.md](design-docs/000-roadMap.md) — 整体路线图
- [001-api-basic.md](design-docs/001-api-basic.md) — Phase 0 API Foundation
- [002-api-gateway.md](design-docs/002-api-gateway.md) — Phase 1 API Gateway
- [003-api-definition.md](design-docs/003-api-definition.md) — Phase 2 API Definition
- [004-api-key.md](design-docs/004-api-key.md) — Phase 3 API Key
- [005-sdk.md](design-docs/005-sdk.md) — Phase 4 SDK
- [006-security.md](design-docs/006-security.md) — Phase 5 Security

## 📝 License

MIT — 详见 [LICENSE](LICENSE)

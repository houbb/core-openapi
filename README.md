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
│   ├── src/main/java/      Java 源码（六边形架构）
│   ├── src/main/resources/ 配置 + Flyway 数据库迁移
│   └── pom.xml             Maven 构建
│
├── web/                    Vue3 + TypeScript 管理后台
│   ├── src/api/            API Client 层
│   ├── src/pages/          各模块页面
│   ├── src/router/         路由配置
│   └── src/layouts/        AdminLayout
│
├── web-dev/                Vue3 + TypeScript 开发者门户
│   ├── src/api/            API Client 层
│   ├── src/pages/          门户页面（Landing、Catalog、Playground、Apps）
│   ├── src/stores/         Pinia 状态管理
│   └── src/components/     公共组件
│
├── sdk/                    SDK Generator 模块
│   └── pom.xml
│
├── design-docs/            设计文档（Phase 0–10）
├── pom.xml                 根聚合 POM
├── CHANGELOG.md
└── README.md
```

## 🎯 定位

Core Platform 的所有内部能力（User、AI、Storage、Workflow 等）通过 `core-openapi` 对外暴露为标准化 API，并提供完整的企业级开发者体验：

| Phase | Runtime | 状态 |
|-------|---------|------|
| 0 | API Foundation | ✅ 已完成 |
| 1 | API Gateway | ✅ 已完成 |
| 2 | API Definition | ✅ 已完成 |
| 3 | API Key | ✅ 已完成 |
| 4 | SDK | ✅ 已完成 |
| 5 | Security | ✅ 已完成 |
| 6 | Rate Limit | ✅ 已完成 |
| 7 | Developer Portal | ✅ 已完成 |
| 8 | API Marketplace | ✅ 已完成 |
| 9 | Enterprise Open Platform | ✅ 已完成 |

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

### 启动管理后台（开发模式）

```bash
cd web
npm install
npm run dev
```

管理后台运行在 `http://localhost:5173`，自动代理后端 API。

### 启动开发者门户（开发模式）

```bash
cd web-dev
npm install
npm run dev
```

开发者门户运行在 `http://localhost:5174`。

### 聚合构建（生产模式）

```bash
# 构建全部模块 + 前端，输出可运行 JAR
mvn clean package -f pom.xml
java -jar backend/target/core-openapi-backend-1.0.0-SNAPSHOT.jar
```

## 📡 API 端点

管理 API 前缀：`/api/v1/openapi`

### API Foundation（Phase 0）

| 端点 | 说明 |
|------|------|
| `GET /services` | 服务列表 |
| `POST /services` | 创建服务 |
| `GET /services/{id}` | 服务详情 |
| `GET /tags` | 标签管理 |
| `GET /versions` | API 版本列表 |

### API Definition（Phase 2）

| 端点 | 说明 |
|------|------|
| `GET /definitions` | API 定义列表 |
| `GET /definitions/{id}` | API 定义详情 |
| `POST /definitions` | 创建 API 定义 |
| `POST /definitions/{id}/publish` | 发布 API |
| `GET /definitions/{id}/docs` | API 自动文档 |
| `POST /definitions/{id}/lifecycle` | 生命周期状态转换（Governance） |

### API Key & 应用管理（Phase 3）

| 端点 | 说明 |
|------|------|
| `GET /users` | 开发者列表 |
| `GET /applications` | 应用列表 |
| `POST /applications` | 创建应用 |
| `POST /applications/{id}/keys` | 生成 API Key |
| `POST /applications/{id}/permissions` | 授予权限 |

### Security（Phase 5）

| 端点 | 说明 |
|------|------|
| `GET /security/roles` | 角色管理 |
| `GET /security/policies` | 安全策略 |
| `GET /security/audit` | 审计日志 |

### Rate Limit（Phase 6）

| 端点 | 说明 |
|------|------|
| `GET /rate-limit/policies` | 限流策略 |
| `GET /rate-limit/status` | 限流状态 |

### Developer Portal（Phase 7）

| 端点 | 说明 |
|------|------|
| `GET /portal/documents` | 开发者文档 |
| `GET /portal/subscriptions` | 订阅管理 |
| `GET /portal/notifications` | 开发者通知 |
| `GET /portal/feedback` | 用户反馈 |

### Marketplace（Phase 8）

| 端点 | 说明 |
|------|------|
| `GET /marketplace/products` | API 商品列表 |
| `POST /marketplace/products` | 创建商品 |
| `GET /marketplace/providers` | Provider 管理 |
| `GET /marketplace/public/featured` | 公开浏览（无需认证） |
| `GET /marketplace/public/search` | 商品搜索 |
| `GET /marketplace/provider/dashboard` | Provider 仪表盘 |

### Enterprise（Phase 9）

| 端点 | 说明 |
|------|------|
| `GET /enterprise/dashboard` | 企业控制台总览 |
| `GET /enterprise/organizations` | 组织管理 |
| `GET /enterprise/organizations/{orgId}/teams` | 团队管理 |
| `GET /enterprise/organizations/{orgId}/members` | 成员管理 |
| `GET /enterprise/partners` | 合作伙伴管理 |
| `GET /enterprise/contracts` | 合同管理 |
| `GET /enterprise/sla-policies` | SLA 策略 |
| `GET /enterprise/compliance` | 合规策略 |
| `GET /enterprise/identity` | 身份提供商（SSO/LDAP） |
| `GET /enterprise/audit/logs` | 企业审计日志 |

### SDK（Phase 4）

| 端点 | 说明 |
|------|------|
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
| 管理后台 | Vue 3.4 + Vite 5 + TypeScript |
| 开发者门户 | Vue 3.4 + Vite 5 + TypeScript |
| 状态管理 | Pinia |
| HTTP Client | Axios |

## 🏗️ 架构

```
                         Enterprise
                             │
                   Enterprise Portal
                             │
                   API Marketplace
                             │
                   Developer Portal
                             │
                       SDK Runtime
                             │
                      API Gateway
                             │
                   Security Runtime
                             │
                   Rate Limit Runtime
                             │
                  API Definition Runtime
                             │
                   API Foundation Runtime
                             │
                       Core Platform
                             │
     ┌──────────┬──────────┬──────┬──────────┬──────────┐
     │          │          │      │          │          │
    User       Auth        AI   Storage   Workflow   Knowledge
```

### 后端模块

```
io.coreplatform.openapi/
├── api/              Controller 层 — 通用 Controller + DTO + 异常处理
├── application/      Service 层 — Domain 模型 + Command + Port 接口
├── gateway/          Phase 1 — API Gateway 运行时（路由代理、请求上下文）
├── infrastructure/   Entity + Mapper + Repository 实现 + 配置
├── sdk/              Phase 4 — SDK 生成引擎（Java/Python 代码生成）
├── security/         Phase 5 — 安全运行时（RBAC、JWT、审计、风控）
├── rate/             Phase 6 — 限流运行时（Token Bucket、QPS 控制）
├── portal/           Phase 7 — 开发者门户运行时（订阅、文档、通知）
├── marketplace/      Phase 8 — API Marketplace 运行时（商品、Provider、定价）
└── enterprise/       Phase 9 — 企业开放平台（组织、租户、SLA、合规）
```

### 数据库

10 个 Flyway 迁移脚本（V1–V10），覆盖全部 Phase：

| 迁移 | 内容 |
|------|------|
| V1 | API Foundation：service, definition, parameter, response, version, tag |
| V2 | API Gateway：route, access_log |
| V3 | API Definition：request_schema, example, response 扩展 |
| V4 | API Key：user, application, api_key, permission, usage_log |
| V5 | SDK：sdk_project, sdk_generation |
| V6 | Security：security_role, role_permission, security_policy, audit_log, risk_event, tenant_id 扩展 |
| V7 | Rate Limit：rate_limit_policy, rate_limit_bucket, rate_limit_event |
| V8 | Developer Portal：user 扩展, subscription, notification, setting, document, feedback |
| V9 | Marketplace：provider, product, plan, listing, review, usage_record |
| V10 | Enterprise：organization, team, member, partner, contract, sla, billing, compliance, identity, setting + 9 张表 tenant_id 扩展 |

## 🔑 API Key 体系

- Key 格式：`sk_live_` 或 `sk_test_` + 43 位随机字符
- Key 原文仅生成时返回一次，存储 SHA-256 哈希
- 请求头：`Authorization: Bearer <key>`
- Gateway 层自动校验：Key 有效性 → 状态检查 → 过期检查 → 权限匹配 → 租户隔离

## 🏢 企业级能力（Phase 9）

- **多组织模型**：Organization → Team → Member 三级层级
- **租户隔离**：全部核心资源表（API、Key、Usage、Subscription）支持 tenant_id 隔离
- **合作伙伴管理**：Partner 独立实体，STANDARD/PREMIUM/STRATEGIC 三级
- **API 治理**：DRAFT → REVIEW → APPROVED → PUBLISHED → DEPRECATED 完整生命周期
- **SLA 策略**：Organization 级别可用性/延迟/故障响应管理
- **合规审计**：审计日志 + 数据保留 + 脱敏策略
- **企业身份**：SSO / LDAP / SAML / OIDC 身份提供商集成

## 📖 设计文档

详见 [design-docs/](design-docs/) 目录：

| 文档 | 内容 |
|------|------|
| [000-tech.md](design-docs/000-tech.md) | 项目技术规范 |
| [000-roadMap.md](design-docs/000-roadMap.md) | 整体路线图 |
| [001-api-basic.md](design-docs/001-api-basic.md) | Phase 0 API Foundation |
| [002-api-gateway.md](design-docs/002-api-gateway.md) | Phase 1 API Gateway |
| [003-api-definition.md](design-docs/003-api-definition.md) | Phase 2 API Definition |
| [004-api-key.md](design-docs/004-api-key.md) | Phase 3 API Key |
| [005-sdk.md](design-docs/005-sdk.md) | Phase 4 SDK |
| [006-security.md](design-docs/006-security.md) | Phase 5 Security |
| [007-rate-limit.md](design-docs/007-rate-limit.md) | Phase 6 Rate Limit |
| [008-dev.md](design-docs/008-dev.md) | Phase 7 Developer Portal |
| [009-market.md](design-docs/009-market.md) | Phase 8 API Marketplace |
| [010-enterprise.md](design-docs/010-enterprise.md) | Phase 9 Enterprise Open Platform |

## 📝 License

MIT — 详见 [LICENSE](LICENSE)
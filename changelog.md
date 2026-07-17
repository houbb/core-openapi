# Changelog

## [0.5.0] — 2026-07-17

### Phase 4：SDK Runtime + 目录重组

**目标**：自动生成 Java/Python SDK + 项目目录规范化。

#### 目录重组
- `src/` → `backend/` — Spring Boot 后端（独立 Maven 工程，artifactId: `core-openapi-backend`）
- `sdk/` — SDK 模块（独立 Maven 工程，artifactId: `core-openapi-sdk`，未来扩展占位）
- `web/` — Vue3 前端（不变）
- 删除根聚合 `pom.xml`，backend 和 sdk 各自独立
- `changelog.md` → `CHANGELOG.md`
- 新增 `README.md` — 项目整体文档

#### 数据库变更 (V5__sdk.sql)

**新建表 (2 张)**
- `openapi_sdk_project` — SDK 项目（name, language, version, status）
- `openapi_sdk_generation` — SDK 生成记录（api_ids, generator_version, download_url, file_size）

#### 后端 — SDK Generator

**核心服务**
- `OpenApiSpecBuilder` — 从 DB 动态聚合 API Definition → OpenAPI 3.0 spec JSON
  - 遍历 definition → parameter → requestSchema → responseSchema
  - 自动映射参数位置、类型、必填、示例值
- `SdkGeneratorService` — 主生成流程
  - 写入 spec.json → 调用 OpenAPI Generator 7.8 → 打包 zip
  - 异步状态管理：GENERATING → READY / FAILED
  - 产物输出到 `data/sdk/`
- `SdkController` — `/api/v1/openapi/sdk`
  - `POST /generate` — 选择 API + 语言 → 生成
  - `GET /projects` — SDK 项目列表（分页）
  - `GET /projects/{id}` — 项目详情
  - `GET /projects/{id}/generations` — 生成记录列表
  - `GET /generations/{id}` — 生成记录详情
  - `GET /generations/{id}/download` — 下载 SDK zip

**已有模块增强**
- `application.yml` 新增 `app.sdk.output-dir` 配置

#### 前端 — SDK 管理

**新增页面**
- `SdkCenterPage.vue` — SDK 管理中心（路由：`/sdk`）
  - SDK 项目列表（名称、语言、版本、状态、生成时间）
  - 生成对话框：多选 API + 单选语言 + 填写名称版本 → 点击 Generate
  - 生成记录面板：查看历次生成记录，状态为 READY 时可直接下载

**API 层**
- `web/src/api/sdk.ts` — SDK API 类型和函数（SdkProject, SdkGeneration, generateSdk 等）

**路由 & 导航**
- 新增 `/sdk` → `SdkCenterPage`
- 侧边栏新增：🔧 SDK 管理

#### 验证结果
- ✅ backend 独立编译 + 12 个已有测试全部通过
- ✅ sdk 独立编译通过
- ✅ 前端 TypeScript 类型检查 + Vite 构建成功

---

## [0.4.0] — 2026-07-17

### Phase 3：API Key Runtime

**目标**：构建 API Key 认证体系，识别调用者身份，实现应用管理 + API Key 生命周期 + 权限绑定 + 网关认证 + 使用审计。

#### 数据库变更 (V4__apikey.sql)

**新建表 (6 张)**
- `openapi_user` — 开发者/用户（username, email, status）
- `openapi_application` — 应用（app_name, app_code, owner_id → openapi_user）
- `openapi_api_key` — API Key（key_prefix + SHA-256 key_hash，绝不存储明文）
- `api_permission` — 权限定义（如 ai.chat, storage.read）
- `application_permission` — 应用-权限多对多关联
- `api_key_usage_log` — Key 使用审计日志

#### 后端 (Spring Boot 3.2 + MyBatis-Plus)

**用户管理**
- `UserController` — `/api/v1/openapi/users` (CRUD)
- 完整六边形架构：domain → command → port → entity → mapper → repository → service → controller

**应用管理**
- `ApplicationController` — `/api/v1/openapi/applications` (CRUD + Keys + Permissions)
- 子资源：`/{id}/keys` (生成/禁用/启用/删除) + `/{id}/permissions` (授予/撤销)
- 生成 API Key 时返回原始 Key（仅此一次），后续仅显示前缀

**权限管理**
- `PermissionController` — `/api/v1/openapi/permissions` (CRUD)
- 种子数据：ai.chat, ai.embedding, storage.read, storage.upload, user.read

**Key 加密**
- `KeyGenerator` (`infrastructure/crypto/`) — SecureRandom 32字节 → base64url 密钥；SHA-256 哈希存储
- Key 格式：`sk_live_` 或 `sk_test_` + 43字符随机串

**Gateway 认证集成**
- `KeyValidator` — 在 GatewayFilter 中插入认证步骤（路由匹配后、请求校验前）
  - 提取 `Authorization: Bearer <key>` 头
  - 哈希查找，校验状态（ACTIVE/未过期/父应用有效）
  - 权限检查（基于 API definition 的 category 字段）
  - 更新 last_used_time，写使用日志
- `GatewayErrorCode` 新增：`INVALID_API_KEY(40101)`, `API_KEY_EXPIRED(40102)`, `API_KEY_DISABLED(40103)`, `PERMISSION_DENIED(40301)`
- `GatewayFilter` 管道新增 auth 步骤
- `GatewayException.mapToHttpStatus` 支持 401/403

**Dashboard 增强**
- 新增统计：userCount, applicationCount, activeKeyCount

#### 前端 (Vue3 + TypeScript)

**新增页面**
- `UserListPage.vue` — 用户 CRUD（路由：`/users`）
- `ApplicationListPage.vue` — 应用 CRUD（路由：`/applications`）
  - 创建/编辑对话框
- `ApplicationDetailPage.vue` — 应用详情 + API Keys + 权限管理（路由：`/applications/:id`）
  - Key 管理：生成（选择环境/名称/过期时间）、禁用/启用/删除
  - Key 生成后弹窗显示原始 Key（⚠️ 仅显示一次，支持一键复制）
  - 权限管理：授予/撤销
- `DashboardPage.vue` — 新增用户、应用、活跃 Key 统计卡片

**API 层**
- `web/src/api/apikey.ts` — 所有新 API 类型和函数（Users, Applications, API Keys, Permissions, Dashboard）

**路由 & 导航**
- 新增路由：`/users`, `/applications`, `/applications/:id`
- 侧边栏新增：👤 用户管理、📱 应用管理

#### 验证结果
- ✅ 后端编译通过 + 12 个已有测试全部通过
- ✅ 前端 TypeScript 类型检查通过 + Vite 构建成功
- ✅ 测试 schema.sql 补齐 Phase 1/2/3 全部表

---

## [0.3.0] — 2026-07-17

### Phase 2：API Definition Runtime

**目标**：深化 API 描述能力，让 API 变成机器可理解、开发者可直接使用的标准能力描述（JSON Schema + 示例 + 自动文档 + 请求校验）。

#### 数据库变更 (V3__definition.sql)

**新建表 (2 张)**
- `openapi_request_schema` — 请求体 JSON Schema 存储（api_id 唯一约束，支持 schema_json + example_json）
- `openapi_example` — 请求/响应示例（支持 REQUEST/RESPONSE 类型）

**扩展已有表**
- `openapi_response` 新增 `description` 字段

#### 后端 (Spring Boot 3.2 + MyBatis-Plus)

**Request Schema 管理**
- `RequestSchemaController` — `/api/v1/openapi/definitions/{apiId}/request-schema` (GET/PUT/DELETE)
- `RequestSchemaApplicationService` — upsert 模式：已有则更新，无则创建
- 完整六边形架构：domain → command → port → entity → mapper → repository → service → controller

**Example 管理**
- `ExampleController` — `/api/v1/openapi/definitions/{apiId}/examples` (GET/POST/PUT/DELETE)
- `ExampleApplicationService` — 标准 CRUD，支持 REQUEST/RESPONSE 类型

**自动文档（动态生成，无存储）**
- `DocumentationController` — `/api/v1/openapi/definitions/{apiId}/docs`、`/docs`、`/services/{serviceId}/docs`
- `DocumentationApplicationService` — 聚合 definition + parameters + requestSchema + responses + examples + tags，动态生成完整 API 文档

**Gateway 请求校验**
- `RequestValidator` — 在 GatewayFilter 管道中插入校验步骤（路由匹配后、转发前）
- 校验能力：必填参数检查 + 请求体 JSON Schema 校验（基于 `com.networknt:json-schema-validator`）
- `GatewayException` 新增 detail message 构造器，校验失败返回详细错误信息

**已有模块增强**
- `openapi_response` 全链路支持 `description` 字段（domain → entity → repository → service → controller → DTO）

#### 前端 (Vue3 + TypeScript)

**新增页面**
- `ApiExplorerPage.vue` — API Explorer（路由：`/definitions/:id/explorer`）
  - 聚合展示：基本信息 + 标签 + 参数分组（按 PATH/QUERY/HEADER/BODY） + 请求体 Schema + 响应定义 + 示例
  - JSON 自动格式化显示
  - 从 DefinitionDetailPage 一键跳转

**DefinitionDetailPage 增强**
- 新增 "请求Schema" Tab — JSON Schema 编辑（textarea + 格式化 + 保存/删除）
- 新增 "示例" Tab — Example CRUD（添加对话框 + 列表 + 删除）
- 顶部新增 "API Explorer" 按钮链接

**API 层**
- `definitions.ts` 新增类型：`RequestSchemaItem`、`ExampleItem`、`ApiDocument`
- 新增函数：`getRequestSchema`、`saveRequestSchema`、`deleteRequestSchema`、`getExamples`、`createExample`、`updateExample`、`deleteExample`、`getApiDocs`、`getAllDocs`、`getServiceDocs`

**路由**
- 新增 `/definitions/:id/explorer` → `ApiExplorerPage`

#### 验证结果
- ✅ 后端编译通过 + 12 个已有测试全部通过
- ✅ 前端 TypeScript 类型检查通过 + Vite 构建成功

---

## [0.2.0] — 2026-07-17

### Phase 1：API Gateway Runtime

**目标**：构建统一流量入口层，实现请求路由、HTTP 代理、统一响应、访问日志。

#### 后端 (Spring Boot 3.2 + RestClient)

**数据模型 (3 张新表)**
- `openapi_route` — 路由配置，关联 API 定义到后端目标 URL
- `openapi_access_log` — 网关访问日志
- `openapi_gateway_config` — 网关 KV 配置

**网关核心 (gateway 包)**
- `GatewayFilter` (OncePerRequestFilter) — 拦截 `/gateway/**`，编排完整代理管道
- `RequestRouter` — 路径+方法匹配，支持 `{param}` 模式转 Ant 模式
- `RequestProxy` — 基于 RestClient 的 HTTP 透明代理（Header/Body 透传）
- `RequestContext` / `RequestContextHolder` — 请求上下文 (requestId, traceId, clientId)
- `UnifiedResponse<T>` — 统一响应 `{code, message, data, traceId}`
- `GatewayErrorCode` — 错误码体系 (40400 路由未找到, 50001 服务不可用, 50002 超时等)

**REST API (新增)**
- 路由管理 CRUD + 激活/停用 (`/api/v1/openapi/routes`)
- 网关仪表盘 (`/api/v1/openapi/gateway/dashboard`)

**配置**
- 异步访问日志写入 (ThreadPoolTaskExecutor + @Async)
- RestClient 超时配置
- YAML 配置项 (`gateway.default-timeout`, `gateway.proxy-prefix`)

**暂缓**: JWT/OAuth 认证、API Key、限流、熔断、服务发现（后续 Phase）

---

## [0.1.0] — 2026-07-17

### Phase 0：API Foundation Runtime

**目标**：构建 API Catalog（API 目录），将平台内部能力标准化为统一 API 资产。

#### 后端 (Spring Boot 3.2 + MyBatis-Plus 3.5.7 + SQLite/MySQL)

**数据模型 (7 张表)**
- `openapi_service` — 服务注册
- `openapi_definition` — API 接口定义，生命周期：DRAFT → REVIEW → PUBLISHED → DEPRECATED → OFFLINE
- `openapi_parameter` — 请求参数定义 (PATH/QUERY/HEADER/BODY)
- `openapi_response` — 响应定义
- `openapi_version` — 接口级别版本管理 (DRAFT → ACTIVE → DEPRECATED → DISABLED)
- `openapi_tag` / `openapi_tag_mapping` — 多对多标签分类

**REST API (28+ 端点)** — `/api/v1/openapi`
- 服务 CRUD、API 定义 CRUD + 生命周期操作 (publish/deprecate/offline)
- 参数/响应/版本/标签 子资源管理
- Dashboard 统计聚合

**架构**：三层架构 (api → application → infrastructure) + 端口-适配器模式

#### 前端 (Vue3 + Vite + TypeScript)
- 仪表盘、服务管理、API 目录、API 详情（参数/响应/版本/标签）、标签管理

#### 测试
- 7 单元测试 (Mockito) + 5 集成测试 (H2)，全部通过
# Changelog

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
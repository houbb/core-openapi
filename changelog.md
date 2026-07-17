# Changelog

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
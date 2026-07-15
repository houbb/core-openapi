# Phase 1：API Gateway Runtime ⭐⭐⭐⭐⭐

```text
core-openapi

        Phase 1

    API Gateway Runtime
```

---

# 一、定位

API Foundation Runtime 解决：

> 「有哪些 API」

API Gateway Runtime 解决：

> 「所有 API 如何统一进入平台」

它是整个开放平台的**流量入口层**。

最终架构：

```text
                 Client

                   |
                   |
            core-openapi gateway

                   |
      --------------------------------

      core-user
      core-ai
      core-storage
      core-workflow
      core-notification

```

---

# 二、为什么 Phase 1 必须是 Gateway？

如果没有 Gateway：

调用：

```text
app
 |
 |
user-service

app
 |
 |
ai-service

app
 |
 |
storage-service

```

问题：

## 1. 地址暴露

客户端知道：

```
user-service:8081

ai-service:8082

storage-service:8083
```

未来服务迁移：

全部影响。

---

## 2. 安全分散

每个服务自己处理：

```
JWT

权限

日志

异常

限流

```

最终：

每个服务重复造轮子。

---

## 3. 无法形成平台能力

没有统一入口：

无法：

* API Key
* 调用统计
* 计费
* 限流
* 审计

---

所以：

Gateway 是：

> 平台能力出口。

---

# 三、总体架构设计

```text
                  Client


                    |
                    |

          API Gateway Runtime


 -------------------------------------------------

 Request Router

 Authentication

 Request Filter

 Response Handler

 Logging

 Exception Handler

 Protocol Adapter


 -------------------------------------------------


                    |


              Backend Services


```

---

# 四、核心模块设计

# 1. Request Router Runtime ⭐⭐⭐⭐⭐

## 作用

根据 API 定义找到目标服务。

例如：

请求：

```http
GET

/openapi/v1/users/100

```

Gateway：

查询：

```
api_definition

```

得到：

```
service:

core-user


target:

http://localhost:9001/users/100

```

然后转发。

---

## 路由模型

新增表：

## api_route

```sql
id

api_id

service_name

target_url

timeout

status

```

示例：

```
api_id:

10001


service:

core-user


target:

http://localhost:8081

```

---

# UX设计

后台：

```
路由管理


API:

GET /users/{id}


目标服务:


core-user


地址:

localhost:8081


状态:

ACTIVE

```

---

# 2. Service Registry Runtime ⭐⭐⭐⭐☆

## 作用

管理后端服务。

例如：

```
core-user

实例:

localhost:8081

localhost:8082

```

---

## 数据模型

service_instance

```sql
id

service_id

host

port

weight

status

heartbeat_time

```

---

MVP：

先不用服务发现。

直接：

```yaml
core-user:
 url:http://localhost:8081

```

即可。

---

企业：

接入：

* Kubernetes Service
* Consul
* Nacos

---

# 3. Request Forward Runtime ⭐⭐⭐⭐⭐

## 核心能力

请求代理。

流程：

```
Client


 |
 |
Gateway


 |
 |
Backend


```

---

支持：

## HTTP Method

```
GET

POST

PUT

DELETE

PATCH

```

---

## Header透传

例如：

客户端：

```
Authorization

X-Request-ID

```

Gateway：

转发。

---

## Body透传

JSON：

```json
{
"name":"Tom"
}

```

---

# 4. Unified Response Runtime ⭐⭐⭐⭐⭐

所有接口统一返回。

后端：

可能：

```json
{
"userId":1
}

```

Gateway转换：

```json
{
 "code":0,

 "message":"success",

 "data":{
    "userId":1
 }
}

```

---

错误统一：

```json
{
"code":50001,

"message":"service unavailable",

"traceId":"xxx"

}

```

---

# 5. Request Context Runtime ⭐⭐⭐⭐⭐

这是非常重要的设计。

每个请求生成上下文：

```
RequestContext

{

requestId,

clientId,

userId,

tenantId,

timestamp

}

```

---

为什么？

未来：

* 日志
* 审计
* 计费
* AI分析

全部依赖。

---

例如：

```
用户A

调用:

AI Chat

消耗:

1000 token

requestId:

abc123

```

未来直接关联。

---

# 6. Exception Runtime ⭐⭐⭐⭐⭐

统一异常。

后端：

```
TimeoutException

DatabaseException

NullException

```

Gateway转换：

```
SERVICE_ERROR

TIMEOUT

INVALID_REQUEST

UNAUTHORIZED

```

---

错误码体系：

建议：

```
10000 系统

20000 用户

30000 权限

40000 参数

50000 服务

```

---

# 7. Gateway Logging Runtime ⭐⭐⭐⭐⭐

所有请求记录。

表：

## api_access_log

```sql
id


request_id


api_id


client_id


request_time


response_time


status_code


cost_time


error_message

```

---

例如：

```
2026-07-15

POST /ai/chat

耗时:

320ms

状态:

200

```

---

未来：

Analytics Runtime

直接使用。

---

# 五、请求生命周期设计

完整流程：

```
Request


  |

Generate RequestId


  |

Route Match


  |

Authentication Check


  |

Permission Check


  |

Forward


  |

Receive Response


  |

Normalize Response


  |

Write Log


  |

Return


```

---

# 六、Gateway UX设计

## 首页 Dashboard

```
API Gateway


--------------------------------

今日请求

1,230,000


成功率

99.98%


平均延迟

120ms


错误

231


--------------------------------


实时流量

████████


```

---

# API流量页面

```
API


GET /users/{id}


调用次数

10000


平均耗时

30ms


错误率

0.01%


```

---

# 路由配置页面

```
Routes


--------------------------------


/api/v1/users/*


↓

core-user


timeout:

3s


状态:

ACTIVE


```

---

# 七、技术实现建议（符合你的路线）

你的目标：

> 简洁、自研、开源、SQLite起步。

所以 MVP：

不要：

❌ Kong

❌ Envoy

❌ Spring Cloud Gateway

---

建议：

## Backend

Spring Boot

模块：

```
core-openapi-gateway


├── router

├── proxy

├── filter

├── context

├── logging

└── exception

```

---

## HTTP Client

Java：

```
WebClient

或者

OkHttp

```

---

## 数据：

SQLite

表：

```
api_route

service_instance

api_access_log

gateway_config

```

---

# 八、与 Phase 0 集成

Phase0：

保存：

```
API Definition


GET /users/{id}

```

Phase1：

增加：

```
Route


/users/{id}

↓

core-user-service

```

关系：

```
API Definition

        +

API Route

        =

Executable API


```

---

# 九、MVP范围

必须：

✅ 请求路由

✅ 服务映射

✅ HTTP代理

✅ 请求ID

✅ 统一响应

✅ 访问日志

✅ 异常处理

---

暂缓：

❌ JWT

❌ OAuth

❌ API Key

❌ 限流

❌ 熔断

❌ 服务发现

这些属于后续 Runtime。

---

# 十、企业级演进路线

最终：

```text
                 Client


                   |

             API Gateway


 ------------------------------------------------

 WAF

 Authentication

 Authorization

 Rate Limit

 Circuit Breaker

 Load Balance

 Cache

 Audit

 Billing


                   |

 ------------------------------------------------


 Services

```

---

# 十一、关键设计原则

## 1. Gateway 不做业务

错误：

```
Gateway里面写用户逻辑
```

正确：

```
Gateway只负责治理
```

---

## 2. Gateway 是平台控制面

它未来管理：

```
流量

权限

成本

安全

生态

```

---

## 3. Request Context 要提前设计

因为未来 AI Agent 调用：

```
Agent

↓

Gateway

↓

API

```

必须知道：

```
谁调用

为什么调用

花费多少

结果如何

```

---

完成 Phase 1 后：

你的 Core Platform 会出现第一个真正的「统一入口」：

```text
                 Application


                     |

              core-openapi


                     |

 ------------------------------------------------

 User

 AI

 Storage

 Workflow

 Notification


```

这一步意味着你的平台开始从：

> 一堆独立 Core 服务

进化为：

> 一个真正的平台操作系统。

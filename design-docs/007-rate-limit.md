# Phase 6：Rate Limit Runtime ⭐⭐⭐⭐⭐

```text
core-openapi

        Phase 6

    Rate Limit Runtime
```

---

# 一、定位

前面的能力：

```text
Phase 0 API Foundation
        |
        | API资产管理

Phase 1 API Gateway
        |
        | 流量入口

Phase 2 API Definition
        |
        | API契约

Phase 3 API Key
        |
        | 调用身份

Phase 4 SDK
        |
        | 开发体验

Phase 5 Security
        |
        | 安全治理

```

现在解决：

> **如何保证开放平台在大量调用下稳定运行。**

---

开放 API 最大的问题：

不是有人不会调用。

而是：

> 有人调用太多。

例如：

你的 AI API：

正常：

```
1000 requests/min
```

某个程序：

```
100000 requests/min
```

结果：

```text
AI服务耗尽

数据库压力

系统雪崩

其他客户受影响
```

---

Rate Limit Runtime 的目标：

```text
保护平台

保护服务

保护客户

控制成本

实现公平使用
```

---

# 二、核心设计理念

不要简单理解：

> 限流 = 拒绝请求

企业级限流：

实际上是：

```text
Traffic Governance

流量治理
```

包括：

* 请求限制
* 并发控制
* 配额管理
* 服务保护
* 优先级调度
* 成本控制

---

# 三、整体架构

```text
                 Client


                    |

                    |


             API Gateway


                    |

                    |


          Rate Limit Runtime


 ------------------------------------------------


 Request Counter

 Token Bucket

 Quota Manager

 Policy Engine

 Priority Manager

 Cost Control


 ------------------------------------------------


                    |


              Core Services

```

---

# 四、核心模块设计

---

# 1. Rate Limit Policy Runtime ⭐⭐⭐⭐⭐

## 定位

定义：

> 什么情况下限制？

---

例如：

AI Chat API：

```text
每分钟:

100次


每天:

10000次

```

---

模型：

```text
Rate Policy

       |

       |

API / Application / Tenant

```

---

## 数据表

### rate_limit_policy

```sql
CREATE TABLE rate_limit_policy
(
 id INTEGER PRIMARY KEY,


 name VARCHAR(100),


 scope VARCHAR(50),


 algorithm VARCHAR(50),


 limit_value INTEGER,


 window_seconds INTEGER,


 status VARCHAR(20),


 created_time DATETIME
);

```

---

scope：

```text
API

APPLICATION

TENANT

USER

IP

```

---

algorithm：

```text
TOKEN_BUCKET

FIXED_WINDOW

SLIDING_WINDOW

CONCURRENT

```

---

示例：

```json
{
"name":"AI Chat Limit",

"scope":"API",

"algorithm":"TOKEN_BUCKET",

"limit":100
}

```

---

# 2. Token Bucket Runtime ⭐⭐⭐⭐⭐

MVP核心算法。

## 原理

桶：

```
容量:

100 tokens


每秒恢复:

10 tokens

```

请求：

消耗：

```
1 token
```

---

流程：

```
Request

 |

Bucket

 |

还有token?

 |

YES

 |

Allow


NO

 |

Reject

```

---

为什么选择 Token Bucket？

因为：

比固定窗口更平滑。

固定窗口：

```
12:00:59

100次


12:01:00

100次


瞬间200
```

---

Token Bucket：

自然控制。

---

# 3. Quota Runtime ⭐⭐⭐⭐⭐

限流：

短期。

Quota：

长期。

---

区别：

## Rate Limit

例如：

```
100 req/min
```

控制瞬时流量。

---

## Quota

例如：

```
100000 req/month
```

控制总量。

---

模型：

```text
Monthly Quota

       |

Application

       |

Usage

```

---

数据表：

### api_quota

```sql
id

application_id

api_id

period

limit_value

used_value

reset_time

```

---

例如：

```
AI Pro套餐

100万token/月

```

---

# 4. Concurrent Limit Runtime ⭐⭐⭐⭐☆

解决：

> 慢请求占满资源。

例如：

AI请求：

平均：

30秒。

用户：

同时发：

10000个。

---

限制：

```
同时执行:

50个

```

---

流程：

```
Request

 |

Acquire Slot

 |

Execute

 |

Release Slot

```

---

适合：

* AI
* 文件处理
* 工作流

---

# 5. Rate Limit Rule Engine ⭐⭐⭐⭐⭐

复杂规则。

例如：

```
如果：

Application=A

并且：

API=AI Chat

那么：

100/min

```

---

规则模型：

```text
Condition

      +

Action

```

---

Example：

```json
{
"condition":

{
"application":"app001",
"api":"ai.chat"
},


"action":

{
"limit":"100/min"
}

}

```

---

# 6. Priority Runtime ⭐⭐⭐⭐☆

企业客户需要优先级。

例如：

客户：

```
Enterprise

Priority=10


Free

Priority=1

```

---

流量紧张：

优先：

```
Enterprise

```

---

模型：

```text
Tenant

 |

Plan

 |

Priority

```

---

# 7. Cost Control Runtime ⭐⭐⭐⭐⭐

特别针对 AI。

因为：

API成本不同。

例如：

普通接口：

```
1 request = 1 unit
```

AI：

```
1 token = cost
```

---

模型：

```text
Usage

 |

Cost

 |

Limit

```

---

例如：

```json
{
"api":"ai.chat",

"cost":

{
"input_token":0.001,

"output_token":0.002

}

}

```

---

# 8. Rate Limit Storage Runtime ⭐⭐⭐⭐⭐

限流需要快速计数。

MVP：

SQLite。

但是注意：

不要每次：

```
select count(*)

```

---

设计：

内存计数：

```
API Counter

Application Counter

```

---

未来：

Redis。

---

你的原则：

前期：

不要 Redis。

所以：

设计：

```text
RateLimitStore Interface


       |

       |

MemoryStore


       |

       |

RedisStore

```

---

未来替换。

---

# 五、完整请求流程

```text
Client


 |

 |

Gateway


 |

 |

Authentication

(Phase5)


 |

 |

Permission

(Phase5)


 |

 |

Rate Limit Check


 |

 |

Token Bucket


 |

 |

Allow?


 |

 +------------+

 |            |

YES          NO


 |            |

Forward      429


 |


Service


 |

Record Usage

```

---

# 六、错误设计

超过限制：

HTTP：

```
429 Too Many Requests
```

---

返回：

```json
{
"code":42901,

"message":"Rate limit exceeded",

"retryAfter":30

}

```

---

Header：

增加：

```
X-RateLimit-Limit

X-RateLimit-Remaining

X-RateLimit-Reset

```

---

# 七、后台 UX 设计

## Rate Dashboard

```text
Rate Control


--------------------------------


Requests/min

1,200


Blocked

30


Top API


AI Chat


--------------------------------

```

---

# 策略管理

```text
Rate Policies


--------------------------------


AI Chat Limit


Scope:

API


Limit:

100/min


Quota:

100000/month


Status:

ACTIVE

```

---

# 应用额度页面

```text
Application


AI Assistant


--------------------------------


Today:


1200 requests


Limit:


10000/day


Remaining:


8800

```

---

# 八、数据库设计

完整：

```text
rate_limit_policy


rate_limit_rule


api_quota


usage_record


rate_limit_event


plan_limit

```

---

关系：

```text
Application

      |

Plan

      |

Policy

      |

API

      |

Usage

```

---

# 九、技术实现建议

符合你的架构：

```
SpringBoot

Vue3

SQLite

```

---

模块：

```
core-openapi-rate


├── policy

├── limiter

├── quota

├── counter

├── usage

└── dashboard

```

---

接口：

```java
interface RateLimiter {


 boolean allow(
     RequestContext ctx
 );


}

```

---

实现：

MVP：

```
TokenBucketLimiter

```

未来：

```
RedisTokenBucketLimiter

```

---

# 十、MVP范围

必须：

✅ Token Bucket

✅ API级限流

✅ Application级限流

✅ Quota统计

✅ 429返回

✅ Usage记录

---

不要做：

❌ 分布式限流

❌ AI智能限流

❌ 全球流量调度

❌ CDN限流

---

# 十一、企业级演进路线

## Level 1

当前：

```
单机限流

API限流

Application限流

```

---

## Level 2

增加：

```
Redis分布式限流

集群一致性

```

---

## Level 3

企业治理：

```
Tenant QoS

Priority Queue

Traffic Shaping

```

---

## Level 4

AI时代：

```
AI Cost Governor


根据：

Token价格

模型负载

客户等级


动态调整

```

---

# 十二、与整个 OpenAPI 平台关系

最终链路：

```text
                Developer


                    |

                  SDK


                    |

                API Key


                    |

                Gateway


                    |

             Security Runtime


                    |

            Rate Limit Runtime


                    |

              Core Services

```

---

# 完成 Phase 6 后：

你的 `core-openapi` 从：

> 可以开放 API

升级为：

> 可以安全、大规模、可控成本地开放 API。

这一步之后，平台才真正具备 SaaS/API 产品化能力。

下一阶段 **Phase 7 Developer Portal** 会把这些底层能力包装成开发者生态入口。

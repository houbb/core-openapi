# Phase 5：Security Runtime ⭐⭐⭐⭐⭐

```text
core-openapi

        Phase 5

    Security Runtime
```

---

# 一、定位

前面的 Runtime：

```text
Phase 0 API Foundation
    |
    | 管理 API 能力

Phase 1 API Gateway
    |
    | 统一流量入口

Phase 2 API Definition
    |
    | API契约描述

Phase 3 API Key
    |
    | 识别调用者

Phase 4 SDK
    |
    | 降低调用成本

```

现在进入企业开放平台最关键的一层：

> **确定调用者能做什么，以及如何保证调用安全。**

---

API Key 解决：

> 你是谁？

Security Runtime 解决：

> 你能做什么？你的请求可信吗？你的数据安全吗？

---

# 二、核心目标

Security Runtime 提供：

```text
身份认证 Authentication

权限控制 Authorization

数据隔离 Data Isolation

请求签名 Signature

安全策略 Security Policy

审计 Audit

```

---

最终：

```text
Application

      |

 API Key / OAuth / JWT

      |

 Security Runtime

      |

 Permission Check

      |

 API Gateway

      |

 Core Service

```

---

# 三、总体架构

```text
                 Security Runtime


 --------------------------------------------------


 Authentication Runtime


 Authorization Runtime


 Policy Runtime


 Tenant Isolation Runtime


 Signature Runtime


 Security Audit Runtime


 Risk Control Runtime


 --------------------------------------------------


                    |

              API Gateway


```

---

# 四、核心模块设计

---

# 1. Authentication Runtime ⭐⭐⭐⭐⭐

## 定位

验证：

> 请求来自谁。

支持多种认证方式。

---

## MVP

基于：

```text
API Key
```

请求：

```http
Authorization:

Bearer sk_live_xxxx

```

流程：

```text
Request

 |

Extract Credential

 |

Validate

 |

Create Identity Context

```

---

## Identity Context

非常重要。

每次请求生成：

```json
{
 "applicationId":"app001",

 "ownerId":"user001",

 "tenantId":"tenant001",

 "authType":"API_KEY"
}

```

---

未来：

支持：

```text
OAuth2

JWT

SAML

OIDC

mTLS

```

---

# 数据模型

## security_identity

```sql
id

type

name

status

created_time

```

类型：

```text
APPLICATION

USER

SERVICE_ACCOUNT

AGENT

```

---

# 2. Authorization Runtime ⭐⭐⭐⭐⭐

## 定位

权限判断。

模型：

```text
Who

  |

Can Do

  |

What Resource

```

---

例如：

Application:

```text
AI Assistant
```

权限：

```text
ai.chat

ai.embedding

```

请求：

```text
POST /ai/chat

```

判断：

```text
ai.chat ?

YES

允许

```

---

# 权限模型设计

采用：

RBAC + Permission。

结构：

```text
Application

      |

Role

      |

Permission

      |

API

```

---

## Role

例如：

```text
AI_USER

ADMIN

READ_ONLY

```

---

## Permission

例如：

```text
user.read

user.write

ai.chat

storage.upload

```

---

数据库：

## security_permission

```sql
id

code

name

description

```

---

## security_role

```sql
id

name

description

```

---

## role_permission

```sql
role_id

permission_id

```

---

## application_role

```sql
application_id

role_id

```

---

# UX设计

权限管理：

```text
Application


AI Assistant


Roles:


☑ AI_USER


Permissions:


✓ ai.chat

✓ ai.embedding

✓ storage.read


```

---

# 3. API Permission Binding Runtime ⭐⭐⭐⭐⭐

## 作用

API绑定权限。

例如：

API：

```text
POST /ai/chat

```

绑定：

```text
ai.chat

```

---

数据：

## api_security_policy

```sql
api_id

required_permission

```

---

效果：

请求：

```text
API:

/ai/chat


Permission:

ai.chat

```

自动校验。

---

# 4. Security Policy Runtime ⭐⭐⭐⭐⭐

## 定位

统一安全规则。

例如：

```text
这个API：

必须HTTPS

必须签名

限制IP

需要审批

```

---

模型：

```text
Security Policy


        |

        |

 API

```

---

数据：

## security_policy

```sql
id

name

type

config_json

status

```

---

策略类型：

```text
AUTH_REQUIRED

SIGN_REQUIRED

IP_WHITE_LIST

TIME_LIMIT

TENANT_CHECK

```

---

# 5. Signature Runtime ⭐⭐⭐⭐☆

## 为什么需要？

API Key：

只能证明：

> 有钥匙。

但是：

请求可能被篡改。

例如：

攻击：

```text
截获请求

修改金额

重新发送

```

---

签名机制：

请求：

```http
POST /payment


X-Signature:

xxxx

X-Timestamp:

123456

```

---

签名：

```text
signature=

HMAC(

secret,

method+path+body+timestamp

)

```

---

验证：

```text
Gateway

 |

计算签名

 |

比较

 |

通过

```

---

应用场景：

* 支付
* 交易
* 企业系统

---

# 6. Tenant Isolation Runtime ⭐⭐⭐⭐⭐

企业 SaaS 必须。

目标：

> 企业之间数据绝对隔离。

---

模型：

```text
Tenant A


 User

 API


 Data


----------------


Tenant B


 User

 API


 Data

```

---

请求上下文：

```json
{
tenantId:"companyA"
}

```

---

数据库：

所有业务表：

增加：

```sql
tenant_id

```

---

例如：

user：

```sql
id

tenant_id

username

```

---

# 7. Security Audit Runtime ⭐⭐⭐⭐⭐

所有安全事件记录。

例如：

```text
谁

什么时候

调用什么

成功失败

原因

```

---

表：

## security_audit_log

```sql
id

identity_id

event_type

resource

result

ip

created_time

```

---

事件：

```text
LOGIN

API_CALL

PERMISSION_DENIED

KEY_REVOKED

POLICY_CHANGED

```

---

# 8. Risk Control Runtime ⭐⭐⭐⭐☆

高级安全。

检测：

异常调用。

例如：

正常：

```text
100 requests/day

```

突然：

```text
100000 requests/min

```

触发：

```text
Risk Alert

```

---

规则：

```text
频率异常

IP异常

地区异常

时间异常

行为异常

```

---

# 五、请求完整安全流程

```text
Client


 |

 |

API Request


 |

 |

Gateway


 |

 |

Authentication

确认身份


 |

 |

Authorization

检查权限


 |

 |

Security Policy

检查规则


 |

 |

Risk Check


 |

 |

Forward


 |

 |

Service


 |

 |

Audit Log

```

---

# 六、后台 UX 设计

## Security Dashboard

```text
Security Center


--------------------------------


今日请求


1,200,000


拒绝请求


120


异常请求


5


权限错误


30


--------------------------------

```

---

# API安全详情

```text
POST /ai/chat


Security


Authentication:

Required


Permission:

ai.chat


Signature:

Optional


Tenant:

Required


```

---

# 七、数据库设计

整体：

```text
security_identity


security_role


security_permission


role_permission


application_role


api_security_policy


security_policy


security_audit_log


security_risk_event

```

---

# 八、技术实现建议

符合你的架构：

```text
Spring Boot

Vue3

SQLite

```

---

模块：

```text
core-openapi-security


├── authentication

├── authorization

├── policy

├── signature

├── tenant

├── audit

└── risk

```

---

# 九、MVP范围

第一版：

必须：

✅ API Key认证

✅ Permission权限

✅ API权限绑定

✅ 安全策略

✅ 审计日志

---

暂缓：

❌ OAuth Server

❌ SSO

❌ mTLS

❌ WAF

❌ AI风险检测

---

# 十、企业级演进路线

## Level 1

当前：

```text
API Key

RBAC

Audit

```

---

## Level 2

增加：

```text
OAuth2

OIDC

SSO

```

---

## Level 3

企业安全：

```text
Zero Trust

mTLS

Device Trust

Risk Engine

```

---

## Level 4

AI时代：

```text
Agent Identity


Agent Permission


Agent Sandbox


Agent Audit

```

---

# 十一、与前面 Runtime 关系

最终调用链：

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

Permission

   |

Policy

   |

Core Service

```

---

# 十二、关键设计原则

## 1. Security 不应该散落在业务服务

错误：

```java
if(user.hasPermission())

```

到处写。

正确：

```text
Gateway

统一治理

```

---

## 2. 权限模型提前设计

因为未来：

你的平台不仅有人调用：

还有：

* Plugin
* Agent
* Workflow
* Enterprise System

权限主体会越来越复杂。

---

## 3. 审计必须第一天存在

开放平台最大的风险：

不是攻击。

而是：

> 出问题后不知道发生了什么。

---

完成 Phase 5 后：

你的 `core-openapi` 已经具备企业开放平台的安全内核：

```text
                 Open Platform


 API

 |

 Gateway

 |

 API Key

 |

 SDK

 |

 Security

 |

 Permission

 |

 Audit

```

下一阶段 **Phase 6 Rate Limit Runtime** 会建立流量治理能力，让平台从「安全开放」进入「可规模化开放」。

# Phase 3：API Key Runtime ⭐⭐⭐⭐⭐

```text
core-openapi

        Phase 3

    API Key Runtime
```

---

# 一、定位

前两个阶段：

```text
Phase 0 API Foundation

解决：
API是什么


Phase 1 API Gateway

解决：
请求怎么进入


Phase 2 API Definition

解决：
API如何被理解和使用

```

但是现在还有一个核心问题：

> **谁可以调用 API？**

API Key Runtime 解决：

```text
身份识别
调用者管理
访问凭证
密钥生命周期
应用管理
```

---

最终形成：

```text
Developer/Application

          |
          |
      API Key

          |
          |

   core-openapi Gateway

          |

   Core Services

```

---

# 二、为什么 API Key 是开放平台核心？

没有 API Key：

任何人：

```http
POST /api/v1/ai/chat
```

都可以调用。

问题：

* 无法知道是谁
* 无法统计
* 无法收费
* 无法限制
* 无法审计

有 API Key：

请求：

```http
POST /api/v1/ai/chat


Authorization:

Bearer sk_xxxxxxxxx

```

平台知道：

```text
调用者:
OpenAI Plugin Demo


所属:
Developer A


权限:
ai.chat


额度:
10000/day


成本:
¥50

```

---

# 三、整体架构

```text
                 Developer


                    |

             Application


                    |

              API Key Runtime


 ------------------------------------------------

 Application Management

 Key Generation

 Key Validation

 Key Rotation

 Key Permission

 Key Lifecycle

 Secret Storage


 ------------------------------------------------


                    |

              API Gateway


                    |

              Core Services

```

---

# 四、核心模块设计

# 1. Application Runtime ⭐⭐⭐⭐⭐

## 定位

API Key 不直接属于用户。

而属于：

> 应用（Application）

为什么？

例如：

开发者：

```text
张三
```

创建：

```text
AI助手App
```

这个 App：

拥有：

```text
API Key
```

---

模型：

```text
User

 |

 |

Application

 |

 |

API Key

 |

 |

API Access

```

---

## 数据表

### openapi_application

```sql
CREATE TABLE openapi_application
(
 id INTEGER PRIMARY KEY,


 app_name VARCHAR(100),


 app_code VARCHAR(100),


 owner_id INTEGER,


 description TEXT,


 status VARCHAR(20),


 created_time DATETIME

);

```

---

示例：

```json
{
"name":"My AI Assistant",

"owner":"user001",

"status":"ACTIVE"
}

```

---

# UX设计

## 应用列表

```text
Developer Center


Applications


--------------------------------


AI Assistant


Owner:

Echo


API Keys:

2


Status:

ACTIVE



--------------------------------


Trading Bot


API Keys:

1

```

---

# 2. API Key Generation Runtime ⭐⭐⭐⭐⭐

## 作用

生成访问凭证。

生成：

```text
sk_live_xxxxxxxxxxxxxxxxx

```

类似：

Stripe API Key设计。

---

## Key结构建议

不要：

```text
123456
```

建议：

```text
sk_live_

随机32字节

```

例如：

```text
sk_live_8a72c9f1xxxx
```

---

组成：

```text
prefix

+

random secret

+

checksum

```

---

# 存储注意

数据库：

不要保存明文。

错误：

```sql
api_key

sk_live_xxxx

```

正确：

```sql
api_key_hash

sha256(key)

```

---

表：

### openapi_api_key

```sql
id


application_id


key_prefix


key_hash


name


status


expire_time


last_used_time


created_time

```

---

# 3. API Key Validation Runtime ⭐⭐⭐⭐⭐

Gateway收到请求：

```http
Authorization:

Bearer sk_live_xxx

```

流程：

```text
Request

 |

Extract Key

 |

Hash Key

 |

Query Database

 |

Validate

 |

Create Context

 |

Forward

```

---

验证：

检查：

```text
✔ Key存在

✔ 未过期

✔ 状态ACTIVE

✔ 所属Application有效

```

---

失败：

返回：

```json
{
"code":40101,

"message":"Invalid API Key"

}

```

---

# 4. Key Lifecycle Runtime ⭐⭐⭐⭐⭐

API Key不是永久。

生命周期：

```text
Created

    |

Active

    |

Expired

    |

Revoked

    |

Deleted

```

---

支持：

## 创建

```text
Generate Key

```

---

## 禁用

```text
Disable

```

---

## 删除

```text
Delete

```

---

## 轮换

非常重要。

流程：

```text
Old Key


    |

Generate New Key


    |

Both Active


    |

Switch


    |

Disable Old

```

---

# 5. Application Permission Runtime ⭐⭐⭐⭐⭐

API Key不能拥有无限权限。

例如：

AI应用：

允许：

```text
ai.chat

ai.embedding

```

禁止：

```text
billing.read

user.admin

```

---

模型：

```text
Application


      |

      |

 Permission


      |

      |

 API

```

---

表：

### api_permission

```sql
id

name

description

```

例如：

```text
ai.chat

storage.upload

user.read

```

---

关联：

### application_permission

```sql
application_id

permission_id

```

---

# 6. Environment Runtime ⭐⭐⭐⭐☆

企业必须区分：

```text
Development

Testing

Production

```

---

Key：

```text
sk_test_xxxx


sk_live_xxxx

```

---

原因：

避免：

测试程序：

调用生产AI。

---

表增加：

```sql
environment

```

---

# 7. API Key Audit Runtime ⭐⭐⭐⭐⭐

所有 Key 使用记录。

表：

### api_key_usage_log

```sql
id


api_key_id


api_id


request_id


ip


timestamp


status

```

---

例如：

```text
2026-07-15 14:00


AI Chat


Application:

Trading Bot


Success


320ms

```

---

# 五、完整调用流程

```text
Developer


 |

创建 Application


 |

生成 API Key


 |

获得:

sk_live_xxxx


 |

调用API


 |

Gateway


 |

验证Key


 |

查询权限


 |

创建RequestContext


 |

调用Core Service


 |

记录日志

```

---

# 六、后台 UX 设计

# Application详情

```text
AI Assistant


--------------------------------


API Keys


+ Create Key



Production


sk_live_xxxxx


Created:

2026-07-15


Last Used:

2 minutes ago



Permissions:


✓ ai.chat

✓ ai.embedding


```

---

# 创建Key流程

Step 1:

```text
选择环境

Production

```

---

Step 2:

选择权限：

```text
☑ ai.chat

☑ storage.read

☐ user.admin

```

---

Step 3:

生成：

```text
sk_live_xxxx


Copy Now

```

提示：

> Secret只显示一次。

---

# 七、数据库设计

完整：

```text
openapi_application


        |

        |

openapi_api_key


        |

        |

application_permission


        |

        |

api_permission


        |

        |

api_key_usage_log

```

---

# 八、技术实现建议

符合你的架构：

```text
Java SpringBoot

Vue3

SQLite

```

---

模块：

```text
core-openapi-key


├── application

├── key

├── permission

├── validator

├── audit

└── crypto

```

---

加密：

MVP:

```text
SHA-256

```

企业：

```text
Hash + Salt

KMS

HSM

```

---

# 九、MVP范围

必须：

✅ Application管理

✅ API Key生成

✅ Key验证

✅ Key禁用

✅ Key过期

✅ 基础权限绑定

✅ 使用记录

---

不要做：

❌ OAuth Server

❌ SSO

❌ Billing

❌ Marketplace

❌ 企业IAM

这些后面。

---

# 十、企业级演进

## 1. OAuth2 Runtime

支持：

```text
第三方登录

授权流程

```

---

## 2. Service Account

企业机器调用：

```text
Robot

Agent

CI/CD

```

---

## 3. Secret Vault

Key存储：

```text
KMS

Vault

HSM

```

---

## 4. API Subscription

未来：

```text
Free

Pro

Enterprise

```

---

# 十一、与前面 Runtime 的关系

完整链路：

```text
API Definition

定义能力


        ↓


API Gateway

接收请求


        ↓


API Key Runtime

识别调用者


        ↓


Permission

决定能不能调用


        ↓


Core Service

执行能力

```

---

# 完成 Phase 3 后，你的平台发生一个重要变化：

之前：

```text
Core能力

只能自己使用

```

之后：

```text
Core能力

可以安全开放给：

开发者

插件

企业

AI Agent

第三方应用

```

这一步是真正从 **内部平台 → 开放生态平台** 的开始。

# Phase 0：API Foundation Runtime ⭐⭐⭐⭐⭐

```text
core-openapi
        |
        |
 API Foundation Runtime
```

## 一、定位

API Foundation Runtime 是 `core-openapi` 的最底层能力。

它不负责：

* 网关转发
* API Key
* 限流
* SDK
* 商业化

它只解决一个核心问题：

> **如何把平台内部能力标准化描述，并形成统一 API 资产。**

---

## 二、设计目标

### MVP目标

实现：

```
服务注册
    ↓
API接口注册
    ↓
接口版本管理
    ↓
接口生命周期管理
    ↓
API元数据查询
```

最终形成：

```
API Catalog
```

类似：

> 企业内部 API 黄页。

---

# 三、整体架构设计

```
                 Admin UI

                    |
                    |

          API Foundation Runtime


 ------------------------------------------------

 API Registry

 API Definition

 API Version

 API Lifecycle

 API Metadata


 ------------------------------------------------


                    |

             Core Services


 ---------------------------------

 core-user

 core-ai

 core-storage

 core-config

 core-workflow

```

---

# 四、核心模块设计

# 1. API Registry Runtime

## 作用

管理：

> 谁提供 API

例如：

```
core-user

core-ai

core-storage
```

都是 API Provider。

---

## 数据模型

### api_service

服务注册表

| 字段           | 说明   |
| ------------ | ---- |
| id           | 主键   |
| service_name | 服务名称 |
| service_code | 唯一编码 |
| description  | 描述   |
| owner        | 负责人  |
| status       | 状态   |
| version      | 服务版本 |
| created_time | 创建时间 |

示例：

```json
{
 "serviceCode":"core-user",
 "name":"用户服务",
 "version":"1.0"
}
```

---

## UI

页面：

```
API服务管理


+ 创建服务


------------------------------------------------

core-user

用户中心服务

版本:
1.0


状态:
运行中


接口数量:
20


------------------------------------------------


core-ai

AI能力服务

接口数量:
15

```

---

# 2. API Definition Runtime

## 作用

描述：

> 一个 API 到底是什么。

例如：

```
GET

/api/users/{id}

查询用户

```

---

## 核心对象

### API Definition

```json
{
"id":1,

"service":"core-user",

"path":"/users/{id}",

"method":"GET",

"name":"getUser",

"description":"查询用户"

}
```

---

## 字段设计

### api_definition

| 字段          | 说明 |
| ----------- | -- |
| id          |    |
| service_id  |    |
| name        |    |
| path        |    |
| http_method |    |
| description |    |
| category    |    |
| status      |    |
| create_time |    |

---

## HTTP Method

支持：

```
GET

POST

PUT

DELETE

PATCH
```

---

# 3. API Parameter Runtime

## 作用

定义输入。

例如：

接口：

```
GET /users/{id}
```

参数：

```
id

类型:
Long

必须:
true

```

---

## 数据表

### api_parameter

```sql
id

api_id

name

location

type

required

description

example

```

location:

```
PATH

QUERY

HEADER

BODY
```

---

## UI

接口详情：

```
GET /users/{id}


Parameters


Path


id
Long
required


Query


page
Integer


Header


Authorization
String

```

---

# 4. API Response Runtime

## 作用

定义返回。

例如：

```json
{
"id":1,

"name":"Tom"
}
```

---

## api_response

```sql
id

api_id

status_code

content_type

schema

example

```

---

支持：

```
200

400

401

500
```

---

# 5. API Version Runtime

## 非常重要

API一定要版本化。

不要：

```
/users
```

应该：

```
/v1/users

/v2/users
```

---

## 数据模型

### api_version

```sql
id

api_id

version

status

release_time

deprecated_time

```

---

状态：

```
DRAFT

ACTIVE

DEPRECATED

DISABLED

```

---

生命周期：

```
创建

 |

Draft

 |

Release

 |

Active

 |

Deprecated

 |

Removed

```

---

# 6. API Lifecycle Runtime

## 管理接口生命周期。

状态机：

```
Draft

 |

Review

 |

Published

 |

Deprecated

 |

Offline

```

---

## 为什么需要？

企业环境：

接口不是：

```
写完马上上线
```

而是：

```
开发

测试

审核

发布

下线

```

---

# 五、核心流程设计

# 流程1：创建API

用户：

```
API管理

↓

创建API

```

填写：

```
所属服务

接口名称

HTTP Method

Path

描述

版本

```

提交：

```
保存Draft

```

---

状态：

```
DRAFT
```

---

# 流程2：发布API

点击：

```
发布
```

系统检查：

```
✔ Path存在

✔ Method存在

✔ 参数完整

✔ Response定义

✔ Version存在

```

通过：

```
ACTIVE
```

---

# 流程3：修改API

不要直接修改。

流程：

```
v1

 |

复制

 |

v2

 |

发布

```

---

# 六、后台管理 UX

## 首页 Dashboard

```
Open API


---------------------------------

API数量

128


服务数量

15


版本数量

230


活跃接口

180


---------------------------------


最近更新


core-ai

新增 chat API


core-user

v2 发布


```

---

# API列表

```
API Catalog


搜索:


[ 用户 ]


------------------------------------------------

GET

/v1/users/{id}


用户查询


core-user


ACTIVE


v1

------------------------------------------------


POST

/v1/users


创建用户


core-user


ACTIVE

```

---

# API详情页

类似开发文档：

```
GET


/users/{id}



描述:

查询用户信息



Request


Path

id


Response


200

{
 id,
 username
}


Example


curl


```

---

# 七、技术实现建议

结合你的技术路线：

```
Java SpringBoot

Vue3

SQLite

```

非常适合。

---

## 后端模块

```
core-openapi


├── controller

├── service

├── repository

├── domain

├── dto

└── migration

```

---

## 数据库

初期：

SQLite

表：

```
api_service

api_definition

api_parameter

api_response

api_version

api_tag

```

---

后期：

MySQL/PostgreSQL

无需改变领域模型。

---

# 八、与其他 Core 集成

## core-user

注册：

```
GET /users
```

---

## core-ai

注册：

```
POST /chat

POST /embedding

```

---

## core-storage

注册：

```
POST /upload

GET /resource/{id}

```

---

# 九、MVP范围控制

第一版不要做：

❌ API Gateway

❌ API Key

❌ OAuth

❌ 限流

❌ SDK生成

❌ Marketplace

否则复杂度爆炸。

---

MVP只做：

✅ API目录

✅ API定义

✅ 参数定义

✅ Response定义

✅ Version管理

✅ 生命周期

---

# 十、关键注意点

## 1. API Definition 是资产

不要把 API 当代码。

代码会变化。

API 是：

```
企业能力地图
```

---

## 2. 一定支持版本

否则未来：

```
改一个接口

所有应用崩溃
```

---

## 3. 元数据优先

未来：

AI Agent 会读取：

```
API Catalog

↓

理解能力

↓

自动调用API

```

所以：

API描述质量非常重要。

---

## 4. 不要绑定技术实现

不要设计：

```
Spring Controller表
```

应该设计：

```
业务API模型
```

未来：

Java/Rust/Go

都可以接入。

---

# 最终 MVP 成果

完成 Phase 0 后，你拥有：

```
                 API Catalog


        core-user
             |
             |
        /users/{id}


        core-ai
             |
             |
        /chat


        core-storage
             |
             |
        /upload


```

平台第一次具备：

> **把内部能力转化为标准化产品能力的基础。**

后续 Phase 1 API Gateway、Phase 2 API Definition增强、Phase 3 API Key 才是在这个基础上自然生长。

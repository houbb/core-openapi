# Phase 2：API Definition Runtime ⭐⭐⭐⭐⭐

```text
core-openapi

        Phase 2

   API Definition Runtime
```

---

# 一、定位

Phase 0 的 API Foundation Runtime 解决：

> API 是什么？在哪里？属于谁？

Phase 1 的 API Gateway Runtime 解决：

> 请求如何进入系统？

Phase 2 的 API Definition Runtime 解决：

> 如何让 API 变成**机器可理解、开发者可使用、AI 可调用的标准能力描述**。

它是整个 Open API 平台的**知识层（API Knowledge Layer）**。

最终目标：

```text
API Code

    ↓

API Definition

    ↓

Developer Documentation

    ↓

SDK Generation

    ↓

AI Agent Tool Calling

```

---

# 二、为什么 API Definition 要独立成为 Runtime？

很多系统认为：

> API 文档 = Swagger 文件

这是不够的。

企业级平台中：

API Definition 不只是文档。

它是：

## 1. 开发者契约

告诉调用者：

```
如何调用
需要什么参数
返回什么结果
```

---

## 2. 自动化基础

未来：

```
AI Agent

读取 API Definition

↓

理解能力

↓

自动调用 API
```

---

## 3. 平台资产

例如：

你的平台有：

```
core-user

100个API


core-ai

50个API


core-storage

30个API
```

API Definition 就是：

> 企业能力地图。

---

# 三、整体架构

```text
                 API Definition Runtime


 ------------------------------------------------


 API Schema Runtime

 Parameter Runtime

 Request Schema Runtime

 Response Schema Runtime

 Example Runtime

 Documentation Runtime

 Validation Runtime


 ------------------------------------------------


                    |


              API Gateway


                    |


              Backend Service

```

---

# 四、核心模块设计

# 1. API Schema Runtime ⭐⭐⭐⭐⭐

## 作用

定义 API 基础信息。

例如：

```
POST /api/v1/users


创建用户
```

---

## 数据模型

### api_definition

```sql
CREATE TABLE api_definition
(
 id INTEGER PRIMARY KEY,

 service_id INTEGER,

 name VARCHAR(100),

 path VARCHAR(255),

 method VARCHAR(10),

 summary VARCHAR(255),

 description TEXT,

 category VARCHAR(100),

 version VARCHAR(20),

 status VARCHAR(20),

 created_time DATETIME
);

```

---

## 示例

```json
{
"name":"createUser",

"path":"/users",

"method":"POST",

"summary":"创建用户",

"version":"v1"
}

```

---

# UX设计

API列表：

```
API Catalog


用户服务


 POST

 /users


创建用户


版本:

v1


状态:

Published

```

---

# 2. Parameter Definition Runtime ⭐⭐⭐⭐⭐

## 作用

定义请求参数。

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

必填:
true

位置:
Path

```

---

## 数据表

### api_parameter

```sql
id

api_id

name

location

data_type

required

default_value

description

example

```

---

支持位置：

```
PATH

QUERY

HEADER

COOKIE

BODY

```

---

## UI

```
Request Parameters


Path


id

Long

required


Query


page

Integer


size

Integer

```

---

# 3. Request Schema Runtime ⭐⭐⭐⭐⭐

## 作用

定义 Body。

例如：

POST:

```
/users
```

Body:

```json
{
"name":"Tom",

"email":"a@test.com"
}

```

---

设计：

不要简单存 JSON。

需要 Schema。

采用：

JSON Schema。

例如：

```json
{
"type":"object",

"properties":{

"name":{
"type":"string"
},

"age":{
"type":"integer"
}

}

}

```

---

## 数据表

### api_request_schema

```
id

api_id

content_type

schema_json

example_json

```

---

# 4. Response Schema Runtime ⭐⭐⭐⭐⭐

定义返回。

例如：

```json
{
"id":1,

"name":"Tom"
}

```

---

数据：

### api_response_schema

```
id

api_id

status_code

schema_json

example_json

description

```

---

支持：

```
200

201

400

401

403

500

```

---

# 5. Example Runtime ⭐⭐⭐⭐☆

## 为什么需要？

开发者最喜欢：

不是理论。

而是：

> 示例。

---

例如：

Request:

```bash
curl

POST /users

{
"name":"Tom"
}

```

Response:

```json
{
"id":100
}

```

---

存储：

### api_example

```
id

api_id

type

content

```

type:

```
REQUEST

RESPONSE

```

---

# 6. Documentation Runtime ⭐⭐⭐⭐⭐

## 目标

自动生成 API 文档。

类似：

Swagger

---

页面：

```
Core API Docs


---------------------


User API


POST /users


创建用户



Parameters


Request Body


Response



Try It


```

---

# 7. API Validation Runtime ⭐⭐⭐⭐☆

## 作用

请求自动校验。

例如：

定义：

```json
{
"name":

required=true
}

```

请求：

```json
{
}

```

Gateway：

返回：

```json
{
code:40001,

message:"name required"

}

```

---

# 五、核心流程设计

# 流程1：创建 API

用户：

```
创建接口

```

填写：

```
基本信息

↓

参数

↓

Request Schema

↓

Response Schema

↓

Example

```

保存：

```
Draft

```

---

# 流程2：发布 API

点击：

```
Publish
```

系统检查：

```
✔ Path

✔ Method

✔ Request

✔ Response

✔ Example

✔ Version

```

成功：

```
Published

```

---

# 流程3：生成文档

自动生成：

```
/docs/api/user

```

---

# 六、后台 UX 设计

## API Explorer

类似：

```
API Explorer


POST


/api/v1/users



Description


创建用户



Request


{
 username:"",
 email:""
}



Response


{
 id:"",
 username:""
}



Try

```

---

## Schema编辑器

不要让用户手写 JSON。

提供：

树形编辑。

例如：

```
User


├── id

│   number


├── name

│   string


└── email

    string

```

---

# 七、数据库设计

完整：

```
api_definition

api_version

api_parameter

api_request_schema

api_response_schema

api_example

api_tag

api_document

```

---

关系：

```
Service

 |

API Definition

 |

Version

 |

Request Schema

 |

Response Schema

 |

Example

```

---

# 八、技术实现建议

你的技术路线：

Java + SpringBoot + Vue3 + SQLite

非常适合。

---

后端：

```
core-openapi-definition


├── controller

├── domain

├── schema

├── validator

├── generator

└── repository

```

---

Schema：

建议：

JSON Schema。

原因：

未来：

* OpenAPI 3
* SDK生成
* AI Tool Calling

都兼容。

---

# 九、MVP范围

必须：

✅ API基本信息

✅ 参数定义

✅ Request Schema

✅ Response Schema

✅ Example

✅ 自动文档

✅ 发布状态

---

不要做：

❌ SDK生成

❌ API市场

❌ AI自动调用

❌ Mock Server

---

# 十、企业级演进

未来增加：

## 1. Mock Runtime

没有后端：

直接模拟：

```
GET /users

返回Example

```

---

## 2. Contract Testing

自动验证：

```
API Definition

是否符合代码实现

```

---

## 3. AI API Understanding

未来：

AI读取：

```
API Definition

↓

生成Tool

↓

Agent调用

```

---

# 十一、与 Phase 0 / 1 的关系

完整链路：

```text
Phase0

API Foundation

记录:

有什么API


        ↓


Phase2

API Definition

描述:

API如何使用


        ↓


Phase1

Gateway

执行:

API请求


```

最终：

```text
API Definition

        +

Gateway

        +

Security

        +

Rate Limit


=

Enterprise Open API Platform

```

---

# 完成 Phase 2 后，你的平台会出现一个非常关键的能力：

> **任何 Core 服务都可以被“描述、理解、展示、调用”。**

这也是后续：

* SDK Runtime
* Developer Portal
* AI Agent Runtime
* Plugin Marketplace

能够建立的基础。

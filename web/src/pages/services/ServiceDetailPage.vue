<template>
  <div class="page">
    <div class="page-header">
      <div>
        <router-link to="/services" style="font-size: 13px; color: var(--accent);">← 服务列表</router-link>
        <h1 style="margin-top: 8px;">{{ service?.serviceName || '加载中...' }}</h1>
      </div>
      <button class="btn btn-primary" @click="showCreateDef = true">+ 新建 API</button>
    </div>

    <div class="card" v-if="loading">
      <div class="loading">加载中...</div>
    </div>

    <div class="card" v-else-if="definitions.length === 0">
      <div class="empty-state">
        <h3>暂无接口定义</h3>
        <p>点击上方按钮添加第一个 API</p>
      </div>
    </div>

    <div class="card" v-else>
      <table>
        <thead>
          <tr>
            <th>方法</th>
            <th>路径</th>
            <th>名称</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="def in definitions" :key="def.id">
            <td>
              <span class="method-badge" :class="`method-${def.httpMethod}`">{{ def.httpMethod }}</span>
            </td>
            <td><code>{{ def.path }}</code></td>
            <td>
              <router-link :to="`/definitions/${def.id}`" style="font-weight: 600;">
                {{ def.name }}
              </router-link>
            </td>
            <td>
              <span class="badge" :class="`badge-${def.status}`">{{ def.status }}</span>
            </td>
            <td>
              <button class="btn btn-sm" @click="editDefinition(def)">编辑</button>
              <button class="btn btn-sm btn-danger" @click="confirmDelete(def)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Create/Edit Definition Dialog -->
    <div class="dialog-overlay" v-if="showCreateDef" @click.self="showCreateDef = false">
      <div class="dialog">
        <h2>{{ editing ? '编辑 API' : '新建 API' }}</h2>
        <div class="form-group">
          <label>接口名称 *</label>
          <input class="form-input" v-model="defForm.name" placeholder="如：getUser" />
        </div>
        <div class="form-group">
          <label>路径 *</label>
          <input class="form-input" v-model="defForm.path" placeholder="如：/users/{id}" />
        </div>
        <div class="form-group">
          <label>HTTP 方法 *</label>
          <select class="form-select" v-model="defForm.httpMethod">
            <option value="GET">GET</option>
            <option value="POST">POST</option>
            <option value="PUT">PUT</option>
            <option value="DELETE">DELETE</option>
            <option value="PATCH">PATCH</option>
          </select>
        </div>
        <div class="form-group">
          <label>描述</label>
          <input class="form-input" v-model="defForm.description" placeholder="接口描述" />
        </div>
        <div class="form-group">
          <label>分类</label>
          <input class="form-input" v-model="defForm.category" placeholder="分类" />
        </div>
        <div class="dialog-actions">
          <button class="btn" @click="showCreateDef = false">取消</button>
          <button class="btn btn-primary" @click="submitDef">{{ editing ? '保存' : '创建' }}</button>
        </div>
      </div>
    </div>

    <!-- Delete Confirm -->
    <div class="dialog-overlay" v-if="showDelete" @click.self="showDelete = false">
      <div class="dialog">
        <h2>确认删除</h2>
        <p>确定要删除 API "{{ deletingDef?.name }}" 吗？</p>
        <div class="dialog-actions">
          <button class="btn" @click="showDelete = false">取消</button>
          <button class="btn btn-danger" @click="doDelete">删除</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getService, type ServiceItem } from '@/api/services'
import {
  getDefinitionsByService, createDefinition, updateDefinition, deleteDefinition,
  type DefinitionItem,
} from '@/api/definitions'

const route = useRoute()
const id = Number(route.params.id)

const service = ref<ServiceItem | null>(null)
const definitions = ref<DefinitionItem[]>([])
const loading = ref(true)

const showCreateDef = ref(false)
const editing = ref(false)
const defForm = ref<Partial<DefinitionItem>>({})
let editDefId = 0

const showDelete = ref(false)
const deletingDef = ref<DefinitionItem | null>(null)

async function loadData() {
  loading.value = true
  try {
    const [svcRes, defRes] = await Promise.all([
      getService(id),
      getDefinitionsByService(id, { page: 1, size: 100 }),
    ])
    service.value = svcRes.data
    definitions.value = defRes.data.items
  } finally {
    loading.value = false
  }
}

function editDefinition(def: DefinitionItem) {
  editing.value = true
  editDefId = def.id
  defForm.value = { ...def }
  showCreateDef.value = true
}

async function submitDef() {
  try {
    if (editing.value) {
      await updateDefinition(editDefId, { ...defForm.value, serviceId: id })
    } else {
      await createDefinition({ ...defForm.value, serviceId: id })
    }
    showCreateDef.value = false
    editing.value = false
    defForm.value = {}
    loadData()
  } catch (err: any) {
    alert(err.response?.data?.detail || '操作失败')
  }
}

function confirmDelete(def: DefinitionItem) {
  deletingDef.value = def
  showDelete.value = true
}

async function doDelete() {
  if (deletingDef.value) {
    await deleteDefinition(deletingDef.value.id)
    showDelete.value = false
    loadData()
  }
}

onMounted(loadData)
</script>
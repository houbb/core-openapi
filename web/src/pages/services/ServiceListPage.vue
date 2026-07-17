<template>
  <div class="page">
    <div class="page-header">
      <h1>服务管理</h1>
      <button class="btn btn-primary" @click="showCreate = true">+ 创建服务</button>
    </div>

    <div class="search-bar">
      <input
        v-model="keyword"
        class="form-input"
        placeholder="搜索服务名称或编码..."
        @keyup.enter="loadData"
      />
      <button class="btn" @click="loadData">搜索</button>
    </div>

    <div class="card" v-if="loading">
      <div class="loading">加载中...</div>
    </div>

    <div class="card" v-else-if="services.length === 0">
      <div class="empty-state">
        <h3>暂无服务</h3>
        <p>点击上方按钮创建第一个 API 服务</p>
      </div>
    </div>

    <div class="card" v-else>
      <table>
        <thead>
          <tr>
            <th>服务名称</th>
            <th>编码</th>
            <th>版本</th>
            <th>状态</th>
            <th>负责人</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="svc in services" :key="svc.id">
            <td>
              <router-link :to="`/services/${svc.id}`" style="font-weight: 600;">
                {{ svc.serviceName }}
              </router-link>
            </td>
            <td><code>{{ svc.serviceCode }}</code></td>
            <td>{{ svc.version }}</td>
            <td>
              <span class="badge" :class="`badge-${svc.status}`">{{ svc.status }}</span>
            </td>
            <td>{{ svc.owner || '-' }}</td>
            <td>
              <button class="btn btn-sm" @click="editService(svc)">编辑</button>
              <button class="btn btn-sm btn-danger" @click="confirmDelete(svc)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>

      <div style="margin-top: 12px; text-align: center; color: var(--text-secondary); font-size: 12px;">
        共 {{ total }} 条
        <button class="btn btn-sm" :disabled="page <= 1" @click="page--; loadData()">上一页</button>
        <button class="btn btn-sm" :disabled="!hasNext" @click="page++; loadData()">下一页</button>
      </div>
    </div>

    <!-- Create/Edit Dialog -->
    <div class="dialog-overlay" v-if="showCreate" @click.self="showCreate = false">
      <div class="dialog">
        <h2>{{ editing ? '编辑服务' : '创建服务' }}</h2>
        <div class="form-group">
          <label>服务名称 *</label>
          <input class="form-input" v-model="form.serviceName" placeholder="如：用户服务" />
        </div>
        <div class="form-group">
          <label>服务编码 *</label>
          <input class="form-input" v-model="form.serviceCode" placeholder="如：core-user" />
        </div>
        <div class="form-group">
          <label>描述</label>
          <input class="form-input" v-model="form.description" placeholder="服务描述" />
        </div>
        <div class="form-group">
          <label>负责人</label>
          <input class="form-input" v-model="form.owner" placeholder="负责人" />
        </div>
        <div class="form-group">
          <label>版本</label>
          <input class="form-input" v-model="form.version" placeholder="1.0" />
        </div>
        <div class="dialog-actions">
          <button class="btn" @click="showCreate = false">取消</button>
          <button class="btn btn-primary" @click="submitForm">{{ editing ? '保存' : '创建' }}</button>
        </div>
      </div>
    </div>

    <!-- Delete Confirm -->
    <div class="dialog-overlay" v-if="showDelete" @click.self="showDelete = false">
      <div class="dialog">
        <h2>确认删除</h2>
        <p>确定要删除服务 "{{ deletingItem?.serviceName }}" 吗？此操作不可撤销。</p>
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
import { getServices, createService, updateService, deleteService, type ServiceItem } from '@/api/services'

const services = ref<ServiceItem[]>([])
const loading = ref(true)
const page = ref(1)
const total = ref(0)
const hasNext = ref(false)
const keyword = ref('')

const showCreate = ref(false)
const editing = ref(false)
const form = ref<Partial<ServiceItem>>({})
let editId = 0

const showDelete = ref(false)
const deletingItem = ref<ServiceItem | null>(null)

async function loadData() {
  loading.value = true
  try {
    const { data } = await getServices({ page: page.value, size: 20, keyword: keyword.value || undefined })
    services.value = data.items
    total.value = data.total
    hasNext.value = data.hasNext
  } finally {
    loading.value = false
  }
}

function editService(svc: ServiceItem) {
  editing.value = true
  editId = svc.id
  form.value = { ...svc }
  showCreate.value = true
}

async function submitForm() {
  try {
    if (editing.value) {
      await updateService(editId, form.value)
    } else {
      await createService(form.value)
    }
    showCreate.value = false
    editing.value = false
    form.value = {}
    loadData()
  } catch (err: any) {
    alert(err.response?.data?.detail || '操作失败')
  }
}

function confirmDelete(svc: ServiceItem) {
  deletingItem.value = svc
  showDelete.value = true
}

async function doDelete() {
  if (deletingItem.value) {
    try {
      await deleteService(deletingItem.value.id)
      showDelete.value = false
      loadData()
    } catch (err: any) {
      alert(err.response?.data?.detail || '删除失败')
    }
  }
}

onMounted(loadData)
</script>
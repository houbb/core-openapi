<template>
  <div class="page">
    <div class="page-header">
      <h1>应用管理</h1>
      <button class="btn btn-primary" @click="showCreateDialog = true">+ 创建应用</button>
    </div>

    <div class="table-wrapper">
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>应用名称</th>
            <th>编码</th>
            <th>状态</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="app in apps" :key="app.id">
            <td>{{ app.id }}</td>
            <td>
              <router-link :to="`/applications/${app.id}`" class="link">{{ app.appName }}</router-link>
            </td>
            <td>{{ app.appCode }}</td>
            <td><span class="badge" :class="app.status === 'ACTIVE' ? 'badge-success' : 'badge-secondary'">{{ app.status }}</span></td>
            <td>{{ app.createTime }}</td>
            <td>
              <button class="btn btn-sm" @click="editApp(app)">编辑</button>
              <button class="btn btn-sm btn-danger" @click="handleDelete(app.id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="apps.length === 0" class="empty">暂无应用</div>
    </div>

    <!-- Create/Edit Dialog -->
    <div v-if="showCreateDialog" class="modal-overlay" @click.self="closeDialog">
      <div class="modal">
        <h3>{{ editingApp ? '编辑应用' : '创建应用' }}</h3>
        <div class="form-group">
          <label>应用名称</label>
          <input v-model="form.appName" class="input" placeholder="请输入应用名称" />
        </div>
        <div class="form-group">
          <label>应用编码</label>
          <input v-model="form.appCode" class="input" placeholder="唯一编码（如 my-ai-app）" />
        </div>
        <div class="form-group">
          <label>所属用户ID</label>
          <input v-model.number="form.ownerId" type="number" class="input" placeholder="用户ID（可选）" />
        </div>
        <div class="form-group">
          <label>描述</label>
          <textarea v-model="form.description" class="input" rows="2" placeholder="描述（可选）"></textarea>
        </div>
        <div class="form-group">
          <label>状态</label>
          <select v-model="form.status" class="input">
            <option value="ACTIVE">ACTIVE</option>
            <option value="INACTIVE">INACTIVE</option>
          </select>
        </div>
        <div class="form-actions">
          <button class="btn" @click="closeDialog">取消</button>
          <button class="btn btn-primary" @click="handleSave">{{ editingApp ? '更新' : '创建' }}</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getApplications, createApplication, updateApplication, deleteApplication, type ApplicationItem } from '@/api/apikey'

const apps = ref<ApplicationItem[]>([])
const showCreateDialog = ref(false)
const editingApp = ref<ApplicationItem | null>(null)

const form = ref({
  appName: '',
  appCode: '',
  ownerId: null as number | null,
  description: '',
  status: 'ACTIVE',
})

async function loadApps() {
  try {
    const { data } = await getApplications({ page: 1, size: 100 })
    apps.value = data.items
  } catch { /* ignore */ }
}

function editApp(app: ApplicationItem) {
  editingApp.value = app
  form.value = {
    appName: app.appName,
    appCode: app.appCode,
    ownerId: app.ownerId,
    description: app.description,
    status: app.status,
  }
  showCreateDialog.value = true
}

function closeDialog() {
  showCreateDialog.value = false
  editingApp.value = null
  form.value = { appName: '', appCode: '', ownerId: null, description: '', status: 'ACTIVE' }
}

async function handleSave() {
  try {
    const payload = {
      appName: form.value.appName,
      appCode: form.value.appCode,
      ownerId: form.value.ownerId ?? undefined,
      description: form.value.description,
      status: form.value.status,
    }
    if (editingApp.value) {
      await updateApplication(editingApp.value.id, payload)
    } else {
      await createApplication(payload)
    }
    closeDialog()
    await loadApps()
  } catch (e: any) {
    alert(e.response?.data?.detail || '操作失败')
  }
}

async function handleDelete(id: number) {
  if (!confirm('确定删除该应用？这将同时删除该应用下的所有API Key和权限。')) return
  try {
    await deleteApplication(id)
    await loadApps()
  } catch (e: any) {
    alert(e.response?.data?.detail || '删除失败')
  }
}

onMounted(loadApps)
</script>
<template>
  <div class="page">
    <div class="page-header">
      <div>
        <router-link to="/applications" class="back-link">&larr; 返回应用列表</router-link>
        <h1 v-if="app">{{ app.appName }}</h1>
      </div>
    </div>

    <div v-if="app" class="content">
      <!-- App Info -->
      <section class="section">
        <h2>基本信息</h2>
        <div class="info-grid">
          <div class="info-item"><label>ID</label><span>{{ app.id }}</span></div>
          <div class="info-item"><label>应用名称</label><span>{{ app.appName }}</span></div>
          <div class="info-item"><label>应用编码</label><span>{{ app.appCode }}</span></div>
          <div class="info-item"><label>描述</label><span>{{ app.description || '-' }}</span></div>
          <div class="info-item"><label>状态</label><span class="badge" :class="app.status === 'ACTIVE' ? 'badge-success' : 'badge-secondary'">{{ app.status }}</span></div>
          <div class="info-item"><label>创建时间</label><span>{{ app.createTime }}</span></div>
        </div>
      </section>

      <!-- API Keys -->
      <section class="section">
        <div class="section-header">
          <h2>API Keys</h2>
          <button class="btn btn-primary" @click="showKeyDialog = true">+ 创建 Key</button>
        </div>
        <div v-if="keys.length > 0">
          <table class="table">
            <thead>
              <tr>
                <th>名称</th>
                <th>前缀</th>
                <th>环境</th>
                <th>状态</th>
                <th>过期时间</th>
                <th>最后使用</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="key in keys" :key="key.id">
                <td>{{ key.name || '-' }}</td>
                <td><code>{{ key.keyPrefix }}...</code></td>
                <td><span class="badge" :class="key.environment === 'LIVE' ? 'badge-warning' : 'badge-secondary'">{{ key.environment }}</span></td>
                <td><span class="badge" :class="key.status === 'ACTIVE' ? 'badge-success' : 'badge-danger'">{{ key.status }}</span></td>
                <td>{{ key.expireTime || '永不过期' }}</td>
                <td>{{ key.lastUsedTime || '-' }}</td>
                <td>
                  <button v-if="key.status === 'ACTIVE'" class="btn btn-sm btn-warning" @click="handleDisable(key.id)">禁用</button>
                  <button v-else-if="key.status === 'DISABLED'" class="btn btn-sm" @click="handleEnable(key.id)">启用</button>
                  <button class="btn btn-sm btn-danger" @click="handleDeleteKey(key.id)">删除</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div v-else class="empty">暂无 API Key</div>
      </section>

      <!-- Permissions -->
      <section class="section">
        <div class="section-header">
          <h2>权限</h2>
          <button class="btn btn-primary" @click="showPermDialog = true">+ 授予权限</button>
        </div>
        <div v-if="appPermissions.length > 0" class="perm-list">
          <span v-for="perm in appPermissions" :key="perm.id" class="perm-tag">
            {{ perm.name }}
            <button class="perm-remove" @click="handleRevoke(perm.id)">&times;</button>
          </span>
        </div>
        <div v-else class="empty">暂无权限</div>
      </section>
    </div>

    <!-- Create Key Dialog -->
    <div v-if="showKeyDialog" class="modal-overlay" @click.self="showKeyDialog = false">
      <div class="modal">
        <h3>创建 API Key</h3>
        <div class="form-group">
          <label>名称</label>
          <input v-model="keyForm.name" class="input" placeholder="例如：生产环境Key" />
        </div>
        <div class="form-group">
          <label>环境</label>
          <select v-model="keyForm.environment" class="input">
            <option value="LIVE">LIVE (生产)</option>
            <option value="TEST">TEST (测试)</option>
          </select>
        </div>
        <div class="form-group">
          <label>过期时间（可选）</label>
          <input v-model="keyForm.expireTime" class="input" type="datetime-local" />
        </div>
        <div class="form-actions">
          <button class="btn" @click="showKeyDialog = false">取消</button>
          <button class="btn btn-primary" @click="handleCreateKey">生成</button>
        </div>
      </div>
    </div>

    <!-- Generated Key Display -->
    <div v-if="newRawKey" class="modal-overlay" @click.self="newRawKey = null">
      <div class="modal">
        <h3>API Key 已生成</h3>
        <div class="alert alert-warning">
          ⚠️ 此 Key 仅显示一次，请立即复制保存！
        </div>
        <div class="key-display">
          <code>{{ newRawKey }}</code>
          <button class="btn btn-sm" @click="copyKey">复制</button>
        </div>
        <div class="form-actions">
          <button class="btn btn-primary" @click="newRawKey = null; loadKeys()">完成</button>
        </div>
      </div>
    </div>

    <!-- Grant Permission Dialog -->
    <div v-if="showPermDialog" class="modal-overlay" @click.self="showPermDialog = false">
      <div class="modal">
        <h3>授予权限</h3>
        <div class="form-group">
          <label>选择权限</label>
          <select v-model="selectedPermId" class="input">
            <option v-for="perm in allPermissions" :key="perm.id" :value="perm.id">
              {{ perm.name }} — {{ perm.description }}
            </option>
          </select>
        </div>
        <div class="form-actions">
          <button class="btn" @click="showPermDialog = false">取消</button>
          <button class="btn btn-primary" @click="handleGrant">授予</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import {
  getApplication, getApiKeys, createApiKey, disableApiKey, enableApiKey, deleteApiKey,
  getApplicationPermissions, grantPermission, revokePermission,
  getPermissions,
  type ApplicationItem, type ApiKeyItem, type PermissionItem,
} from '@/api/apikey'

const route = useRoute()
const appId = Number(route.params.id)

const app = ref<ApplicationItem | null>(null)
const keys = ref<ApiKeyItem[]>([])
const appPermissions = ref<PermissionItem[]>([])
const allPermissions = ref<PermissionItem[]>([])

const showKeyDialog = ref(false)
const showPermDialog = ref(false)
const newRawKey = ref<string | null>(null)
const selectedPermId = ref<number | null>(null)

const keyForm = ref({
  name: '',
  environment: 'LIVE',
  expireTime: '',
})

async function loadApp() {
  try {
    const { data } = await getApplication(appId)
    app.value = data
  } catch { /* ignore */ }
}

async function loadKeys() {
  try {
    const { data } = await getApiKeys(appId)
    keys.value = data
  } catch { /* ignore */ }
}

async function loadAppPermissions() {
  try {
    const { data } = await getApplicationPermissions(appId)
    appPermissions.value = data
  } catch { /* ignore */ }
}

async function loadAllPermissions() {
  try {
    const { data } = await getPermissions({ page: 1, size: 100 })
    allPermissions.value = data.items
  } catch { /* ignore */ }
}

async function handleCreateKey() {
  try {
    const { data } = await createApiKey(appId, {
      environment: keyForm.value.environment,
      name: keyForm.value.name,
      expireTime: keyForm.value.expireTime ? new Date(keyForm.value.expireTime).toISOString() : undefined,
    })
    showKeyDialog.value = false
    keyForm.value = { name: '', environment: 'LIVE', expireTime: '' }
    newRawKey.value = data.rawKey || null
    await loadKeys()
  } catch (e: any) {
    alert(e.response?.data?.detail || '创建失败')
  }
}

async function handleDisable(keyId: number) {
  try {
    await disableApiKey(appId, keyId)
    await loadKeys()
  } catch { /* ignore */ }
}

async function handleEnable(keyId: number) {
  try {
    await enableApiKey(appId, keyId)
    await loadKeys()
  } catch { /* ignore */ }
}

async function handleDeleteKey(keyId: number) {
  if (!confirm('确定删除该 API Key？')) return
  try {
    await deleteApiKey(appId, keyId)
    await loadKeys()
  } catch { /* ignore */ }
}

async function handleGrant() {
  if (!selectedPermId.value) return
  try {
    const { data } = await grantPermission(appId, selectedPermId.value)
    appPermissions.value = data
    showPermDialog.value = false
    selectedPermId.value = null
  } catch (e: any) {
    alert(e.response?.data?.detail || '授予失败')
  }
}

async function handleRevoke(permId: number) {
  try {
    const { data } = await revokePermission(appId, permId)
    appPermissions.value = data
  } catch { /* ignore */ }
}

function copyKey() {
  if (newRawKey.value) {
    navigator.clipboard.writeText(newRawKey.value)
    alert('已复制到剪贴板')
  }
}

onMounted(() => {
  loadApp()
  loadKeys()
  loadAppPermissions()
  loadAllPermissions()
})
</script>

<style scoped>
.back-link {
  color: var(--accent);
  font-size: 13px;
  text-decoration: none;
  margin-bottom: 8px;
  display: inline-block;
}
.content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.info-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}
.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.info-item label {
  font-size: 12px;
  color: var(--text-secondary);
}
.perm-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.perm-tag {
  background: var(--accent-bg);
  color: var(--accent);
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 6px;
}
.perm-remove {
  background: none;
  border: none;
  color: var(--accent);
  cursor: pointer;
  font-size: 16px;
  padding: 0;
  line-height: 1;
}
.key-display {
  background: var(--bg-tertiary);
  padding: 16px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 12px 0;
}
.key-display code {
  flex: 1;
  font-size: 14px;
  word-break: break-all;
}
.alert {
  padding: 12px;
  border-radius: 8px;
  font-size: 13px;
}
.alert-warning {
  background: #fff3cd;
  color: #856404;
  border: 1px solid #ffeeba;
}
</style>
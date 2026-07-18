<template>
  <div class="page">
    <div class="page-header">
      <h1>📝 审计日志</h1>
      <p class="page-subtitle">企业级操作审计追踪（复用 security_audit_log）</p>
    </div>

    <div class="filters">
      <select v-model="filterEventType" class="input" @change="load">
        <option value="">全部事件</option>
        <option value="AUTH_SUCCESS">认证成功</option>
        <option value="AUTH_FAILURE">认证失败</option>
        <option value="PERMISSION_DENIED">权限拒绝</option>
        <option value="API_CALL">API调用</option>
        <option value="KEY_CREATED">Key创建</option>
        <option value="KEY_REVOKED">Key吊销</option>
      </select>
      <select v-model="filterResult" class="input" @change="load">
        <option value="">全部结果</option>
        <option value="SUCCESS">成功</option>
        <option value="FAILURE">失败</option>
        <option value="DENIED">拒绝</option>
      </select>
      <input v-model="filterIdentity" placeholder="身份标识..." class="input" @keyup.enter="load" />
    </div>

    <table class="table">
      <thead>
        <tr><th>ID</th><th>事件类型</th><th>身份</th><th>资源</th><th>结果</th><th>IP</th><th>租户</th><th>时间</th></tr>
      </thead>
      <tbody>
        <tr v-for="log in items" :key="log.id">
          <td>{{ log.id }}</td>
          <td><span class="badge">{{ log.eventType }}</span></td>
          <td>{{ log.identityId }}</td>
          <td>{{ log.resourceType }}/{{ log.resourceId }}</td>
          <td><span :class="['badge', log.result === 'SUCCESS' ? 'badge-success' : 'badge-danger']">{{ log.result }}</span></td>
          <td>{{ log.ip }}</td>
          <td>{{ log.tenantId || '-' }}</td>
          <td>{{ log.createdTime }}</td>
        </tr>
      </tbody>
    </table>

    <div class="pagination" v-if="total > size">
      <button :disabled="page <= 1" @click="page--; load()">上一页</button>
      <span>{{ page }} / {{ Math.ceil(total / size) }}</span>
      <button :disabled="page >= Math.ceil(total / size)" @click="page++; load()">下一页</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import apiClient from '@/api/client'

interface AuditLog {
  id: number
  eventType: string
  identityId: string
  resourceType: string
  resourceId: string
  result: string
  ip: string
  tenantId: string
  createdTime: string
}

const items = ref<AuditLog[]>([])
const page = ref(1), size = ref(20), total = ref(0)
const filterEventType = ref(''), filterResult = ref(''), filterIdentity = ref('')

const load = async () => {
  try {
    const { data } = await apiClient.get('/enterprise/audit/logs', {
      params: { page: page.value, size: size.value, eventType: filterEventType.value, result: filterResult.value, identity: filterIdentity.value }
    })
    items.value = data.items; total.value = data.total
  } catch { /* ignore */ }
}

onMounted(load)
</script>
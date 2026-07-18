<template>
  <div class="page">
    <div class="page-header">
      <h1>🔍 API 治理</h1>
      <p class="page-subtitle">API生命周期管理与合规检查</p>
    </div>

    <div class="filters">
      <input v-model="keyword" placeholder="搜索API名称..." class="input" @keyup.enter="load" />
      <select v-model="filterStatus" class="input" @change="load">
        <option value="">全部状态</option>
        <option value="DRAFT">草稿</option>
        <option value="REVIEW">审核中</option>
        <option value="APPROVED">已批准</option>
        <option value="PUBLISHED">已发布</option>
        <option value="DEPRECATED">已废弃</option>
      </select>
    </div>

    <table class="table">
      <thead>
        <tr><th>ID</th><th>名称</th><th>路径</th><th>方法</th><th>生命周期</th><th>治理标签</th><th>操作</th></tr>
      </thead>
      <tbody>
        <tr v-for="api in items" :key="api.id">
          <td>{{ api.id }}</td>
          <td>{{ api.name }}</td>
          <td><code>{{ api.path }}</code></td>
          <td><span class="badge">{{ api.httpMethod }}</span></td>
          <td><span :class="['badge', lifecycleClass(api.lifecycleStatus || api.status)]">{{ api.lifecycleStatus || api.status }}</span></td>
          <td>{{ api.governanceTags || '-' }}</td>
          <td class="actions">
            <button v-if="canTransitionTo(api, 'REVIEW')" class="btn btn-sm" @click="transition(api.id, 'REVIEW')">提交审核</button>
            <button v-if="canTransitionTo(api, 'APPROVED')" class="btn btn-sm" @click="transition(api.id, 'APPROVED')">批准</button>
            <button v-if="canTransitionTo(api, 'PUBLISHED')" class="btn btn-sm btn-primary" @click="transition(api.id, 'PUBLISHED')">发布</button>
            <button v-if="canTransitionTo(api, 'DEPRECATED')" class="btn btn-sm btn-warning" @click="transition(api.id, 'DEPRECATED')">废弃</button>
          </td>
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
import { getDefinitions, type Definition } from '@/api/definitions'

const items = ref<(Definition & { lifecycleStatus?: string; governanceTags?: string })[]>([])
const page = ref(1), size = ref(20), total = ref(0)
const keyword = ref(''), filterStatus = ref('')

const load = async () => {
  try {
    const { data } = await getDefinitions({ page: page.value, size: size.value, keyword: keyword.value })
    items.value = data.items
    total.value = data.total
  } catch { /* ignore */ }
}

const TRANSITIONS: Record<string, string[]> = {
  DRAFT: ['REVIEW'],
  REVIEW: ['APPROVED', 'DRAFT'],
  APPROVED: ['PUBLISHED', 'DRAFT'],
  PUBLISHED: ['DEPRECATED'],
  DEPRECATED: ['DRAFT'],
}

const canTransitionTo = (api: any, target: string) => {
  const current = api.lifecycleStatus || api.status || 'DRAFT'
  return TRANSITIONS[current]?.includes(target) || false
}

const transition = async (id: number, status: string) => {
  try {
    // Use the existing definition update endpoint to change lifecycle_status
    await fetch(`/api/v1/openapi/definitions/${id}/lifecycle?status=${status}`, { method: 'POST' })
    load()
  } catch { /* ignore */ }
}

const lifecycleClass = (s: string) => {
  const map: Record<string, string> = { DRAFT: '', REVIEW: 'badge-warning', APPROVED: 'badge-info', PUBLISHED: 'badge-success', DEPRECATED: 'badge-danger' }
  return map[s] || ''
}

onMounted(load)
</script>
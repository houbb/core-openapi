<template>
  <div class="page">
    <div class="page-header">
      <h1>API 目录</h1>
      <button class="btn btn-primary" @click="goCreate">+ 新建 API</button>
    </div>

    <div class="search-bar">
      <select class="form-select" style="max-width: 200px;" v-model="statusFilter" @change="loadData">
        <option value="">全部状态</option>
        <option value="DRAFT">DRAFT</option>
        <option value="REVIEW">REVIEW</option>
        <option value="PUBLISHED">PUBLISHED</option>
        <option value="DEPRECATED">DEPRECATED</option>
        <option value="OFFLINE">OFFLINE</option>
      </select>
      <input class="form-input" v-model="keyword" placeholder="搜索名称、路径或描述..." @keyup.enter="loadData" />
      <button class="btn" @click="loadData">搜索</button>
    </div>

    <div class="card" v-if="loading"><div class="loading">加载中...</div></div>
    <div class="card" v-else-if="items.length === 0">
      <div class="empty-state"><h3>暂无 API</h3></div>
    </div>
    <div class="card" v-else>
      <table>
        <thead>
          <tr>
            <th>方法</th>
            <th>路径</th>
            <th>名称</th>
            <th>服务</th>
            <th>分类</th>
            <th>状态</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in items" :key="item.id" style="cursor: pointer;" @click="$router.push(`/definitions/${item.id}`)">
            <td><span class="method-badge" :class="`method-${item.httpMethod}`">{{ item.httpMethod }}</span></td>
            <td><code>{{ item.path }}</code></td>
            <td style="font-weight: 600;">{{ item.name }}</td>
            <td style="color: var(--text-secondary);">{{ item.serviceId }}</td>
            <td>{{ item.category || '-' }}</td>
            <td><span class="badge" :class="`badge-${item.status}`">{{ item.status }}</span></td>
          </tr>
        </tbody>
      </table>
      <div style="margin-top: 12px; text-align: center; color: var(--text-secondary); font-size: 12px;">
        共 {{ total }} 条
        <button class="btn btn-sm" :disabled="page <= 1" @click="page--; loadData()">上一页</button>
        <button class="btn btn-sm" :disabled="!hasNext" @click="page++; loadData()">下一页</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getDefinitions, type DefinitionItem } from '@/api/definitions'

const router = useRouter()

const items = ref<DefinitionItem[]>([])
const loading = ref(true)
const page = ref(1)
const total = ref(0)
const hasNext = ref(false)
const keyword = ref('')
const statusFilter = ref('')

async function loadData() {
  loading.value = true
  try {
    const { data } = await getDefinitions({
      page: page.value, size: 20,
      keyword: keyword.value || undefined,
      status: statusFilter.value || undefined,
    })
    items.value = data.items
    total.value = data.total
    hasNext.value = data.hasNext
  } finally {
    loading.value = false
  }
}

function goCreate() {
  router.push('/services')
}

onMounted(loadData)
</script>
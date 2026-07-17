<template>
  <div class="page">
    <div class="page-header">
      <h1>仪表盘</h1>
    </div>

    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-value">{{ stats.serviceCount }}</div>
        <div class="stat-label">服务数量</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ stats.definitionCount }}</div>
        <div class="stat-label">API 接口</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ stats.publishedCount }}</div>
        <div class="stat-label">已发布</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getDashboard } from '@/api/definitions'

const stats = ref({
  serviceCount: 0,
  definitionCount: 0,
  publishedCount: 0,
})

onMounted(async () => {
  try {
    const { data } = await getDashboard()
    stats.value = data as any
  } catch {
    // ignore
  }
})
</script>
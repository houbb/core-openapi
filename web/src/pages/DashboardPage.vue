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
        <div class="stat-value">{{ stats.userCount }}</div>
        <div class="stat-label">用户数量</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ stats.applicationCount }}</div>
        <div class="stat-label">应用数量</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ stats.publishedCount || 0 }}</div>
        <div class="stat-label">已发布 API</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ stats.activeKeyCount || 0 }}</div>
        <div class="stat-label">活跃 Key</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getDashboardStats } from '@/api/apikey'

const stats = ref({
  serviceCount: 0,
  definitionCount: 0,
  publishedCount: 0,
  userCount: 0,
  applicationCount: 0,
  activeKeyCount: 0,
})

onMounted(async () => {
  try {
    const { data } = await getDashboardStats()
    stats.value = data as any
  } catch {
    // ignore
  }
})
</script>
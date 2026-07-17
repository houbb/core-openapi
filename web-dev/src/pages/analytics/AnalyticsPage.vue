<template>
  <div class="page">
    <h1>Analytics</h1>
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-value">{{ stats.todayCalls }}</div>
        <div class="stat-label">Today Calls</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ stats.monthCalls }}</div>
        <div class="stat-label">Month Calls</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ stats.avgLatencyMs }}ms</div>
        <div class="stat-label">Avg Latency</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ stats.errorRate }}%</div>
        <div class="stat-label">Error Rate</div>
      </div>
    </div>
    <div v-if="stats.byApi && stats.byApi.length" style="margin-top:32px;">
      <h3>By API</h3>
      <div v-for="item in stats.byApi" :key="item.apiName" class="api-row">
        <span>{{ item.apiName }}</span>
        <span>{{ item.callCount }} calls</span>
        <span>{{ item.avgLatencyMs }}ms avg</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import apiClient from '@/api/client'

const stats = ref<any>({
  todayCalls: 0, monthCalls: 0, avgLatencyMs: 0, errorRate: 0, byApi: []
})

onMounted(async () => {
  try {
    const { data } = await apiClient.get('/analytics/overview')
    stats.value = data
  } catch { /* ignore */ }
})
</script>

<style scoped>
.page { padding: 32px; }
h1 { font-size: 24px; margin-bottom: 24px; }
.stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }
.stat-card { background: var(--bg-primary); border: 1px solid var(--border); border-radius: 12px; padding: 24px; text-align: center; }
.stat-value { font-size: 28px; font-weight: 700; color: var(--accent); }
.stat-label { font-size: 13px; color: var(--text-secondary); margin-top: 4px; }
.api-row { display: flex; justify-content: space-between; padding: 12px 0; border-bottom: 1px solid var(--border); font-size: 14px; }
h3 { margin-bottom: 12px; }
@media (max-width: 768px) { .stats-grid { grid-template-columns: repeat(2, 1fr); } }
</style>
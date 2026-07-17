<template>
  <div class="page">
    <h1>Dashboard</h1>
    <div class="stats-grid">
      <div class="stat-card" v-for="s in stats" :key="s.label">
        <div class="stat-value">{{ s.value }}</div>
        <div class="stat-label">{{ s.label }}</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import apiClient from '@/api/client'

const stats = ref([
  { label: 'Applications', value: '-' },
  { label: 'API Keys', value: '-' },
  { label: 'Subscriptions', value: '-' },
  { label: 'Today Calls', value: '-' },
])

onMounted(async () => {
  try {
    const { data } = await apiClient.get('/dashboard')
    stats.value = [
      { label: 'Applications', value: data.applicationCount },
      { label: 'API Keys', value: data.apiKeyCount },
      { label: 'Subscriptions', value: data.subscriptionCount },
      { label: 'Today Calls', value: data.todayCalls },
    ]
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
@media (max-width: 768px) { .stats-grid { grid-template-columns: repeat(2, 1fr); } }
</style>
<template>
  <div class="page">
    <h1>SDK Center</h1>
    <div class="sdk-grid">
      <div v-for="sdk in sdks" :key="sdk.id" class="sdk-card">
        <h3>{{ sdk.name }}</h3>
        <div class="sdk-meta">
          <span>{{ sdk.language }}</span>
          <span>v{{ sdk.version }}</span>
          <span class="badge" :class="sdk.status">{{ sdk.status }}</span>
        </div>
        <button class="btn-primary" style="margin-top:12px;">Download</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import apiClient from '@/api/client'

const sdks = ref<any[]>([])

onMounted(async () => {
  try {
    const { data } = await apiClient.get('/sdk')
    sdks.value = data || []
  } catch { /* ignore */ }
})
</script>

<style scoped>
.page { padding: 32px; }
h1 { font-size: 24px; margin-bottom: 24px; }
.sdk-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 16px; }
.sdk-card { background: var(--bg-primary); border: 1px solid var(--border); border-radius: 12px; padding: 24px; }
.sdk-card h3 { font-size: 17px; margin-bottom: 8px; }
.sdk-meta { display: flex; gap: 12px; font-size: 13px; color: var(--text-secondary); }
.badge { padding: 2px 8px; border-radius: 4px; font-size: 11px; }
.badge.READY { background: #c6f6d5; color: #22543d; }
.btn-primary { background: var(--accent); color: #fff; border: none; border-radius: 6px; padding: 8px 16px; cursor: pointer; }
</style>
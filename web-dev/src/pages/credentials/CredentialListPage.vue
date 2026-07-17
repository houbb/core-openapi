<template>
  <div class="page">
    <h1>Credentials</h1>
    <div v-if="credentials.length" class="cred-list">
      <div v-for="c in credentials" :key="c.id" class="cred-row">
        <div>
          <strong>{{ c.name }}</strong>
          <code>{{ c.keyPrefix }}****</code>
        </div>
        <div class="cred-meta">
          <span class="badge" :class="c.status">{{ c.status }}</span>
          <span>{{ c.environment }}</span>
          <button class="btn-sm" @click="revoke(c.id)">Revoke</button>
        </div>
      </div>
    </div>
    <p v-else style="text-align:center;color:var(--text-tertiary);padding:40px;">No credentials. Create an application first, then generate API keys.</p>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import apiClient from '@/api/client'

const credentials = ref<any[]>([])

async function load() {
  try {
    const { data: apps } = await apiClient.get('/apps')
    const items = apps.items || []
    for (const app of items) {
      const { data: creds } = await apiClient.get(`/apps/${app.id}/credentials`)
      credentials.value.push(...(creds || []))
    }
  } catch { /* ignore */ }
}

async function revoke(id: number) {
  await apiClient.post(`/credentials/${id}/revoke`)
  credentials.value = credentials.value.filter(c => c.id !== id)
}

onMounted(load)
</script>

<style scoped>
.page { padding: 32px; }
h1 { font-size: 24px; margin-bottom: 24px; }
.cred-list { display: flex; flex-direction: column; gap: 8px; }
.cred-row { display: flex; justify-content: space-between; align-items: center; padding: 16px; background: var(--bg-primary); border: 1px solid var(--border); border-radius: 8px; }
.cred-row code { font-family: monospace; font-size: 12px; margin-left: 12px; }
.cred-meta { display: flex; align-items: center; gap: 12px; font-size: 13px; }
.badge { padding: 2px 8px; border-radius: 4px; font-size: 11px; }
.badge.ACTIVE { background: #c6f6d5; color: #22543d; }
.badge.DISABLED { background: #fed7d7; color: #742a2a; }
.btn-sm { padding: 4px 10px; font-size: 12px; border: 1px solid var(--border); border-radius: 4px; background: var(--bg-primary); cursor: pointer; }
</style>
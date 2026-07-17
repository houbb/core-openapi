<template>
  <div class="page">
    <button class="back-btn" @click="$router.back()">&larr; Back</button>
    <div v-if="app" class="detail-card">
      <h1>{{ app.appName }}</h1>
      <p>{{ app.description }}</p>
      <div class="meta-grid">
        <div><strong>App Code</strong>: {{ app.appCode }}</div>
        <div><strong>Status</strong>: {{ app.status }}</div>
        <div><strong>Keys</strong>: {{ app.keyCount }}</div>
        <div><strong>Subscriptions</strong>: {{ app.subscriptionCount }}</div>
      </div>

      <div style="margin-top:24px;">
        <h3>API Keys</h3>
        <button class="btn-primary" @click="showKeyCreate = true">+ Generate Key</button>
        <div v-if="keys.length" style="margin-top:12px;">
          <div v-for="key in keys" :key="key.id" class="key-row">
            <span>{{ key.name }}</span>
            <code>{{ key.keyPrefix }}****</code>
            <span class="badge" :class="key.status">{{ key.status }}</span>
            <button class="btn-sm" @click="revokeKey(key.id)">Revoke</button>
          </div>
        </div>
        <p v-if="rawKey" class="raw-key">🔑 Your API Key: <code>{{ rawKey }}</code> (copy now — won't be shown again)</p>
      </div>

      <div v-if="showKeyCreate" class="modal-overlay" @click.self="showKeyCreate = false">
        <div class="modal">
          <h2>Generate API Key</h2>
          <form @submit.prevent="generateKey">
            <div class="form-group"><label>Name</label><input v-model="keyForm.name" class="input" required /></div>
            <div class="form-group"><label>Environment</label>
              <select v-model="keyForm.environment" class="input">
                <option value="DEVELOPMENT">Development</option>
                <option value="PRODUCTION">Production</option>
              </select>
            </div>
            <button type="submit" class="btn-primary" style="width:100%;padding:10px;">Generate</button>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import apiClient from '@/api/client'

const route = useRoute()
const app = ref<any>(null)
const keys = ref<any[]>([])
const showKeyCreate = ref(false)
const keyForm = ref({ name: '', environment: 'DEVELOPMENT' })
const rawKey = ref('')

async function load() {
  const id = route.params.id
  const { data } = await apiClient.get(`/apps/${id}`)
  app.value = data
  const { data: creds } = await apiClient.get(`/apps/${id}/credentials`)
  keys.value = creds || []
}

async function generateKey() {
  const id = route.params.id
  const { data } = await apiClient.post(`/apps/${id}/credentials`, keyForm.value)
  rawKey.value = data.rawKey
  showKeyCreate.value = false
  load()
}

async function revokeKey(keyId: number) {
  await apiClient.post(`/credentials/${keyId}/revoke`)
  load()
}

onMounted(load)
</script>

<style scoped>
.page { padding: 32px; }
.back-btn { background: none; border: none; color: var(--accent); cursor: pointer; font-size: 14px; margin-bottom: 20px; }
.detail-card { background: var(--bg-primary); border: 1px solid var(--border); border-radius: 12px; padding: 32px; }
h1 { font-size: 24px; margin-bottom: 12px; }
.meta-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 8px; font-size: 13px; margin-top: 16px; }
.key-row { display: flex; align-items: center; gap: 12px; padding: 10px 0; border-bottom: 1px solid var(--border); font-size: 13px; }
.badge { padding: 2px 8px; border-radius: 4px; font-size: 11px; }
.badge.ACTIVE { background: #c6f6d5; color: #22543d; }
.badge.DISABLED { background: #fed7d7; color: #742a2a; }
.raw-key { margin-top: 12px; padding: 12px; background: #fefcbf; border-radius: 8px; font-size: 13px; }
.raw-key code { font-family: monospace; }
.btn-primary { background: var(--accent); color: #fff; border: none; border-radius: 6px; padding: 8px 16px; cursor: pointer; font-size: 14px; }
.btn-sm { padding: 4px 10px; font-size: 12px; border: 1px solid var(--border); border-radius: 4px; background: var(--bg-primary); cursor: pointer; }
.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,.4); display: flex; justify-content: center; align-items: center; z-index: 200; }
.modal { background: var(--bg-primary); border-radius: 12px; padding: 32px; max-width: 420px; width: 100%; }
.modal h2 { font-size: 18px; margin-bottom: 20px; }
.form-group { margin-bottom: 16px; }
.form-group label { display: block; font-size: 13px; margin-bottom: 4px; }
.input { width: 100%; padding: 8px 12px; border: 1px solid var(--border); border-radius: 6px; font-size: 14px; box-sizing: border-box; }
</style>
<template>
  <div class="page">
    <h1>API Playground</h1>
    <div class="playground">
      <div class="input-panel">
        <div class="row">
          <select v-model="method" class="method-select">
            <option>GET</option><option>POST</option><option>PUT</option><option>DELETE</option>
          </select>
          <input v-model="url" class="url-input" placeholder="/api/v1/..." />
          <button class="btn-primary" @click="execute">Send</button>
        </div>
        <div class="form-group">
          <label>API Key</label>
          <input v-model="apiKey" class="input" placeholder="sk_live_..." />
        </div>
        <div class="form-group">
          <label>Request Body</label>
          <textarea v-model="body" class="body-input" rows="8" placeholder='{"key": "value"}'></textarea>
        </div>
      </div>
      <div class="response-panel" v-if="response">
        <div class="resp-header">
          <span class="status" :class="{ success: response.statusCode < 400 }">Status: {{ response.statusCode }}</span>
          <span>Latency: {{ response.latencyMs }}ms</span>
        </div>
        <pre class="resp-body">{{ response.body }}</pre>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import apiClient from '@/api/client'

const method = ref('GET')
const url = ref('')
const apiKey = ref('')
const body = ref('')
const response = ref<any>(null)

async function execute() {
  const { data } = await apiClient.post('/playground/execute', {
    method: method.value,
    url: url.value,
    apiKey: apiKey.value,
    body: body.value,
  })
  response.value = data
}
</script>

<style scoped>
.page { padding: 32px; }
h1 { font-size: 24px; margin-bottom: 24px; }
.playground { display: flex; gap: 24px; }
.input-panel { flex: 1; }
.response-panel { flex: 1; background: var(--bg-primary); border: 1px solid var(--border); border-radius: 12px; padding: 20px; }
.row { display: flex; gap: 8px; margin-bottom: 16px; }
.method-select { padding: 8px; border: 1px solid var(--border); border-radius: 6px; }
.url-input { flex: 1; padding: 8px 12px; border: 1px solid var(--border); border-radius: 6px; font-size: 14px; }
.form-group { margin-bottom: 16px; }
.form-group label { display: block; font-size: 13px; margin-bottom: 4px; }
.input { width: 100%; padding: 8px 12px; border: 1px solid var(--border); border-radius: 6px; font-size: 14px; box-sizing: border-box; }
.body-input { width: 100%; padding: 12px; border: 1px solid var(--border); border-radius: 6px; font-family: monospace; font-size: 13px; box-sizing: border-box; }
.resp-header { display: flex; gap: 16px; font-size: 13px; margin-bottom: 12px; }
.status { font-weight: 600; }
.status.success { color: #22543d; }
.resp-body { background: var(--bg-secondary); padding: 16px; border-radius: 8px; font-family: monospace; font-size: 12px; white-space: pre-wrap; overflow-x: auto; }
.btn-primary { background: var(--accent); color: #fff; border: none; border-radius: 6px; padding: 8px 20px; cursor: pointer; }
@media (max-width: 768px) { .playground { flex-direction: column; } }
</style>
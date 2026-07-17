<template>
  <div class="page">
    <h1>Settings</h1>
    <div class="card" style="max-width: 480px;">
      <div class="form-group">
        <label>Language</label>
        <select v-model="form.language" class="input">
          <option value="zh-CN">中文</option>
          <option value="en-US">English</option>
        </select>
      </div>
      <div class="form-group">
        <label>Theme</label>
        <select v-model="form.theme" class="input">
          <option value="light">Light</option>
          <option value="dark">Dark</option>
        </select>
      </div>
      <div class="form-group">
        <label><input type="checkbox" v-model="form.notifyEmail" /> Email Notifications</label>
      </div>
      <div class="form-group">
        <label><input type="checkbox" v-model="form.notifyUsage" /> Usage Alerts</label>
      </div>
      <button class="btn-primary" @click="save">Save Settings</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import apiClient from '@/api/client'

const form = ref({ language: 'zh-CN', theme: 'light', notifyEmail: true, notifyUsage: true })

onMounted(async () => {
  try {
    const { data } = await apiClient.get('/settings')
    Object.assign(form.value, data)
  } catch { /* ignore */ }
})

async function save() {
  await apiClient.put('/settings', form.value)
}
</script>

<style scoped>
.page { padding: 32px; }
h1 { font-size: 24px; margin-bottom: 24px; }
.card { background: var(--bg-primary); border: 1px solid var(--border); border-radius: 12px; padding: 32px; }
.form-group { margin-bottom: 16px; }
.form-group label { font-size: 14px; }
.input { width: 100%; padding: 8px 12px; border: 1px solid var(--border); border-radius: 6px; font-size: 14px; box-sizing: border-box; }
.btn-primary { background: var(--accent); color: #fff; border: none; border-radius: 6px; padding: 10px 24px; cursor: pointer; font-size: 14px; }
</style>
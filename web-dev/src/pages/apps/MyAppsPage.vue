<template>
  <div class="page">
    <div class="page-header">
      <h1>My Applications</h1>
      <button class="btn-primary" @click="showCreate = true">+ Create App</button>
    </div>

    <div v-if="apps.length" class="app-grid">
      <div v-for="app in apps" :key="app.id" class="app-card" @click="goDetail(app.id)">
        <h3>{{ app.appName }}</h3>
        <p>{{ app.description }}</p>
        <div class="app-meta">Keys: {{ app.keyCount }} · Status: {{ app.status }}</div>
      </div>
    </div>
    <p v-else style="text-align:center;color:var(--text-tertiary);padding:40px;">No applications yet. Create your first app!</p>

    <div v-if="showCreate" class="modal-overlay" @click.self="showCreate = false">
      <div class="modal">
        <h2>Create Application</h2>
        <form @submit.prevent="create">
          <div class="form-group"><label>Name</label><input v-model="form.appName" class="input" required /></div>
          <div class="form-group"><label>Description</label><input v-model="form.description" class="input" /></div>
          <p v-if="error" class="error">{{ error }}</p>
          <button type="submit" class="btn-primary" style="width:100%;padding:10px;">Create</button>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import apiClient from '@/api/client'

const router = useRouter()
const apps = ref<any[]>([])
const showCreate = ref(false)
const form = ref({ appName: '', description: '' })
const error = ref('')

async function load() {
  const { data } = await apiClient.get('/apps')
  apps.value = data.items || []
}

async function create() {
  try {
    error.value = ''
    await apiClient.post('/apps', form.value)
    showCreate.value = false
    form.value = { appName: '', description: '' }
    load()
  } catch (e: any) { error.value = e.response?.data?.detail || 'Failed' }
}

function goDetail(id: number) { router.push(`/app/apps/${id}`) }

onMounted(load)
</script>

<style scoped>
.page { padding: 32px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
h1 { font-size: 24px; }
.app-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 16px; }
.app-card { background: var(--bg-primary); border: 1px solid var(--border); border-radius: 12px; padding: 20px; cursor: pointer; }
.app-card:hover { box-shadow: 0 2px 8px rgba(0,0,0,.06); }
.app-card h3 { font-size: 15px; margin-bottom: 6px; }
.app-card p { font-size: 13px; color: var(--text-secondary); }
.app-meta { font-size: 12px; color: var(--text-tertiary); margin-top: 8px; }
.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,.4); display: flex; justify-content: center; align-items: center; z-index: 200; }
.modal { background: var(--bg-primary); border-radius: 12px; padding: 32px; max-width: 420px; width: 100%; }
.modal h2 { font-size: 18px; margin-bottom: 20px; }
.form-group { margin-bottom: 16px; }
.form-group label { display: block; font-size: 13px; margin-bottom: 4px; }
.input { width: 100%; padding: 8px 12px; border: 1px solid var(--border); border-radius: 6px; font-size: 14px; box-sizing: border-box; }
.error { color: #e53e3e; font-size: 13px; margin-bottom: 12px; }
.btn-primary { background: var(--accent); color: #fff; border: none; border-radius: 6px; padding: 8px 16px; cursor: pointer; font-size: 14px; }
</style>
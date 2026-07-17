<template>
  <div class="page">
    <div class="page-header">
      <h1>API Catalog</h1>
      <input v-model="keyword" class="search-input" placeholder="Search APIs..." @input="search" />
    </div>
    <div class="api-grid">
      <div v-for="api in apis" :key="api.id" class="api-card" @click="goDetail(api.id)">
        <span class="method-badge" :class="api.method">{{ api.method }}</span>
        <h3>{{ api.name }}</h3>
        <p>{{ api.description }}</p>
        <div class="api-meta">{{ api.serviceName }} · {{ api.category }}</div>
      </div>
    </div>
    <p v-if="apis.length === 0" style="text-align:center;color:var(--text-tertiary);padding:40px;">No APIs found</p>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import apiClient from '@/api/client'

const router = useRouter()
const apis = ref<any[]>([])
const keyword = ref('')

async function search() {
  const { data } = await apiClient.get('/catalog', { params: { keyword: keyword.value, size: 50 } })
  apis.value = data.items || []
}

function goDetail(id: number) { router.push(`/catalog/${id}`) }

onMounted(() => search())
</script>

<style scoped>
.page { padding: 32px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
h1 { font-size: 24px; }
.search-input { padding: 8px 16px; border: 1px solid var(--border); border-radius: 8px; font-size: 14px; width: 280px; }
.api-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 16px; }
.api-card { background: var(--bg-primary); border: 1px solid var(--border); border-radius: 12px; padding: 20px; cursor: pointer; transition: box-shadow 0.15s; }
.api-card:hover { box-shadow: 0 2px 8px rgba(0,0,0,.06); }
.method-badge { display: inline-block; padding: 2px 8px; border-radius: 4px; font-size: 11px; font-weight: 600; margin-bottom: 8px; }
.method-badge.GET { background: #e6fffa; color: #234e52; }
.method-badge.POST { background: #fefcbf; color: #744210; }
.method-badge.PUT { background: #bee3f8; color: #2a4365; }
.method-badge.DELETE { background: #fed7d7; color: #742a2a; }
.api-card h3 { font-size: 15px; margin-bottom: 6px; }
.api-card p { font-size: 13px; color: var(--text-secondary); margin-bottom: 8px; }
.api-meta { font-size: 12px; color: var(--text-tertiary); }
@media (max-width: 768px) { .api-grid { grid-template-columns: 1fr; } }
</style>
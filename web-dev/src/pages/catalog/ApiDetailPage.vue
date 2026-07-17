<template>
  <div class="page">
    <button class="back-btn" @click="$router.back()">&larr; Back</button>
    <div v-if="api" class="detail-card">
      <h1>{{ api.name }}</h1>
      <span class="method-badge" :class="api.method">{{ api.method }}</span>
      <code class="endpoint">{{ api.path }}</code>
      <p class="desc">{{ api.description }}</p>
      <div class="meta-grid">
        <div><strong>Service</strong>: {{ api.serviceName }}</div>
        <div><strong>Category</strong>: {{ api.category }}</div>
        <div><strong>Status</strong>: {{ api.status }}</div>
        <div><strong>SDK</strong>: {{ api.sdkAvailability }}</div>
      </div>
      <div style="margin-top: 24px; display: flex; gap: 12px;">
        <a :href="`/#/app/playground/${api.id}`" class="btn-primary">Try Now</a>
        <button class="btn-secondary">Subscribe</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import apiClient from '@/api/client'

const route = useRoute()
const api = ref<any>(null)

onMounted(async () => {
  const id = route.params.id
  const { data } = await apiClient.get(`/catalog/${id}`)
  api.value = data
})
</script>

<style scoped>
.page { padding: 32px; }
.back-btn { background: none; border: none; color: var(--accent); cursor: pointer; font-size: 14px; margin-bottom: 20px; }
.detail-card { background: var(--bg-primary); border: 1px solid var(--border); border-radius: 12px; padding: 32px; }
h1 { font-size: 24px; margin-bottom: 12px; }
.method-badge { display: inline-block; padding: 2px 8px; border-radius: 4px; font-size: 11px; font-weight: 600; margin-right: 8px; }
.method-badge.GET { background: #e6fffa; color: #234e52; }
.method-badge.POST { background: #fefcbf; color: #744210; }
.method-badge.PUT { background: #bee3f8; color: #2a4365; }
.method-badge.DELETE { background: #fed7d7; color: #742a2a; }
.endpoint { font-family: monospace; background: var(--bg-secondary); padding: 4px 8px; border-radius: 4px; font-size: 13px; }
.desc { margin: 16px 0; color: var(--text-secondary); font-size: 14px; }
.meta-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 8px; font-size: 13px; }
.btn-primary { background: var(--accent); color: #fff; padding: 8px 20px; border: none; border-radius: 6px; text-decoration: none; cursor: pointer; }
.btn-secondary { background: var(--bg-secondary); border: 1px solid var(--border); padding: 8px 20px; border-radius: 6px; cursor: pointer; }
</style>
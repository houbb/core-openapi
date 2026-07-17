<template>
  <div class="page">
    <h1>Documentation</h1>
    <div class="docs-layout">
      <aside class="docs-sidebar">
        <a v-for="cat in categories" :key="cat" :href="`/#/docs`"
           class="cat-link" @click.prevent="loadCategory(cat)">
          {{ cat }}
        </a>
      </aside>
      <div class="docs-content">
        <router-view />
        <div v-if="!$route.params.slug">
          <p>Select a document from the sidebar to read.</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import apiClient from '@/api/client'

const router = useRouter()
const categories = ref<string[]>(['GETTING_STARTED', 'API_REFERENCE', 'SDK_GUIDE', 'FAQ', 'CHANGELOG'])
const docs = ref<any[]>([])

async function loadCategory(cat: string) {
  try {
    const { data } = await apiClient.get(`/docs/category/${cat}`)
    docs.value = data || []
    if (docs.value.length > 0) {
      router.push(`/docs/${docs.value[0].slug}`)
    }
  } catch { /* ignore */ }
}
</script>

<style scoped>
.page { padding: 32px; }
h1 { font-size: 24px; margin-bottom: 24px; }
.docs-layout { display: flex; gap: 32px; }
.docs-sidebar { width: 200px; display: flex; flex-direction: column; gap: 8px; }
.cat-link { padding: 8px 12px; border-radius: 6px; color: var(--text-secondary); text-decoration: none; font-size: 14px; cursor: pointer; }
.cat-link:hover { background: var(--bg-secondary); color: var(--text-primary); }
.docs-content { flex: 1; }
</style>
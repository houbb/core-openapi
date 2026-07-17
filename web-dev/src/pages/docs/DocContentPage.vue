<template>
  <div v-if="doc" class="doc-content" v-html="renderedContent"></div>
  <div v-else style="text-align:center;padding:40px;color:var(--text-tertiary);">Loading...</div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import apiClient from '@/api/client'

const route = useRoute()
const doc = ref<any>(null)
const renderedContent = computed(() => {
  if (!doc.value) return ''
  // Simple markdown rendering — in production, use a proper markdown library
  return doc.value.content
    .replace(/^### (.+)$/gm, '<h3>$1</h3>')
    .replace(/^## (.+)$/gm, '<h2>$1</h2>')
    .replace(/^# (.+)$/gm, '<h1>$1</h1>')
    .replace(/```(\w+)\n([\s\S]*?)```/g, '<pre><code>$2</code></pre>')
    .replace(/`([^`]+)`/g, '<code>$1</code>')
    .replace(/\*\*([^*]+)\*\*/g, '<strong>$1</strong>')
    .replace(/\[([^\]]+)\]\(([^)]+)\)/g, '<a href="$2">$1</a>')
    .replace(/\n/g, '<br/>')
})

async function load(slug: string) {
  try {
    const { data } = await apiClient.get(`/docs/${slug}`)
    doc.value = data
  } catch { doc.value = null }
}

onMounted(() => { if (route.params.slug) load(route.params.slug as string) })
watch(() => route.params.slug, (s) => { if (s) load(s as string) })
</script>

<style scoped>
.doc-content { max-width: 720px; line-height: 1.7; }
.doc-content :deep(h1) { font-size: 28px; margin: 24px 0 16px; }
.doc-content :deep(h2) { font-size: 21px; margin: 20px 0 12px; }
.doc-content :deep(h3) { font-size: 17px; margin: 16px 0 8px; }
.doc-content :deep(pre) { background: var(--bg-secondary); padding: 16px; border-radius: 8px; overflow-x: auto; }
.doc-content :deep(code) { font-family: monospace; font-size: 13px; }
.doc-content :deep(a) { color: var(--accent); }
</style>
<template>
  <div class="page">
    <div class="page-header">
      <h1>Provider 详情 — #{{ id }}</h1>
    </div>

    <div v-if="provider">
      <div class="card">
        <h3>{{ provider.name }}</h3>
        <p>{{ provider.description }}</p>
        <p>类型: {{ provider.type }} | 认证: {{ provider.verified ? '✅' : '⬜' }} | 状态: {{ provider.status }}</p>
        <p>邮箱: {{ provider.contactEmail }} | 网站: {{ provider.website }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getProvider } from '@/api/marketplace'
import type { Provider } from '@/api/marketplace'

const route = useRoute()
const id = Number(route.params.id)
const provider = ref<Provider | null>(null)

onMounted(async () => { try { const { data } = await getProvider(id); provider.value = data } catch { /* */ } })
</script>

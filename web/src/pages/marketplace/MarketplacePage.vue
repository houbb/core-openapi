<template>
  <div class="page">
    <div class="page-header">
      <h1>🏪 Marketplace</h1>
    </div>

    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-value">{{ stats.totalProducts }}</div>
        <div class="stat-label">商品总数</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ stats.publishedProducts }}</div>
        <div class="stat-label">已发布</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ stats.pendingReview }}</div>
        <div class="stat-label">待审核</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ stats.totalProviders }}</div>
        <div class="stat-label">Provider 数</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getAdminDashboard } from '@/api/marketplace'

const stats = ref({
  totalProducts: 0,
  publishedProducts: 0,
  pendingReview: 0,
  totalProviders: 0,
  verifiedProviders: 0,
})

onMounted(async () => {
  try {
    const { data } = await getAdminDashboard()
    stats.value = data
  } catch { /* ignore */ }
})
</script>

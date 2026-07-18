<template>
  <div class="product-detail" v-if="product">
    <div class="breadcrumb">
      <a href="#/marketplace">← 返回 Marketplace</a>
    </div>

    <div class="detail-header">
      <div>
        <h1>{{ product.name }}</h1>
        <span class="category-tag">{{ product.category }}</span>
        <span v-if="product.provider?.verified" class="verified">✓ 官方认证</span>
      </div>
      <div class="rating-display">
        <span class="stars">⭐ {{ product.avgRating }}</span>
        <span class="count">({{ product.reviewCount }} 评价)</span>
      </div>
    </div>

    <p class="desc">{{ product.description }}</p>

    <!-- Provider Info -->
    <section v-if="product.provider" class="section">
      <h2>Provider</h2>
      <div class="provider-card">
        <strong>{{ product.provider.name }}</strong>
        <span class="type">{{ product.provider.type }}</span>
      </div>
    </section>

    <!-- Plans -->
    <section v-if="product.plans && product.plans.length > 0" class="section">
      <h2>📦 定价计划</h2>
      <div class="plans-grid">
        <div v-for="plan in product.plans" :key="plan.id" class="plan-card">
          <h3>{{ plan.name }}</h3>
          <div class="plan-price">
            <span v-if="plan.price === 0">免费</span>
            <span v-else>${{ plan.price }}</span>
          </div>
          <p class="plan-desc">{{ plan.description }}</p>
          <span class="billing-type">{{ plan.billingType }}</span>
          <button class="btn btn-primary">订阅</button>
        </div>
      </div>
    </section>
  </div>

  <div v-else class="loading">加载中...</div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getProductDetail } from '@/api/marketplace'
import type { ProductDetail } from '@/api/marketplace'

const route = useRoute()
const id = Number(route.params.id)
const product = ref<ProductDetail | null>(null)

onMounted(async () => {
  try { const { data } = await getProductDetail(id); product.value = data } catch { /* */ }
})
</script>

<style scoped>
.product-detail { max-width: 900px; margin: 0 auto; padding: 24px; }
.breadcrumb { margin-bottom: 16px; }
.breadcrumb a { color: #007aff; text-decoration: none; }
.detail-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 16px; }
.detail-header h1 { font-size: 28px; }
.category-tag { display: inline-block; padding: 2px 10px; background: #f0f0f0; border-radius: 4px; font-size: 12px; margin-right: 8px; }
.verified { color: #34c759; font-size: 13px; }
.rating-display { text-align: right; }
.stars { font-size: 20px; }
.count { font-size: 13px; color: #86868b; display: block; }
.desc { font-size: 15px; color: #666; margin-bottom: 24px; line-height: 1.6; }
.section { margin-bottom: 24px; }
.section h2 { margin-bottom: 12px; }
.provider-card { background: #f5f5f7; border-radius: 8px; padding: 16px; display: flex; gap: 12px; align-items: center; }
.type { font-size: 12px; color: #86868b; }
.plans-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); gap: 16px; }
.plan-card { border: 1px solid #e5e5e5; border-radius: 12px; padding: 20px; text-align: center; }
.plan-card h3 { font-size: 18px; margin-bottom: 8px; }
.plan-price { font-size: 28px; font-weight: 700; color: #007aff; margin-bottom: 8px; }
.plan-desc { font-size: 13px; color: #86868b; margin-bottom: 8px; }
.billing-type { font-size: 11px; padding: 2px 8px; background: #f0f0f0; border-radius: 4px; display: inline-block; margin-bottom: 12px; }
.btn-primary { background: #007aff; color: #fff; border: none; padding: 8px 24px; border-radius: 8px; font-size: 14px; cursor: pointer; width: 100%; }
.loading { text-align: center; padding: 60px; color: #86868b; }
</style>
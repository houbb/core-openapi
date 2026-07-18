<template>
  <div class="marketplace-home">
    <section class="hero">
      <h1>API Marketplace</h1>
      <p>发现、订阅和使用高质量 API 能力</p>
      <div class="search-box">
        <input v-model="keyword" placeholder="搜索 API..." @keyup.enter="doSearch" />
        <button class="btn btn-primary" @click="doSearch">搜索</button>
      </div>
    </section>

    <!-- Featured -->
    <section v-if="featured.length > 0" class="section">
      <h2>🔥 热门推荐</h2>
      <div class="product-grid">
        <div v-for="item in featured" :key="item.id" class="product-card" @click="goDetail(item.id)">
          <div class="product-header">
            <span class="product-category">{{ item.category }}</span>
            <span v-if="item.verified" class="verified-badge">✓</span>
          </div>
          <h3>{{ item.name }}</h3>
          <p class="product-desc">{{ truncate(item.description, 100) }}</p>
          <div class="product-meta">
            <span class="rating">⭐ {{ item.avgRating }} ({{ item.reviewCount }})</span>
            <span class="provider">{{ item.providerName }}</span>
          </div>
        </div>
      </div>
    </section>

    <!-- Categories -->
    <section class="section">
      <h2>📂 按分类浏览</h2>
      <div class="category-tabs">
        <button v-for="cat in categories" :key="cat" :class="{ active: activeCategory === cat }" @click="browseCategory(cat)">
          {{ cat }}
        </button>
      </div>
      <div class="product-grid" v-if="categoryItems.length > 0">
        <div v-for="item in categoryItems" :key="item.id" class="product-card" @click="goDetail(item.id)">
          <div class="product-header">
            <span class="product-category">{{ item.category }}</span>
          </div>
          <h3>{{ item.name }}</h3>
          <p class="product-desc">{{ truncate(item.description, 100) }}</p>
          <div class="product-meta">
            <span class="rating">⭐ {{ item.avgRating }} ({{ item.reviewCount }})</span>
          </div>
        </div>
      </div>
    </section>

    <!-- Search Results -->
    <section v-if="searchResults.length > 0" class="section">
      <h2>搜索结果</h2>
      <div class="product-grid">
        <div v-for="item in searchResults" :key="item.id" class="product-card" @click="goDetail(item.id)">
          <h3>{{ item.name }}</h3>
          <p class="product-desc">{{ truncate(item.description, 100) }}</p>
          <div class="product-meta">
            <span class="rating">⭐ {{ item.avgRating }}</span>
            <span class="provider">{{ item.providerName }}</span>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getFeatured, getByCategory, searchProducts } from '@/api/marketplace'
import type { MarketplaceItem } from '@/api/marketplace'

const router = useRouter()
const keyword = ref('')
const featured = ref<MarketplaceItem[]>([])
const categoryItems = ref<MarketplaceItem[]>([])
const searchResults = ref<MarketplaceItem[]>([])
const activeCategory = ref('')
const categories = ['AI', 'DATA', 'STORAGE', 'WORKFLOW', 'NOTIFICATION', 'PLUGIN', 'OTHER']

function truncate(text: string, len: number) { return text && text.length > len ? text.substring(0, len) + '...' : text }
function goDetail(id: number) { router.push(`/marketplace/${id}`) }

async function browseCategory(cat: string) {
  activeCategory.value = cat
  try { const { data } = await getByCategory(cat); categoryItems.value = data } catch { /* */ }
}

async function doSearch() {
  if (!keyword.value.trim()) return
  try { const { data } = await searchProducts(keyword.value); searchResults.value = data } catch { /* */ }
}

onMounted(async () => {
  try { const { data } = await getFeatured(); featured.value = data } catch { /* */ }
})
</script>

<style scoped>
.marketplace-home { max-width: 1200px; margin: 0 auto; padding: 24px; }
.hero { text-align: center; padding: 48px 24px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border-radius: 16px; color: #fff; margin-bottom: 32px; }
.hero h1 { font-size: 32px; margin-bottom: 8px; }
.hero p { font-size: 16px; opacity: 0.9; margin-bottom: 24px; }
.search-box { display: flex; gap: 8px; justify-content: center; max-width: 480px; margin: 0 auto; }
.search-box input { flex: 1; padding: 10px 16px; border: none; border-radius: 8px; font-size: 14px; outline: none; }
.section { margin-bottom: 32px; }
.section h2 { margin-bottom: 16px; }
.product-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 16px; }
.product-card { background: #fff; border: 1px solid #e5e5e5; border-radius: 12px; padding: 20px; cursor: pointer; transition: box-shadow 0.2s; }
.product-card:hover { box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
.product-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.product-category { font-size: 11px; padding: 2px 8px; background: #f0f0f0; border-radius: 4px; color: #666; }
.verified-badge { color: #34c759; font-weight: 700; }
.product-card h3 { font-size: 16px; margin-bottom: 4px; }
.product-desc { font-size: 13px; color: #86868b; margin-bottom: 12px; }
.product-meta { display: flex; justify-content: space-between; font-size: 12px; color: #86868b; }
.category-tabs { display: flex; gap: 8px; margin-bottom: 16px; flex-wrap: wrap; }
.category-tabs button { padding: 6px 16px; border: 1px solid #e5e5e5; border-radius: 20px; background: #fff; cursor: pointer; font-size: 13px; }
.category-tabs button.active { background: #007aff; color: #fff; border-color: #007aff; }
.btn-primary { background: #fff; color: #667eea; border: none; padding: 10px 20px; border-radius: 8px; font-weight: 600; cursor: pointer; }
</style>
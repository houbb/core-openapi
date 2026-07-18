<template>
  <div class="page">
    <div class="page-header">
      <h1>商品管理</h1>
      <button class="btn btn-primary" @click="showCreate = true">+ 创建商品</button>
    </div>

    <table class="data-table" v-if="products.length > 0">
      <thead>
        <tr>
          <th>ID</th>
          <th>名称</th>
          <th>分类</th>
          <th>Provider</th>
          <th>状态</th>
          <th>创建时间</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="p in products" :key="p.id">
          <td>{{ p.id }}</td>
          <td>{{ p.name }}</td>
          <td>{{ p.category }}</td>
          <td>#{{ p.providerId }}</td>
          <td><span :class="statusClass(p.status)">{{ p.status }}</span></td>
          <td>{{ formatDate(p.createTime) }}</td>
          <td class="actions">
            <button class="btn-sm" @click="editProduct(p)">编辑</button>
            <button v-if="p.status === 'DRAFT'" class="btn-sm btn-success" @click="doPublish(p.id)">发布</button>
            <button v-if="p.status === 'PUBLISHED'" class="btn-sm btn-warning" @click="doDeprecate(p.id)">下架</button>
            <button class="btn-sm btn-danger" @click="doDelete(p.id)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>
    <div v-else class="empty">暂无商品</div>

    <!-- Create/Edit Dialog -->
    <div class="modal" v-if="showCreate || editing">
      <div class="modal-content">
        <h3>{{ editing ? '编辑商品' : '创建商品' }}</h3>
        <label>名称 <input v-model="form.name" /></label>
        <label>描述 <textarea v-model="form.description" rows="3"></textarea></label>
        <label>分类 <select v-model="form.category">
          <option value="AI">AI</option>
          <option value="DATA">DATA</option>
          <option value="STORAGE">STORAGE</option>
          <option value="WORKFLOW">WORKFLOW</option>
          <option value="NOTIFICATION">NOTIFICATION</option>
          <option value="PLUGIN">PLUGIN</option>
          <option value="OTHER">OTHER</option>
        </select></label>
        <label>Provider ID <input v-model.number="form.providerId" type="number" /></label>
        <label>API ID <input v-model.number="form.apiId" type="number" /></label>
        <label>图标 URL <input v-model="form.iconUrl" /></label>
        <div class="modal-actions">
          <button class="btn btn-primary" @click="save">保存</button>
          <button class="btn" @click="closeDialog">取消</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getProducts, createProduct, updateProduct, publishProduct, deprecateProduct, deleteProduct } from '@/api/marketplace'
import type { Product } from '@/api/marketplace'

const products = ref<Product[]>([])
const showCreate = ref(false)
const editing = ref<Product | null>(null)
const form = ref({ name: '', description: '', category: 'OTHER', providerId: 1, apiId: 0, iconUrl: '' })

async function load() {
  try {
    const { data } = await getProducts({ page: 1, size: 100 })
    products.value = data.items
  } catch { /* ignore */ }
}

function statusClass(s: string) {
  return { DRAFT: 'tag-gray', PUBLISHED: 'tag-green', DEPRECATED: 'tag-red' }[s] || ''
}

function formatDate(d: string) { return d ? d.substring(0, 10) : '' }

function editProduct(p: Product) {
  editing.value = p
  form.value = { name: p.name, description: p.description, category: p.category, providerId: p.providerId, apiId: p.apiId, iconUrl: p.iconUrl || '' }
}

function closeDialog() { showCreate.value = false; editing.value = null }

async function save() {
  try {
    if (editing.value) {
      await updateProduct(editing.value.id, form.value as any)
    } else {
      await createProduct(form.value as any)
    }
    closeDialog()
    load()
  } catch (e: any) { alert(e.response?.data?.detail || '保存失败') }
}

async function doPublish(id: number) { try { await publishProduct(id); load() } catch { /* */ } }
async function doDeprecate(id: number) { try { await deprecateProduct(id); load() } catch { /* */ } }
async function doDelete(id: number) { if (confirm('确认删除？')) { await deleteProduct(id); load() } }

onMounted(load)
</script>

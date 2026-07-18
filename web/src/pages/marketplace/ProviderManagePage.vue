<template>
  <div class="page">
    <div class="page-header">
      <h1>Provider 管理</h1>
      <button class="btn btn-primary" @click="showCreate = true">+ 创建 Provider</button>
    </div>

    <table class="data-table" v-if="providers.length > 0">
      <thead>
        <tr>
          <th>ID</th>
          <th>名称</th>
          <th>类型</th>
          <th>认证</th>
          <th>状态</th>
          <th>联系邮箱</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="p in providers" :key="p.id">
          <td>{{ p.id }}</td>
          <td>{{ p.name }}</td>
          <td>{{ p.type }}</td>
          <td>{{ p.verified ? '✅' : '⬜' }}</td>
          <td>{{ p.status }}</td>
          <td>{{ p.contactEmail }}</td>
          <td class="actions">
            <button class="btn-sm" @click="editProvider(p)">编辑</button>
            <button v-if="!p.verified" class="btn-sm btn-success" @click="doVerify(p.id)">认证</button>
            <button class="btn-sm btn-danger" @click="doDelete(p.id)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>
    <div v-else class="empty">暂无 Provider</div>

    <!-- Create/Edit Dialog -->
    <div class="modal" v-if="showCreate || editing">
      <div class="modal-content">
        <h3>{{ editing ? '编辑 Provider' : '创建 Provider' }}</h3>
        <label>名称 <input v-model="form.name" /></label>
        <label>描述 <textarea v-model="form.description" rows="3"></textarea></label>
        <label>类型 <select v-model="form.type">
          <option value="OFFICIAL">OFFICIAL</option>
          <option value="PARTNER">PARTNER</option>
          <option value="COMMUNITY">COMMUNITY</option>
          <option value="ENTERPRISE">ENTERPRISE</option>
        </select></label>
        <label>联系邮箱 <input v-model="form.contactEmail" /></label>
        <label>网站 <input v-model="form.website" /></label>
        <label>图标 URL <input v-model="form.logoUrl" /></label>
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
import { getProviders, createProvider, updateProvider, verifyProvider, deleteProvider } from '@/api/marketplace'
import type { Provider } from '@/api/marketplace'

const providers = ref<Provider[]>([])
const showCreate = ref(false)
const editing = ref<Provider | null>(null)
const form = ref({ name: '', description: '', type: 'COMMUNITY', contactEmail: '', website: '', logoUrl: '' })

async function load() {
  try { const { data } = await getProviders({ page: 1, size: 100 }); providers.value = data.items } catch { /* */ }
}

function editProvider(p: Provider) {
  editing.value = p
  form.value = { name: p.name, description: p.description, type: p.type, contactEmail: p.contactEmail, website: p.website, logoUrl: p.logoUrl || '' }
}

function closeDialog() { showCreate.value = false; editing.value = null }

async function save() {
  try {
    if (editing.value) {
      await updateProvider(editing.value.id, form.value as any)
    } else {
      await createProvider(form.value as any)
    }
    closeDialog()
    load()
  } catch (e: any) { alert(e.response?.data?.detail || '保存失败') }
}

async function doVerify(id: number) { try { await verifyProvider(id); load() } catch { /* */ } }
async function doDelete(id: number) { if (confirm('确认删除？')) { await deleteProvider(id); load() } }

onMounted(load)
</script>

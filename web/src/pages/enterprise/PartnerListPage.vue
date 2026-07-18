<template>
  <div class="page">
    <div class="page-header">
      <h1>🤝 合作伙伴</h1>
      <button class="btn btn-primary" @click="showCreate = true">+ 创建合作伙伴</button>
    </div>

    <div class="filters">
      <input v-model="keyword" placeholder="搜索..." class="input" @keyup.enter="load" />
      <select v-model="filterLevel" class="input" @change="load">
        <option value="">全部等级</option>
        <option value="STANDARD">标准</option>
        <option value="PREMIUM">高级</option>
        <option value="STRATEGIC">战略</option>
      </select>
      <select v-model="filterStatus" class="input" @change="load">
        <option value="">全部状态</option>
        <option value="ACTIVE">活跃</option>
        <option value="INACTIVE">停用</option>
      </select>
    </div>

    <table class="table">
      <thead>
        <tr><th>ID</th><th>名称</th><th>等级</th><th>状态</th><th>联系人</th><th>邮箱</th><th>创建时间</th><th>操作</th></tr>
      </thead>
      <tbody>
        <tr v-for="p in items" :key="p.id">
          <td>{{ p.id }}</td>
          <td>{{ p.name }}</td>
          <td><span :class="['badge', levelClass(p.level)]">{{ p.level }}</span></td>
          <td><span :class="['badge', statusClass(p.status)]">{{ p.status }}</span></td>
          <td>{{ p.contactName }}</td>
          <td>{{ p.contactEmail }}</td>
          <td>{{ formatDate(p.createTime) }}</td>
          <td class="actions">
            <button class="btn btn-sm" @click="editPartner(p)">编辑</button>
            <button class="btn btn-sm btn-danger" @click="del(p.id)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>

    <div class="pagination" v-if="total > size">
      <button :disabled="page <= 1" @click="page--; load()">上一页</button>
      <span>{{ page }} / {{ Math.ceil(total / size) }}</span>
      <button :disabled="page >= Math.ceil(total / size)" @click="page++; load()">下一页</button>
    </div>

    <div v-if="showCreate || editItem" class="modal-overlay" @click.self="closeModal">
      <div class="modal">
        <h3>{{ editItem ? '编辑合作伙伴' : '创建合作伙伴' }}</h3>
        <div class="form">
          <label>名称 <span class="req">*</span></label>
          <input v-model="form.name" class="input" placeholder="合作伙伴名称" />
          <label>等级</label>
          <select v-model="form.level" class="input">
            <option value="STANDARD">标准</option>
            <option value="PREMIUM">高级</option>
            <option value="STRATEGIC">战略</option>
          </select>
          <label>联系人</label>
          <input v-model="form.contactName" class="input" placeholder="联系人" />
          <label>联系邮箱</label>
          <input v-model="form.contactEmail" class="input" placeholder="邮箱" />
          <label>联系电话</label>
          <input v-model="form.contactPhone" class="input" placeholder="电话" />
          <label>描述</label>
          <textarea v-model="form.description" class="input" rows="2" placeholder="描述"></textarea>
          <div class="form-actions">
            <button class="btn" @click="closeModal">取消</button>
            <button class="btn btn-primary" @click="save">保存</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getPartners, createPartner, updatePartner, deletePartner, type Partner, type PartnerRequest } from '@/api/enterprise'

const items = ref<Partner[]>([])
const page = ref(1), size = ref(20), total = ref(0)
const keyword = ref(''), filterLevel = ref(''), filterStatus = ref('')
const showCreate = ref(false), editItem = ref<Partner | null>(null)
const form = ref<PartnerRequest>({ name: '', level: 'STANDARD', contactName: '', contactEmail: '', contactPhone: '', description: '' })

const load = async () => {
  const { data } = await getPartners({ page: page.value, size: size.value, keyword: keyword.value, level: filterLevel.value, status: filterStatus.value })
  items.value = data.items; total.value = data.total
}

const editPartner = (p: Partner) => {
  editItem.value = p
  form.value = { name: p.name, level: p.level, contactName: p.contactName, contactEmail: p.contactEmail, contactPhone: p.contactPhone, description: p.description }
}
const closeModal = () => { showCreate.value = false; editItem.value = null; form.value = { name: '', level: 'STANDARD', contactName: '', contactEmail: '', contactPhone: '', description: '' } }
const save = async () => {
  if (!form.value.name) return
  if (editItem.value) { await updatePartner(editItem.value.id, form.value) } else { await createPartner(form.value) }
  closeModal(); load()
}
const del = async (id: number) => { if (!confirm('确认删除?')) return; await deletePartner(id); load() }

const levelClass = (l: string) => l === 'STRATEGIC' ? 'badge-accent' : l === 'PREMIUM' ? 'badge-warning' : ''
const statusClass = (s: string) => s === 'ACTIVE' ? 'badge-success' : 'badge-danger'
const formatDate = (d: string) => d ? d.substring(0, 10) : ''
onMounted(load)
</script>
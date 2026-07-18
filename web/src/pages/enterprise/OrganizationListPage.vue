<template>
  <div class="page">
    <div class="page-header">
      <h1>🏭 组织管理</h1>
      <button class="btn btn-primary" @click="showCreate = true">+ 创建组织</button>
    </div>

    <div class="filters">
      <input v-model="keyword" placeholder="搜索组织名称..." class="input" @keyup.enter="load" />
      <select v-model="filterType" class="input" @change="load">
        <option value="">全部类型</option>
        <option value="ENTERPRISE">企业</option>
        <option value="PARTNER">合作伙伴</option>
        <option value="INTERNAL">内部</option>
      </select>
      <select v-model="filterStatus" class="input" @change="load">
        <option value="">全部状态</option>
        <option value="ACTIVE">活跃</option>
        <option value="INACTIVE">停用</option>
        <option value="SUSPENDED">暂停</option>
      </select>
    </div>

    <table class="table">
      <thead>
        <tr>
          <th>ID</th><th>名称</th><th>编码</th><th>类型</th><th>状态</th><th>联系人</th><th>创建时间</th><th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="o in items" :key="o.id">
          <td>{{ o.id }}</td>
          <td>
            <router-link :to="`/enterprise/organizations/${o.id}`" class="link">{{ o.name }}</router-link>
          </td>
          <td><span class="badge">{{ o.code }}</span></td>
          <td>{{ typeLabel(o.type) }}</td>
          <td><span :class="['badge', statusClass(o.status)]">{{ o.status }}</span></td>
          <td>{{ o.contactEmail }}</td>
          <td>{{ formatDate(o.createTime) }}</td>
          <td class="actions">
            <button class="btn btn-sm" @click="editOrg(o)">编辑</button>
            <button class="btn btn-sm btn-danger" @click="del(o.id)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>

    <div class="pagination" v-if="total > size">
      <button :disabled="page <= 1" @click="page--; load()">上一页</button>
      <span>{{ page }} / {{ Math.ceil(total / size) }}</span>
      <button :disabled="page >= Math.ceil(total / size)" @click="page++; load()">下一页</button>
    </div>

    <!-- Create/Edit Modal -->
    <div v-if="showCreate || editItem" class="modal-overlay" @click.self="closeModal">
      <div class="modal">
        <h3>{{ editItem ? '编辑组织' : '创建组织' }}</h3>
        <div class="form">
          <label>名称 <span class="req">*</span></label>
          <input v-model="form.name" class="input" placeholder="组织名称" />
          <label>类型</label>
          <select v-model="form.type" class="input">
            <option value="ENTERPRISE">企业</option>
            <option value="PARTNER">合作伙伴</option>
            <option value="INTERNAL">内部</option>
          </select>
          <label>联系邮箱</label>
          <input v-model="form.contactEmail" class="input" placeholder="联系邮箱" />
          <label>联系电话</label>
          <input v-model="form.contactPhone" class="input" placeholder="联系电话" />
          <label>官网</label>
          <input v-model="form.website" class="input" placeholder="https://" />
          <label>描述</label>
          <textarea v-model="form.description" class="input" rows="3" placeholder="组织描述"></textarea>
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
import { getOrganizations, createOrganization, updateOrganization, deleteOrganization, type Organization, type OrganizationRequest } from '@/api/enterprise'

const items = ref<Organization[]>([])
const page = ref(1)
const size = ref(20)
const total = ref(0)
const keyword = ref('')
const filterType = ref('')
const filterStatus = ref('')
const showCreate = ref(false)
const editItem = ref<Organization | null>(null)

const form = ref<OrganizationRequest>({ name: '', type: 'ENTERPRISE', contactEmail: '', contactPhone: '', website: '', description: '' })

const load = async () => {
  try {
    const { data } = await getOrganizations({ page: page.value, size: size.value, keyword: keyword.value, type: filterType.value, status: filterStatus.value })
    items.value = data.items
    total.value = data.total
  } catch { /* ignore */ }
}

const editOrg = (o: Organization) => {
  editItem.value = o
  form.value = { name: o.name, type: o.type, contactEmail: o.contactEmail, contactPhone: o.contactPhone, website: o.website, description: o.description }
}

const closeModal = () => {
  showCreate.value = false
  editItem.value = null
  form.value = { name: '', type: 'ENTERPRISE', contactEmail: '', contactPhone: '', website: '', description: '' }
}

const save = async () => {
  if (!form.value.name) return
  try {
    if (editItem.value) {
      await updateOrganization(editItem.value.id, form.value)
    } else {
      await createOrganization(form.value)
    }
    closeModal()
    load()
  } catch { /* ignore */ }
}

const del = async (id: number) => {
  if (!confirm('确认删除?')) return
  await deleteOrganization(id)
  load()
}

const typeLabel = (t: string) => ({ ENTERPRISE: '企业', PARTNER: '合作伙伴', INTERNAL: '内部' }[t] || t)
const statusClass = (s: string) => s === 'ACTIVE' ? 'badge-success' : s === 'INACTIVE' ? 'badge-warning' : 'badge-danger'
const formatDate = (d: string) => d ? d.substring(0, 10) : ''

onMounted(load)
</script>
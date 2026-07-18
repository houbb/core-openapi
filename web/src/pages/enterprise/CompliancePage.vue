<template>
  <div class="page">
    <div class="page-header">
      <h1>🛡️ 合规管理</h1>
      <button class="btn btn-primary" @click="showCreate = true">+ 创建策略</button>
    </div>

    <div class="form-card" style="margin-bottom: 20px;">
      <label>组织ID</label>
      <input v-model.number="orgId" class="input" type="number" placeholder="输入组织ID查询" @keyup.enter="load" />
      <button class="btn btn-primary" @click="load" style="margin-left: 8px;">查询</button>
    </div>

    <table class="table" v-if="items.length">
      <thead>
        <tr><th>ID</th><th>名称</th><th>类型</th><th>配置</th><th>状态</th><th>操作</th></tr>
      </thead>
      <tbody>
        <tr v-for="p in items" :key="p.id">
          <td>{{ p.id }}</td><td>{{ p.name }}</td>
          <td><span class="badge">{{ p.policyType }}</span></td>
          <td><code style="font-size: 11px;">{{ truncate(p.configJson, 60) }}</code></td>
          <td><span :class="['badge', p.status === 'ACTIVE' ? 'badge-success' : 'badge-danger']">{{ p.status }}</span></td>
          <td class="actions">
            <button class="btn btn-sm btn-danger" @click="del(p.id)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>

    <div v-if="showCreate" class="modal-overlay" @click.self="showCreate = false">
      <div class="modal">
        <h3>创建合规策略</h3>
        <div class="form">
          <label>名称 <span class="req">*</span></label>
          <input v-model="form.name" class="input" placeholder="策略名称" />
          <label>类型 <span class="req">*</span></label>
          <select v-model="form.policyType" class="input">
            <option value="DATA_RETENTION">数据保留</option>
            <option value="DATA_MASKING">数据脱敏</option>
            <option value="CROSS_REGION">跨区域</option>
            <option value="AUDIT_LEVEL">审计级别</option>
            <option value="IP_WHITELIST">IP白名单</option>
          </select>
          <label>配置 (JSON)</label>
          <textarea v-model="form.configJson" class="input" rows="3" placeholder='{"days": 90}'></textarea>
          <div class="form-actions">
            <button class="btn" @click="showCreate = false">取消</button>
            <button class="btn btn-primary" @click="save">保存</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { getComplianceByOrg, createCompliance, deleteCompliance, type CompliancePolicy, type CompliancePolicyRequest } from '@/api/enterprise'

const orgId = ref(0)
const items = ref<CompliancePolicy[]>([])
const showCreate = ref(false)
const form = ref<CompliancePolicyRequest>({ name: '', policyType: 'DATA_RETENTION', configJson: '' })

const load = async () => {
  if (!orgId.value) return
  try {
    const { data } = await getComplianceByOrg(orgId.value)
    items.value = data
  } catch { items.value = [] }
}

const save = async () => {
  if (!orgId.value || !form.value.name || !form.value.policyType) return
  await createCompliance(orgId.value, form.value)
  showCreate.value = false
  form.value = { name: '', policyType: 'DATA_RETENTION', configJson: '' }
  load()
}

const del = async (id: number) => {
  if (!confirm('确认删除?')) return
  await deleteCompliance(id)
  load()
}

const truncate = (s: string, n: number) => s && s.length > n ? s.substring(0, n) + '...' : s
</script>
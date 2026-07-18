<template>
  <div class="page">
    <div class="page-header">
      <h1>📋 合同管理</h1>
      <button class="btn btn-primary" @click="showCreate = true">+ 创建合同</button>
    </div>

    <div class="filters">
      <select v-model="filterStatus" class="input" @change="load">
        <option value="">全部状态</option>
        <option value="DRAFT">草稿</option>
        <option value="ACTIVE">生效</option>
        <option value="EXPIRED">到期</option>
      </select>
    </div>

    <table class="table">
      <thead>
        <tr><th>ID</th><th>合同编号</th><th>名称</th><th>套餐</th><th>状态</th><th>日期</th><th>最大QPS</th><th>操作</th></tr>
      </thead>
      <tbody>
        <tr v-for="c in items" :key="c.id">
          <td>{{ c.id }}</td>
          <td><span class="badge">{{ c.contractNo }}</span></td>
          <td>{{ c.name }}</td>
          <td>{{ c.planName }}</td>
          <td><span :class="['badge', statusClass(c.status)]">{{ c.status }}</span></td>
          <td>{{ c.startDate }} ~ {{ c.endDate }}</td>
          <td>{{ c.maxQps || '-' }}</td>
          <td class="actions">
            <button v-if="c.status === 'DRAFT'" class="btn btn-sm" @click="activate(c.id)">激活</button>
            <button v-if="c.status === 'ACTIVE'" class="btn btn-sm btn-warning" @click="expire(c.id)">到期</button>
            <button class="btn btn-sm btn-danger" @click="del(c.id)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>

    <div class="pagination" v-if="total > size">
      <button :disabled="page <= 1" @click="page--; load()">上一页</button>
      <span>{{ page }} / {{ Math.ceil(total / size) }}</span>
      <button :disabled="page >= Math.ceil(total / size)" @click="page++; load()">下一页</button>
    </div>

    <div v-if="showCreate" class="modal-overlay" @click.self="showCreate = false">
      <div class="modal">
        <h3>创建合同</h3>
        <div class="form">
          <label>合同编号 <span class="req">*</span></label>
          <input v-model="form.contractNo" class="input" placeholder="合同编号" />
          <label>名称 <span class="req">*</span></label>
          <input v-model="form.name" class="input" placeholder="合同名称" />
          <label>套餐</label>
          <select v-model="form.planName" class="input">
            <option value="Basic">Basic</option>
            <option value="Professional">Professional</option>
            <option value="Enterprise">Enterprise</option>
          </select>
          <label>开始日期</label>
          <input v-model="form.startDate" class="input" type="date" />
          <label>结束日期</label>
          <input v-model="form.endDate" class="input" type="date" />
          <label>最大QPS</label>
          <input v-model.number="form.maxQps" class="input" type="number" placeholder="0=不限制" />
          <label>最大请求/月</label>
          <input v-model.number="form.maxRequests" class="input" type="number" placeholder="0=不限制" />
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
import { ref, onMounted } from 'vue'
import { getContracts, createContract, activateContract, expireContract, deleteContract, type Contract, type ContractRequest } from '@/api/enterprise'

const items = ref<Contract[]>([])
const page = ref(1), size = ref(20), total = ref(0)
const filterStatus = ref('')
const showCreate = ref(false)
const form = ref<ContractRequest>({ contractNo: '', name: '', planName: 'Basic', startDate: '', endDate: '', maxQps: 0, maxRequests: 0 })

const load = async () => {
  const { data } = await getContracts({ page: page.value, size: size.value, status: filterStatus.value })
  items.value = data.items; total.value = data.total
}

const save = async () => {
  if (!form.value.contractNo || !form.value.name) return
  await createContract(form.value)
  showCreate.value = false
  form.value = { contractNo: '', name: '', planName: 'Basic', startDate: '', endDate: '', maxQps: 0, maxRequests: 0 }
  load()
}

const activate = async (id: number) => { await activateContract(id); load() }
const expire = async (id: number) => { await expireContract(id); load() }
const del = async (id: number) => { if (!confirm('确认删除?')) return; await deleteContract(id); load() }

const statusClass = (s: string) => s === 'ACTIVE' ? 'badge-success' : s === 'DRAFT' ? 'badge-warning' : 'badge-danger'
onMounted(load)
</script>
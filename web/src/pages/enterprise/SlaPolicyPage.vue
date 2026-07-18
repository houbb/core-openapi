<template>
  <div class="page">
    <div class="page-header">
      <h1>🎯 SLA 策略</h1>
    </div>

    <div class="form-card">
      <label>组织ID</label>
      <input v-model.number="orgId" class="input" type="number" placeholder="输入组织ID查询" @keyup.enter="load" />
      <button class="btn btn-primary" @click="load" style="margin-left: 8px;">查询</button>
    </div>

    <div v-if="sla" class="detail-card" style="margin-top: 16px;">
      <div class="detail-row"><span class="label">名称</span><span>{{ sla.name }}</span></div>
      <div class="detail-row"><span class="label">可用性</span><span class="highlight">{{ (sla.availability * 100).toFixed(4) }}%</span></div>
      <div class="detail-row"><span class="label">响应时间</span><span>{{ sla.responseTimeMs }}ms</span></div>
      <div class="detail-row"><span class="label">P99 延迟</span><span>{{ sla.latencyP99Ms }}ms</span></div>
      <div class="detail-row"><span class="label">支持等级</span><span>{{ sla.supportLevel }}</span></div>
      <div class="detail-row"><span class="label">故障响应</span><span>{{ sla.incidentResponseMin }} 分钟</span></div>
      <div class="detail-row"><span class="label">状态</span><span :class="['badge', sla.status === 'ACTIVE' ? 'badge-success' : 'badge-danger']">{{ sla.status }}</span></div>
    </div>
    <div v-else-if="searched" class="empty">未找到SLA策略，可以创建新的</div>

    <div v-if="orgId" style="margin-top: 24px;">
      <h3>{{ sla ? '更新SLA' : '创建SLA' }}</h3>
      <div class="form-card">
        <label>名称</label>
        <input v-model="form.name" class="input" placeholder="SLA名称" />
        <label>可用性 (0-1)</label>
        <input v-model.number="form.availability" class="input" type="number" step="0.0001" min="0" max="1" placeholder="0.99" />
        <label>响应时间 (ms)</label>
        <input v-model.number="form.responseTimeMs" class="input" type="number" placeholder="1000" />
        <label>P99 延迟 (ms)</label>
        <input v-model.number="form.latencyP99Ms" class="input" type="number" placeholder="500" />
        <label>支持等级</label>
        <select v-model="form.supportLevel" class="input">
          <option value="STANDARD">标准</option>
          <option value="PRIORITY">优先</option>
          <option value="PREMIUM">高级</option>
        </select>
        <label>故障响应 (分钟)</label>
        <input v-model.number="form.incidentResponseMin" class="input" type="number" placeholder="60" />
        <button class="btn btn-primary" @click="save" style="margin-top: 12px;">保存</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { getSlaByOrg, createOrUpdateSla, type SlaPolicy, type SlaPolicyRequest } from '@/api/enterprise'

const orgId = ref(0)
const sla = ref<SlaPolicy | null>(null)
const searched = ref(false)
const form = ref<SlaPolicyRequest>({ name: 'Default SLA', availability: 0.99, responseTimeMs: 1000, latencyP99Ms: 500, supportLevel: 'STANDARD', incidentResponseMin: 60 })

const load = async () => {
  if (!orgId.value) return
  searched.value = true
  try {
    const { data } = await getSlaByOrg(orgId.value)
    sla.value = data
    if (data) {
      form.value = { name: data.name, availability: data.availability, responseTimeMs: data.responseTimeMs, latencyP99Ms: data.latencyP99Ms, supportLevel: data.supportLevel, incidentResponseMin: data.incidentResponseMin }
    }
  } catch { sla.value = null }
}

const save = async () => {
  if (!orgId.value || !form.value.name) return
  await createOrUpdateSla(orgId.value, form.value)
  load()
}
</script>

<style scoped>
.form-card {
  background: var(--bg-secondary);
  border-radius: 12px;
  padding: 20px;
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: flex-end;
}
.form-card label { display: block; font-size: 12px; color: var(--text-tertiary); margin-bottom: 4px; }
.form-card .input { width: 160px; }
.highlight { color: var(--accent); font-weight: 600; }
</style>
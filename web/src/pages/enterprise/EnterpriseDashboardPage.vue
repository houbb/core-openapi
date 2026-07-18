<template>
  <div class="page">
    <div class="page-header">
      <h1>🏢 企业控制台</h1>
      <p class="page-subtitle">Enterprise Open Platform</p>
    </div>

    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-value">{{ stats.totalOrganizations }}</div>
        <div class="stat-label">组织总数</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ stats.activeOrganizations }}</div>
        <div class="stat-label">活跃组织</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ stats.totalPartners }}</div>
        <div class="stat-label">合作伙伴</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ stats.strategicPartners }}</div>
        <div class="stat-label">战略伙伴</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ stats.activeContracts }}</div>
        <div class="stat-label">生效合同</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ stats.draftContracts }}</div>
        <div class="stat-label">草稿合同</div>
      </div>
    </div>

    <div class="quick-links">
      <router-link to="/enterprise/organizations" class="quick-link">
        <span class="ql-icon">🏭</span>
        <span class="ql-title">组织管理</span>
        <span class="ql-desc">管理企业组织、团队和成员</span>
      </router-link>
      <router-link to="/enterprise/partners" class="quick-link">
        <span class="ql-icon">🤝</span>
        <span class="ql-title">合作伙伴</span>
        <span class="ql-desc">管理合作伙伴及API授权</span>
      </router-link>
      <router-link to="/enterprise/contracts" class="quick-link">
        <span class="ql-icon">📋</span>
        <span class="ql-title">合同管理</span>
        <span class="ql-desc">企业合同与套餐管理</span>
      </router-link>
      <router-link to="/enterprise/sla" class="quick-link">
        <span class="ql-icon">🎯</span>
        <span class="ql-title">SLA 策略</span>
        <span class="ql-desc">服务等级协议配置</span>
      </router-link>
      <router-link to="/enterprise/governance" class="quick-link">
        <span class="ql-icon">🔍</span>
        <span class="ql-title">API 治理</span>
        <span class="ql-desc">API生命周期与合规检查</span>
      </router-link>
      <router-link to="/enterprise/audit" class="quick-link">
        <span class="ql-icon">📝</span>
        <span class="ql-title">审计日志</span>
        <span class="ql-desc">企业级操作审计追踪</span>
      </router-link>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getEnterpriseDashboard } from '@/api/enterprise'

const stats = ref({
  totalOrganizations: 0,
  activeOrganizations: 0,
  totalPartners: 0,
  strategicPartners: 0,
  activeContracts: 0,
  draftContracts: 0,
})

onMounted(async () => {
  try {
    const { data } = await getEnterpriseDashboard()
    stats.value = data
  } catch { /* ignore */ }
})
</script>

<style scoped>
.quick-links {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-top: 32px;
}

.quick-link {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 20px;
  border-radius: 12px;
  background: var(--bg-secondary);
  border: 1px solid var(--border);
  text-decoration: none;
  color: var(--text-primary);
  transition: border-color 0.15s, box-shadow 0.15s;
}

.quick-link:hover {
  border-color: var(--accent);
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.ql-icon {
  font-size: 28px;
}

.ql-title {
  font-size: 15px;
  font-weight: 600;
  margin-top: 4px;
}

.ql-desc {
  font-size: 12px;
  color: var(--text-tertiary);
}
</style>
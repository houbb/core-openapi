<template>
  <div class="page">
    <div class="page-header">
      <router-link to="/enterprise/organizations" class="back-link">← 返回</router-link>
      <h1>{{ org?.name || '组织详情' }}</h1>
      <span :class="['badge', statusClass(org?.status || '')]">{{ org?.status }}</span>
    </div>

    <div v-if="org" class="detail-card">
      <div class="detail-row"><span class="label">编码</span><span>{{ org.code }}</span></div>
      <div class="detail-row"><span class="label">类型</span><span>{{ typeLabel(org.type) }}</span></div>
      <div class="detail-row"><span class="label">联系邮箱</span><span>{{ org.contactEmail }}</span></div>
      <div class="detail-row"><span class="label">联系电话</span><span>{{ org.contactPhone }}</span></div>
      <div class="detail-row"><span class="label">官网</span><span>{{ org.website }}</span></div>
      <div class="detail-row"><span class="label">描述</span><span>{{ org.description }}</span></div>
      <div class="detail-row"><span class="label">租户ID</span><span class="badge">{{ org.tenantId }}</span></div>
    </div>

    <!-- Tabs -->
    <div class="tabs">
      <button :class="['tab', { active: tab === 'members' }]" @click="tab = 'members'">成员管理</button>
      <button :class="['tab', { active: tab === 'teams' }]" @click="tab = 'teams'">团队管理</button>
      <button :class="['tab', { active: tab === 'sla' }]" @click="tab = 'sla'">SLA</button>
    </div>

    <!-- Members Tab -->
    <div v-if="tab === 'members'">
      <div class="tab-header">
        <span>{{ members.length }} 位成员</span>
        <button class="btn btn-primary btn-sm" @click="showAddMember = true">+ 添加成员</button>
      </div>
      <table class="table">
        <thead><tr><th>ID</th><th>用户ID</th><th>团队</th><th>角色</th><th>状态</th><th>加入时间</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="m in members" :key="m.id">
            <td>{{ m.id }}</td>
            <td>{{ m.userId }}</td>
            <td>{{ m.teamId || '-' }}</td>
            <td><span class="badge">{{ m.role }}</span></td>
            <td>{{ m.status }}</td>
            <td>{{ formatDate(m.joinedAt) }}</td>
            <td class="actions">
              <button class="btn btn-sm" @click="changeRole(m)">改角色</button>
              <button class="btn btn-sm btn-danger" @click="removeMember(m.id)">移除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Teams Tab -->
    <div v-if="tab === 'teams'">
      <div class="tab-header">
        <span>{{ teams.length }} 个团队</span>
        <button class="btn btn-primary btn-sm" @click="showAddTeam = true">+ 创建团队</button>
      </div>
      <table class="table">
        <thead><tr><th>ID</th><th>名称</th><th>上级团队</th><th>负责人</th><th>状态</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="t in teams" :key="t.id">
            <td>{{ t.id }}</td><td>{{ t.name }}</td><td>{{ t.parentId || '-' }}</td>
            <td>{{ t.leaderId || '-' }}</td><td>{{ t.status }}</td>
            <td class="actions">
              <button class="btn btn-sm btn-danger" @click="delTeam(t.id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- SLA Tab -->
    <div v-if="tab === 'sla'" class="tab-content">
      <div class="tab-header">
        <span>SLA 策略</span>
      </div>
      <div v-if="sla" class="detail-card">
        <div class="detail-row"><span class="label">名称</span><span>{{ sla.name }}</span></div>
        <div class="detail-row"><span class="label">可用性</span><span>{{ (sla.availability * 100).toFixed(2) }}%</span></div>
        <div class="detail-row"><span class="label">响应时间</span><span>{{ sla.responseTimeMs }}ms</span></div>
        <div class="detail-row"><span class="label">P99延迟</span><span>{{ sla.latencyP99Ms }}ms</span></div>
        <div class="detail-row"><span class="label">支持等级</span><span>{{ sla.supportLevel }}</span></div>
        <div class="detail-row"><span class="label">故障响应</span><span>{{ sla.incidentResponseMin }}分钟</span></div>
      </div>
      <div v-else class="empty">未配置SLA策略</div>
    </div>

    <!-- Add Member Modal -->
    <div v-if="showAddMember" class="modal-overlay" @click.self="showAddMember = false">
      <div class="modal">
        <h3>添加成员</h3>
        <div class="form">
          <label>用户ID <span class="req">*</span></label>
          <input v-model.number="memberForm.userId" class="input" type="number" placeholder="用户ID" />
          <label>团队ID</label>
          <input v-model.number="memberForm.teamId" class="input" type="number" placeholder="可选" />
          <label>角色</label>
          <select v-model="memberForm.role" class="input">
            <option value="MEMBER">成员</option>
            <option value="ADMIN">管理员</option>
            <option value="VIEWER">观察者</option>
          </select>
          <div class="form-actions">
            <button class="btn" @click="showAddMember = false">取消</button>
            <button class="btn btn-primary" @click="addMember">添加</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Add Team Modal -->
    <div v-if="showAddTeam" class="modal-overlay" @click.self="showAddTeam = false">
      <div class="modal">
        <h3>创建团队</h3>
        <div class="form">
          <label>名称 <span class="req">*</span></label>
          <input v-model="teamForm.name" class="input" placeholder="团队名称" />
          <label>描述</label>
          <input v-model="teamForm.description" class="input" placeholder="团队描述" />
          <div class="form-actions">
            <button class="btn" @click="showAddTeam = false">取消</button>
            <button class="btn btn-primary" @click="addTeam">创建</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { getOrganization, getMembers, addMember, updateMemberRole, removeMember, getTeams, createTeam, deleteTeam, getSlaByOrg, type Organization, type Member, type Team, type SlaPolicy } from '@/api/enterprise'

const route = useRoute()
const orgId = Number(route.params.id)

const org = ref<Organization | null>(null)
const members = ref<Member[]>([])
const teams = ref<Team[]>([])
const sla = ref<SlaPolicy | null>(null)
const tab = ref('members')

const showAddMember = ref(false)
const memberForm = ref({ userId: 0, teamId: undefined as number | undefined, role: 'MEMBER' })

const showAddTeam = ref(false)
const teamForm = ref({ name: '', description: '' })

const loadAll = async () => {
  try {
    const [orgR, memR, teamR, slaR] = await Promise.all([
      getOrganization(orgId), getMembers(orgId), getTeams(orgId), getSlaByOrg(orgId).catch(() => ({ data: null }))
    ])
    org.value = orgR.data
    members.value = memR.data.items
    teams.value = teamR.data
    sla.value = slaR.data
  } catch { /* ignore */ }
}

const addMemberFn = async () => {
  if (!memberForm.value.userId) return
  await addMember(orgId, memberForm.value)
  showAddMember.value = false
  memberForm.value = { userId: 0, teamId: undefined, role: 'MEMBER' }
  loadAll()
}

const changeRole = async (m: Member) => {
  const newRole = prompt('新角色 (MEMBER/ADMIN/VIEWER):', m.role)
  if (newRole) {
    await updateMemberRole(orgId, m.id, newRole)
    loadAll()
  }
}

const removeMemberFn = async (id: number) => {
  if (!confirm('确认移除?')) return
  await removeMember(orgId, id)
  loadAll()
}

const addTeamFn = async () => {
  if (!teamForm.value.name) return
  await createTeam(orgId, teamForm.value)
  showAddTeam.value = false
  teamForm.value = { name: '', description: '' }
  loadAll()
}

const delTeam = async (id: number) => {
  if (!confirm('确认删除?')) return
  await deleteTeam(orgId, id)
  loadAll()
}

const typeLabel = (t: string) => ({ ENTERPRISE: '企业', PARTNER: '合作伙伴', INTERNAL: '内部' }[t] || t)
const statusClass = (s: string) => s === 'ACTIVE' ? 'badge-success' : s === 'INACTIVE' ? 'badge-warning' : 'badge-danger'
const formatDate = (d: string) => d ? d.substring(0, 10) : ''

onMounted(loadAll)
</script>

<style scoped>
.detail-card {
  background: var(--bg-secondary);
  border-radius: 12px;
  padding: 20px;
  margin: 16px 0;
  display: grid;
  gap: 12px;
}
.detail-row {
  display: flex;
  gap: 12px;
}
.detail-row .label {
  width: 100px;
  color: var(--text-tertiary);
  font-size: 12px;
  flex-shrink: 0;
}
.tabs {
  display: flex;
  gap: 4px;
  margin: 24px 0 16px;
  border-bottom: 1px solid var(--border);
}
.tab {
  padding: 10px 20px;
  border: none;
  background: none;
  cursor: pointer;
  font-size: 13px;
  color: var(--text-tertiary);
  border-bottom: 2px solid transparent;
}
.tab.active {
  color: var(--accent);
  border-bottom-color: var(--accent);
  font-weight: 500;
}
.tab-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.back-link {
  color: var(--accent);
  text-decoration: none;
  font-size: 13px;
}
.empty {
  padding: 40px;
  text-align: center;
  color: var(--text-tertiary);
}
.tab-content {
  padding: 8px 0;
}
</style>
<template>
  <div class="sdk-page">
    <div class="page-header">
      <h2>🔧 SDK 管理</h2>
      <button class="btn-primary" @click="showGenerateDialog = true">+ 生成 SDK</button>
    </div>

    <table class="data-table" v-if="projects.length > 0">
      <thead>
        <tr>
          <th>名称</th>
          <th>语言</th>
          <th>版本</th>
          <th>状态</th>
          <th>生成时间</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="p in projects" :key="p.id">
          <td>{{ p.name }}</td>
          <td>
            <span :class="['lang-tag', p.language]">{{ langLabel(p.language) }}</span>
          </td>
          <td>{{ p.version }}</td>
          <td>
            <span :class="['status-tag', p.status]">{{ statusLabel(p.status) }}</span>
          </td>
          <td>{{ fmtDate(p.updateTime) }}</td>
          <td class="actions">
            <button class="btn-sm" @click="viewGenerations(p)">记录</button>
            <button class="btn-sm btn-danger" @click="handleDelete(p)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>
    <div v-else class="empty-state">暂无 SDK 项目，点击上方按钮生成第一个 SDK。</div>

    <!-- Generate Dialog -->
    <div class="modal-overlay" v-if="showGenerateDialog" @click.self="showGenerateDialog = false">
      <div class="modal">
        <h3>生成 SDK</h3>
        <div class="form-group">
          <label>SDK 名称</label>
          <input v-model="genForm.name" placeholder="如 core-ai-sdk" />
        </div>
        <div class="form-group">
          <label>目标语言</label>
          <select v-model="genForm.language">
            <option value="java">Java</option>
            <option value="python">Python</option>
          </select>
        </div>
        <div class="form-group">
          <label>版本号</label>
          <input v-model="genForm.version" placeholder="1.0.0" />
        </div>
        <div class="form-group">
          <label>选择 API（多选）</label>
          <div class="api-select-list">
            <label v-for="api in availableApis" :key="api.id" class="api-check-item">
              <input type="checkbox" :value="api.id" v-model="genForm.apiIds" />
              <span class="api-method">{{ api.httpMethod }}</span>
              <span class="api-path">{{ api.path }}</span>
              <span class="api-name">{{ api.name }}</span>
            </label>
          </div>
        </div>
        <div class="modal-actions">
          <button class="btn-secondary" @click="showGenerateDialog = false">取消</button>
          <button class="btn-primary" @click="handleGenerate" :disabled="generating">
            {{ generating ? '生成中...' : '生成' }}
          </button>
        </div>
        <div v-if="genError" class="error-msg">{{ genError }}</div>
      </div>
    </div>

    <!-- Generations Panel -->
    <div class="modal-overlay" v-if="showGenPanel" @click.self="showGenPanel = false">
      <div class="modal modal-lg">
        <h3>生成记录 — {{ selectedProject?.name }}</h3>
        <table class="data-table" v-if="generations.length > 0">
          <thead>
            <tr>
              <th>ID</th>
              <th>状态</th>
              <th>文件大小</th>
              <th>生成时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="g in generations" :key="g.id">
              <td>#{{ g.id }}</td>
              <td><span :class="['status-tag', g.status]">{{ statusLabel(g.status) }}</span></td>
              <td>{{ fmtSize(g.fileSize) }}</td>
              <td>{{ fmtDate(g.createTime) }}</td>
              <td>
                <a v-if="g.status === 'READY'" :href="getSdkDownloadUrl(g.id)" class="btn-sm">下载</a>
                <span v-else>-</span>
              </td>
            </tr>
          </tbody>
        </table>
        <div v-else class="empty-state">暂无生成记录。</div>
        <div class="modal-actions">
          <button class="btn-secondary" @click="showGenPanel = false">关闭</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import {
  getSdkProjects, getSdkGenerations, generateSdk,
  deleteSdkProject, getSdkDownloadUrl,
  type SdkProject, type SdkGeneration, type SdkGenerateRequest, type PageResult
} from '@/api/sdk'
import { getDefinitions, type DefinitionItem } from '@/api/definitions'

const projects = ref<SdkProject[]>([])
const availableApis = ref<DefinitionItem[]>([])
const showGenerateDialog = ref(false)
const generating = ref(false)
const genError = ref('')
const genForm = ref<SdkGenerateRequest>({
  name: '',
  language: 'java',
  version: '1.0.0',
  apiIds: [],
})

const showGenPanel = ref(false)
const selectedProject = ref<SdkProject | null>(null)
const generations = ref<SdkGeneration[]>([])

onMounted(() => {
  loadProjects()
  loadApis()
})

async function loadProjects() {
  try {
    const res: PageResult<SdkProject> = await getSdkProjects(1, 50)
    projects.value = res.items || []
  } catch (e) {
    console.error('Failed to load SDK projects', e)
  }
}

async function loadApis() {
  try {
    const res = await getDefinitions({ page: 1, size: 200 })
    availableApis.value = (res.data as any)?.items || (res.data as any)?.records || []
  } catch (e) {
    console.error('Failed to load APIs', e)
  }
}

async function handleGenerate() {
  if (!genForm.value.name || genForm.value.apiIds.length === 0) {
    genError.value = '请填写 SDK 名称并至少选择一个 API'
    return
  }
  generating.value = true
  genError.value = ''
  try {
    await generateSdk(genForm.value)
    showGenerateDialog.value = false
    genForm.value = { name: '', language: 'java', version: '1.0.0', apiIds: [] }
    await loadProjects()
  } catch (e: any) {
    genError.value = e.response?.data?.message || e.message || '生成失败'
  } finally {
    generating.value = false
  }
}

async function viewGenerations(p: SdkProject) {
  selectedProject.value = p
  showGenPanel.value = true
  try {
    const res: PageResult<SdkGeneration> = await getSdkGenerations(p.id, 1, 50)
    generations.value = res.items || []
  } catch (e) {
    console.error('Failed to load generations', e)
  }
}

async function handleDelete(p: SdkProject) {
  if (!confirm(`确定删除 SDK "${p.name}"？`)) return
  try {
    await deleteSdkProject(p.id)
    await loadProjects()
  } catch (e) {
    console.error('Failed to delete', e)
  }
}

function langLabel(lang: string) {
  return lang === 'java' ? 'Java' : lang === 'python' ? 'Python' : lang
}

function statusLabel(s: string) {
  const map: Record<string, string> = {
    GENERATING: '生成中',
    READY: '已完成',
    FAILED: '失败',
  }
  return map[s] || s
}

function fmtDate(d: string) {
  if (!d) return '-'
  return new Date(d).toLocaleString('zh-CN')
}

function fmtSize(bytes?: number) {
  if (bytes == null) return '-'
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
}
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.btn-primary {
  background: #4f46e5;
  color: #fff;
  border: none;
  padding: 8px 20px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
}
.btn-primary:hover { background: #4338ca; }
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }
.btn-secondary {
  background: #e5e7eb;
  color: #374151;
  border: none;
  padding: 8px 20px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
}
.btn-sm {
  background: #e0e7ff;
  color: #4f46e5;
  border: none;
  padding: 4px 12px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  margin-right: 4px;
  text-decoration: none;
  display: inline-block;
}
.btn-danger { background: #fee2e2; color: #dc2626; }
.data-table { width: 100%; border-collapse: collapse; }
.data-table th, .data-table td {
  padding: 10px 12px;
  text-align: left;
  border-bottom: 1px solid #e5e7eb;
  font-size: 13px;
}
.data-table th { color: #6b7280; font-weight: 600; font-size: 12px; }
.lang-tag {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
}
.lang-tag.java { background: #fef3c7; color: #d97706; }
.lang-tag.python { background: #dbeafe; color: #2563eb; }
.status-tag {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}
.status-tag.READY { background: #d1fae5; color: #065f46; }
.status-tag.GENERATING { background: #dbeafe; color: #1d4ed8; }
.status-tag.FAILED { background: #fee2e2; color: #991b1b; }

/* Modal */
.modal-overlay {
  position: fixed; top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.4);
  display: flex; align-items: center; justify-content: center;
  z-index: 1000;
}
.modal {
  background: #fff;
  padding: 24px;
  border-radius: 12px;
  width: 560px;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(0,0,0,0.15);
}
.modal-lg { width: 720px; }
.modal h3 { margin: 0 0 16px 0; font-size: 18px; }
.form-group { margin-bottom: 14px; }
.form-group label { display: block; font-size: 13px; color: #374151; margin-bottom: 4px; font-weight: 600; }
.form-group input, .form-group select {
  width: 100%; padding: 8px 10px; border: 1px solid #d1d5db;
  border-radius: 6px; font-size: 14px; box-sizing: border-box;
}
.api-select-list { max-height: 200px; overflow-y: auto; border: 1px solid #e5e7eb; border-radius: 6px; padding: 4px; }
.api-check-item {
  display: flex; align-items: center; gap: 8px;
  padding: 6px 8px; cursor: pointer; font-size: 13px;
  border-radius: 4px;
}
.api-check-item:hover { background: #f3f4f6; }
.api-method {
  background: #e0e7ff; color: #4338ca; padding: 1px 6px;
  border-radius: 3px; font-size: 11px; font-weight: 600; min-width: 48px; text-align: center;
}
.api-path { color: #374151; font-family: monospace; }
.api-name { color: #9ca3af; margin-left: auto; }
.modal-actions { margin-top: 16px; display: flex; gap: 8px; justify-content: flex-end; }
.error-msg { color: #dc2626; font-size: 13px; margin-top: 8px; }
.empty-state { text-align: center; padding: 40px; color: #9ca3af; font-size: 14px; }
</style>

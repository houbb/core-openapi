<template>
  <div class="page">
    <div class="page-header">
      <div>
        <router-link to="/definitions" style="font-size: 13px; color: var(--accent);">← API 目录</router-link>
        <h1 style="margin-top: 8px;" v-if="definition">
          <span class="method-badge" :class="`method-${definition.httpMethod}`">{{ definition.httpMethod }}</span>
          {{ definition.path }}
        </h1>
      </div>
      <div style="display: flex; gap: 8px;">
        <button v-if="definition?.status === 'DRAFT' || definition?.status === 'REVIEW'" class="btn btn-primary" @click="doPublish">发布</button>
        <button v-if="definition?.status === 'PUBLISHED'" class="btn btn-accent" @click="doDeprecate">废弃</button>
        <button v-if="definition?.status === 'DEPRECATED'" class="btn btn-danger" @click="doOffline">下线</button>
      </div>
    </div>

    <!-- Basic info -->
    <div class="card" style="margin-bottom: 20px;" v-if="definition">
      <p style="margin-bottom: 8px;">{{ definition.description || '暂无描述' }}</p>
      <div style="display: flex; gap: 24px; font-size: 12px; color: var(--text-secondary);">
        <span>状态: <span class="badge" :class="`badge-${definition.status}`">{{ definition.status }}</span></span>
        <span>分类: {{ definition.category || '-' }}</span>
        <span>创建时间: {{ definition.createTime }}</span>
      </div>
    </div>

    <!-- Tabs -->
    <div class="tabs">
      <div class="tab" :class="{ active: activeTab === 'params' }" @click="activeTab = 'params'">请求参数</div>
      <div class="tab" :class="{ active: activeTab === 'responses' }" @click="activeTab = 'responses'">响应定义</div>
      <div class="tab" :class="{ active: activeTab === 'versions' }" @click="activeTab = 'versions'">版本管理</div>
      <div class="tab" :class="{ active: activeTab === 'tags' }" @click="activeTab = 'tags'">标签</div>
    </div>

    <!-- Parameters Tab -->
    <div class="card" v-if="activeTab === 'params'">
      <div style="margin-bottom: 12px;">
        <button class="btn btn-sm btn-accent" @click="showParamForm = true">+ 添加参数</button>
      </div>
      <table v-if="parameters.length > 0">
        <thead>
          <tr>
            <th>名称</th>
            <th>位置</th>
            <th>类型</th>
            <th>必填</th>
            <th>描述</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="p in parameters" :key="p.id">
            <td><strong>{{ p.name }}</strong></td>
            <td><span class="badge badge-default">{{ p.location }}</span></td>
            <td>{{ p.type }}</td>
            <td>{{ p.required ? '✅' : '-' }}</td>
            <td>{{ p.description || '-' }}</td>
            <td>
              <button class="btn btn-sm" @click="editParam(p)">编辑</button>
              <button class="btn btn-sm btn-danger" @click="deleteParam(p.id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
      <div class="empty-state" v-else><p>暂无参数，点击上方按钮添加</p></div>
    </div>

    <!-- Responses Tab -->
    <div class="card" v-if="activeTab === 'responses'">
      <div style="margin-bottom: 12px;">
        <button class="btn btn-sm btn-accent" @click="showRespForm = true">+ 添加响应</button>
      </div>
      <table v-if="responses.length > 0">
        <thead>
          <tr><th>状态码</th><th>类型</th><th>Schema</th><th>操作</th></tr>
        </thead>
        <tbody>
          <tr v-for="r in responses" :key="r.id">
            <td><span class="badge badge-default">{{ r.statusCode }}</span></td>
            <td>{{ r.contentType }}</td>
            <td><code style="font-size: 11px;">{{ r.schema?.substring(0, 100) || '-' }}</code></td>
            <td>
              <button class="btn btn-sm btn-danger" @click="deleteResp(r.id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
      <div class="empty-state" v-else><p>暂无响应定义</p></div>
    </div>

    <!-- Versions Tab -->
    <div class="card" v-if="activeTab === 'versions'">
      <div style="margin-bottom: 12px;">
        <button class="btn btn-sm btn-accent" @click="showVerForm = true">+ 新建版本</button>
      </div>
      <table v-if="versions.length > 0">
        <thead>
          <tr><th>版本号</th><th>状态</th><th>变更说明</th><th>发布时间</th><th>操作</th></tr>
        </thead>
        <tbody>
          <tr v-for="v in versions" :key="v.id">
            <td><strong>v{{ v.version }}</strong></td>
            <td><span class="badge" :class="`badge-${v.status}`">{{ v.status }}</span></td>
            <td>{{ v.changelog || '-' }}</td>
            <td>{{ v.releaseTime || '-' }}</td>
            <td>
              <button v-if="v.status === 'DRAFT'" class="btn btn-sm" @click="doActivate(v.id)">激活</button>
              <button v-if="v.status === 'ACTIVE'" class="btn btn-sm btn-accent" @click="doDeprecateVer(v.id)">废弃</button>
              <button class="btn btn-sm btn-danger" @click="deleteVer(v.id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
      <div class="empty-state" v-else><p>暂无版本</p></div>
    </div>

    <!-- Tags Tab -->
    <div class="card" v-if="activeTab === 'tags'">
      <div style="margin-bottom: 12px; display: flex; gap: 8px; flex-wrap: wrap; align-items: center;">
        <span v-for="tag in apiTags" :key="tag.id" class="badge" :style="{ background: tag.color + '20', color: tag.color, border: '1px solid ' + tag.color }">
          {{ tag.name }}
          <span style="cursor: pointer; margin-left: 4px;" @click="toggleTag(tag.id)">×</span>
        </span>
      </div>
      <div style="margin-top: 12px;">
        <p style="font-size: 11px; color: var(--text-secondary); margin-bottom: 8px;">可用标签（点击添加/移除）</p>
        <div style="display: flex; gap: 8px; flex-wrap: wrap;">
          <span v-for="tag in allTags" :key="tag.id" class="badge badge-default" style="cursor: pointer;"
            @click="toggleTag(tag.id)">
            {{ tag.name }}
          </span>
        </div>
      </div>
    </div>

    <!-- Parameter Form Dialog -->
    <div class="dialog-overlay" v-if="showParamForm" @click.self="showParamForm = false">
      <div class="dialog">
        <h2>{{ editingParam ? '编辑参数' : '添加参数' }}</h2>
        <div class="form-group">
          <label>名称 *</label><input class="form-input" v-model="paramForm.name" placeholder="如：id" />
        </div>
        <div class="form-group">
          <label>位置 *</label>
          <select class="form-select" v-model="paramForm.location">
            <option value="PATH">PATH</option>
            <option value="QUERY">QUERY</option>
            <option value="HEADER">HEADER</option>
            <option value="BODY">BODY</option>
          </select>
        </div>
        <div class="form-group">
          <label>类型 *</label>
          <select class="form-select" v-model="paramForm.type">
            <option value="String">String</option>
            <option value="Long">Long</option>
            <option value="Integer">Integer</option>
            <option value="Boolean">Boolean</option>
          </select>
        </div>
        <div class="form-group">
          <label><input type="checkbox" v-model="paramForm.required" /> 必填</label>
        </div>
        <div class="form-group">
          <label>描述</label><input class="form-input" v-model="paramForm.description" />
        </div>
        <div class="form-group">
          <label>示例值</label><input class="form-input" v-model="paramForm.example" />
        </div>
        <div class="dialog-actions">
          <button class="btn" @click="showParamForm = false">取消</button>
          <button class="btn btn-primary" @click="submitParam">{{ editingParam ? '保存' : '添加' }}</button>
        </div>
      </div>
    </div>

    <!-- Response Form Dialog -->
    <div class="dialog-overlay" v-if="showRespForm" @click.self="showRespForm = false">
      <div class="dialog">
        <h2>添加响应</h2>
        <div class="form-group">
          <label>状态码 *</label>
          <select class="form-select" v-model="respForm.statusCode">
            <option value="200">200 OK</option>
            <option value="201">201 Created</option>
            <option value="400">400 Bad Request</option>
            <option value="401">401 Unauthorized</option>
            <option value="404">404 Not Found</option>
            <option value="500">500 Internal Server Error</option>
          </select>
        </div>
        <div class="form-group">
          <label>Schema (JSON)</label>
          <textarea class="form-textarea" v-model="respForm.schema" placeholder='{"id": 1, "name": "example"}'></textarea>
        </div>
        <div class="dialog-actions">
          <button class="btn" @click="showRespForm = false">取消</button>
          <button class="btn btn-primary" @click="submitResp">添加</button>
        </div>
      </div>
    </div>

    <!-- Version Form Dialog -->
    <div class="dialog-overlay" v-if="showVerForm" @click.self="showVerForm = false">
      <div class="dialog">
        <h2>新建版本</h2>
        <div class="form-group">
          <label>版本号 *</label><input class="form-input" v-model="verForm.version" placeholder="如：1.0" />
        </div>
        <div class="form-group">
          <label>变更说明</label><textarea class="form-textarea" v-model="verForm.changelog" placeholder="此版本变更内容..."></textarea>
        </div>
        <div class="dialog-actions">
          <button class="btn" @click="showVerForm = false">取消</button>
          <button class="btn btn-primary" @click="submitVer">创建</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import {
  getDefinition, publishDefinition, deprecateDefinition, offlineDefinition,
  getParameters, createParameter, updateParameter, deleteParameter,
  getResponses, createResponse, deleteResponse,
  getVersions, createVersion, activateVersion, deprecateVersion, deleteVersion,
  getDefinitionTags, setDefinitionTags, getTags,
  type DefinitionItem, type ParameterItem, type ResponseItem, type VersionItem, type TagItem,
} from '@/api/definitions'

const route = useRoute()
const apiId = Number(route.params.id)

const definition = ref<DefinitionItem | null>(null)
const activeTab = ref('params')

// Load basics
onMounted(async () => {
  try {
    const { data } = await getDefinition(apiId)
    definition.value = data
    loadParameters()
  } catch {
    // ignore
  }
})

// Lifecycle
async function doPublish() { await publishDefinition(apiId); reloadDef() }
async function doDeprecate() { await deprecateDefinition(apiId); reloadDef() }
async function doOffline() { await offlineDefinition(apiId); reloadDef() }
async function reloadDef() {
  const { data } = await getDefinition(apiId)
  definition.value = data
}

// Parameters
const parameters = ref<ParameterItem[]>([])
const showParamForm = ref(false)
const editingParam = ref(false)
const paramForm = ref<Partial<ParameterItem>>({})
let editParamId = 0

async function loadParameters() {
  const { data } = await getParameters(apiId)
  parameters.value = data
}
function editParam(p: ParameterItem) {
  editingParam.value = true; editParamId = p.id; paramForm.value = { ...p }; showParamForm.value = true
}
async function submitParam() {
  if (editingParam.value) {
    await updateParameter(apiId, editParamId, paramForm.value)
  } else {
    await createParameter(apiId, paramForm.value)
  }
  showParamForm.value = false; editingParam.value = false; paramForm.value = {}; loadParameters()
}
async function deleteParam(id: number) {
  await deleteParameter(apiId, id); loadParameters()
}

// Responses
const responses = ref<ResponseItem[]>([])
const showRespForm = ref(false)
const respForm = ref<Partial<ResponseItem>>({})

async function loadResponses() {
  const { data } = await getResponses(apiId)
  responses.value = data
}
async function submitResp() {
  await createResponse(apiId, respForm.value)
  showRespForm.value = false; respForm.value = {}; loadResponses()
}
async function deleteResp(id: number) {
  await deleteResponse(apiId, id); loadResponses()
}

// Versions
const versions = ref<VersionItem[]>([])
const showVerForm = ref(false)
const verForm = ref<Partial<VersionItem>>({})

async function loadVersions() {
  const { data } = await getVersions(apiId)
  versions.value = data
}
async function submitVer() {
  await createVersion(apiId, verForm.value)
  showVerForm.value = false; verForm.value = {}; loadVersions()
}
async function doActivate(id: number) { await activateVersion(apiId, id); loadVersions() }
async function doDeprecateVer(id: number) { await deprecateVersion(apiId, id); loadVersions() }
async function deleteVer(id: number) { await deleteVersion(apiId, id); loadVersions() }

// Tags
const apiTags = ref<TagItem[]>([])
const allTags = ref<TagItem[]>([])

async function loadTags() {
  const [atRes, allRes] = await Promise.all([getDefinitionTags(apiId), getTags({ page: 1, size: 100 })])
  apiTags.value = atRes.data
  allTags.value = allRes.data.items
}
async function toggleTag(tagId: number) {
  const currentIds = apiTags.value.map(t => t.id)
  const isSelected = currentIds.includes(tagId)
  const newIds = isSelected ? currentIds.filter(id => id !== tagId) : [...currentIds, tagId]
  await setDefinitionTags(apiId, newIds)
  loadTags()
}

// Tab switching triggers data loading
watch(activeTab, (tab) => {
  if (tab === 'responses') loadResponses()
  if (tab === 'versions') loadVersions()
  if (tab === 'tags') loadTags()
})

import { watch } from 'vue'
</script>
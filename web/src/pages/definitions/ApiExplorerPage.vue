<template>
  <div class="page">
    <div class="page-header" v-if="doc">
      <div>
        <router-link to="/definitions" style="font-size: 13px; color: var(--accent);">← API 目录</router-link>
        <h1 style="margin-top: 8px;">
          <span class="method-badge" :class="`method-${doc.httpMethod}`">{{ doc.httpMethod }}</span>
          {{ doc.path }}
        </h1>
        <p style="color: var(--text-secondary); margin-top: 4px;">{{ doc.name }} · {{ doc.status }}</p>
      </div>
    </div>

    <div v-if="!doc" class="empty-state"><p>加载中...</p></div>

    <template v-if="doc">
      <!-- Description -->
      <div class="card" style="margin-bottom: 20px;" v-if="doc.description">
        <h3 style="margin-bottom: 8px;">📝 描述</h3>
        <p style="margin-bottom: 8px;">{{ doc.description }}</p>
        <div style="display: flex; gap: 16px; font-size: 12px; color: var(--text-secondary);">
          <span v-if="doc.service">服务: {{ doc.service.name }}</span>
          <span>分类: {{ doc.category || '-' }}</span>
        </div>
      </div>

      <!-- Tags -->
      <div class="card" style="margin-bottom: 20px;" v-if="doc.tags && doc.tags.length > 0">
        <h3 style="margin-bottom: 8px;">🏷️ 标签</h3>
        <div style="display: flex; gap: 8px; flex-wrap: wrap;">
          <span v-for="tag in doc.tags" :key="tag.id" class="badge"
            :style="{ background: tag.color + '20', color: tag.color, border: '1px solid ' + tag.color }">
            {{ tag.name }}
          </span>
        </div>
      </div>

      <!-- Parameters grouped by location -->
      <div class="card" style="margin-bottom: 20px;" v-if="doc.parameters && Object.keys(doc.parameters).length > 0">
        <h3 style="margin-bottom: 12px;">📥 请求参数</h3>
        <div v-for="(params, location) in doc.parameters" :key="location" style="margin-bottom: 16px;">
          <h4 style="margin-bottom: 8px; color: var(--accent);">{{ location }}</h4>
          <table>
            <thead>
              <tr><th>名称</th><th>类型</th><th>必填</th><th>描述</th><th>示例</th></tr>
            </thead>
            <tbody>
              <tr v-for="p in params" :key="p.id">
                <td><strong>{{ p.name }}</strong></td>
                <td><code>{{ p.type }}</code></td>
                <td>{{ p.required ? '✅' : '-' }}</td>
                <td>{{ p.description || '-' }}</td>
                <td><code>{{ p.example || '-' }}</code></td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Request Body Schema -->
      <div class="card" style="margin-bottom: 20px;" v-if="doc.requestSchema">
        <h3 style="margin-bottom: 8px;">📦 请求体 (Request Body)</h3>
        <p v-if="doc.requestSchema.description" style="margin-bottom: 8px; color: var(--text-secondary);">{{ doc.requestSchema.description }}</p>
        <div class="code-block">
          <pre><code>{{ formatJson(doc.requestSchema.schemaJson) || '无Schema定义' }}</code></pre>
        </div>
        <div v-if="doc.requestSchema.exampleJson" style="margin-top: 12px;">
          <h4 style="margin-bottom: 4px; font-size: 13px;">示例:</h4>
          <div class="code-block">
            <pre><code>{{ formatJson(doc.requestSchema.exampleJson) }}</code></pre>
          </div>
        </div>
      </div>

      <!-- Responses -->
      <div class="card" style="margin-bottom: 20px;" v-if="doc.responses && doc.responses.length > 0">
        <h3 style="margin-bottom: 12px;">📤 响应定义</h3>
        <div v-for="r in doc.responses" :key="r.statusCode" style="margin-bottom: 16px; border-left: 3px solid var(--accent); padding-left: 12px;">
          <h4 style="margin-bottom: 4px;">
            <span class="badge badge-default">{{ r.statusCode }}</span>
            <span v-if="r.description" style="margin-left: 8px; color: var(--text-secondary); font-weight: normal;">{{ r.description }}</span>
          </h4>
          <p v-if="r.contentType" style="font-size: 12px; color: var(--text-secondary);">Content-Type: {{ r.contentType }}</p>
          <div v-if="r.schema" class="code-block" style="margin-top: 8px;">
            <pre><code>{{ formatJson(r.schema) }}</code></pre>
          </div>
          <div v-if="r.example" class="code-block" style="margin-top: 8px;">
            <pre><code>{{ formatJson(r.example) }}</code></pre>
          </div>
        </div>
      </div>

      <!-- Examples -->
      <div class="card" style="margin-bottom: 20px;" v-if="doc.examples && doc.examples.length > 0">
        <h3 style="margin-bottom: 12px;">💡 示例</h3>
        <div v-for="ex in doc.examples" :key="ex.id" style="margin-bottom: 16px;">
          <h4 style="margin-bottom: 4px;">
            <span class="badge" :class="ex.type === 'REQUEST' ? 'badge-accent' : 'badge-default'">{{ ex.type }}</span>
            <span v-if="ex.name" style="margin-left: 8px;">{{ ex.name }}</span>
          </h4>
          <div class="code-block">
            <pre><code>{{ formatJson(ex.content) || ex.content }}</code></pre>
          </div>
        </div>
      </div>

      <!-- No parameters/schema info -->
      <div class="card empty-state" v-if="!hasAnyContent">
        <p>此接口暂无详细定义信息。</p>
        <router-link :to="`/definitions/${apiId}`" style="color: var(--accent);">→ 去完善接口定义</router-link>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getApiDocs, type ApiDocument } from '@/api/definitions'

const route = useRoute()
const apiId = Number(route.params.id)
const doc = ref<ApiDocument | null>(null)

onMounted(async () => {
  try {
    const { data } = await getApiDocs(apiId)
    doc.value = data
  } catch {
    // handle error
  }
})

const hasAnyContent = computed(() => {
  if (!doc.value) return false
  const d = doc.value
  return !!(d.description || (d.parameters && Object.keys(d.parameters).length > 0)
    || d.requestSchema || (d.responses && d.responses.length > 0)
    || (d.examples && d.examples.length > 0))
})

function formatJson(str: string | null | undefined): string {
  if (!str) return ''
  try {
    return JSON.stringify(JSON.parse(str), null, 2)
  } catch {
    return str
  }
}
</script>
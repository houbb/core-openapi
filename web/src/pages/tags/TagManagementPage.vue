<template>
  <div class="page">
    <div class="page-header">
      <h1>标签管理</h1>
      <button class="btn btn-primary" @click="showCreate = true">+ 创建标签</button>
    </div>

    <div class="card" v-if="loading"><div class="loading">加载中...</div></div>
    <div class="card" v-else>
      <table v-if="tags.length > 0">
        <thead>
          <tr><th>标签名</th><th>颜色</th><th>操作</th></tr>
        </thead>
        <tbody>
          <tr v-for="tag in tags" :key="tag.id">
            <td>
              <span class="badge" :style="{ background: tag.color + '20', color: tag.color, border: '1px solid ' + tag.color }">
                {{ tag.name }}
              </span>
            </td>
            <td>
              <span :style="{ display: 'inline-block', width: 20, height: 20, borderRadius: 4, background: tag.color, verticalAlign: 'middle' }"></span>
              <code style="margin-left: 6px;">{{ tag.color }}</code>
            </td>
            <td>
              <button class="btn btn-sm" @click="editTag(tag)">编辑</button>
              <button class="btn btn-sm btn-danger" @click="confirmDelete(tag)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
      <div class="empty-state" v-else><p>暂无标签</p></div>
    </div>

    <!-- Create/Edit Dialog -->
    <div class="dialog-overlay" v-if="showCreate" @click.self="showCreate = false">
      <div class="dialog">
        <h2>{{ editing ? '编辑标签' : '创建标签' }}</h2>
        <div class="form-group">
          <label>标签名 *</label>
          <input class="form-input" v-model="form.name" placeholder="如：用户相关" />
        </div>
        <div class="form-group">
          <label>颜色</label>
          <input class="form-input" v-model="form.color" placeholder="#666" />
        </div>
        <div class="dialog-actions">
          <button class="btn" @click="showCreate = false">取消</button>
          <button class="btn btn-primary" @click="submitForm">{{ editing ? '保存' : '创建' }}</button>
        </div>
      </div>
    </div>

    <!-- Delete Confirm -->
    <div class="dialog-overlay" v-if="showDelete" @click.self="showDelete = false">
      <div class="dialog">
        <h2>确认删除</h2>
        <p>确定要删除标签 "{{ deletingTag?.name }}" 吗？</p>
        <div class="dialog-actions">
          <button class="btn" @click="showDelete = false">取消</button>
          <button class="btn btn-danger" @click="doDelete">删除</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getTags, createTag, updateTag, deleteTag, type TagItem } from '@/api/definitions'

const tags = ref<TagItem[]>([])
const loading = ref(true)

const showCreate = ref(false)
const editing = ref(false)
const form = ref<{ name: string; color: string }>({ name: '', color: '#666' })
let editId = 0

const showDelete = ref(false)
const deletingTag = ref<TagItem | null>(null)

async function loadData() {
  loading.value = true
  try {
    const { data } = await getTags({ page: 1, size: 100 })
    tags.value = data.items
  } finally {
    loading.value = false
  }
}

function editTag(tag: TagItem) {
  editing.value = true
  editId = tag.id
  form.value = { name: tag.name, color: tag.color }
  showCreate.value = true
}

async function submitForm() {
  try {
    if (editing.value) {
      await updateTag(editId, form.value)
    } else {
      await createTag(form.value)
    }
    showCreate.value = false
    editing.value = false
    form.value = { name: '', color: '#666' }
    loadData()
  } catch (err: any) {
    alert(err.response?.data?.detail || '操作失败')
  }
}

function confirmDelete(tag: TagItem) {
  deletingTag.value = tag
  showDelete.value = true
}

async function doDelete() {
  if (deletingTag.value) {
    await deleteTag(deletingTag.value.id)
    showDelete.value = false
    loadData()
  }
}

onMounted(loadData)
</script>
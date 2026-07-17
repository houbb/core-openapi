<template>
  <div class="page">
    <div class="page-header">
      <h1>用户管理</h1>
      <button class="btn btn-primary" @click="showCreateDialog = true">+ 创建用户</button>
    </div>

    <div class="table-wrapper">
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>用户名</th>
            <th>邮箱</th>
            <th>状态</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in users" :key="user.id">
            <td>{{ user.id }}</td>
            <td>{{ user.username }}</td>
            <td>{{ user.email }}</td>
            <td><span class="badge" :class="user.status === 'ACTIVE' ? 'badge-success' : 'badge-secondary'">{{ user.status }}</span></td>
            <td>{{ user.createTime }}</td>
            <td>
              <button class="btn btn-sm" @click="editUser(user)">编辑</button>
              <button class="btn btn-sm btn-danger" @click="handleDelete(user.id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="users.length === 0" class="empty">暂无用户</div>
    </div>

    <!-- Create/Edit Dialog -->
    <div v-if="showCreateDialog" class="modal-overlay" @click.self="closeDialog">
      <div class="modal">
        <h3>{{ editingUser ? '编辑用户' : '创建用户' }}</h3>
        <div class="form-group">
          <label>用户名</label>
          <input v-model="form.username" class="input" placeholder="请输入用户名" />
        </div>
        <div class="form-group">
          <label>邮箱</label>
          <input v-model="form.email" class="input" placeholder="请输入邮箱" />
        </div>
        <div class="form-group">
          <label>状态</label>
          <select v-model="form.status" class="input">
            <option value="ACTIVE">ACTIVE</option>
            <option value="INACTIVE">INACTIVE</option>
          </select>
        </div>
        <div class="form-actions">
          <button class="btn" @click="closeDialog">取消</button>
          <button class="btn btn-primary" @click="handleSave">{{ editingUser ? '更新' : '创建' }}</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getUsers, createUser, updateUser, deleteUser, type UserItem } from '@/api/apikey'

const users = ref<UserItem[]>([])
const showCreateDialog = ref(false)
const editingUser = ref<UserItem | null>(null)

const form = ref({
  username: '',
  email: '',
  status: 'ACTIVE',
})

async function loadUsers() {
  try {
    const { data } = await getUsers({ page: 1, size: 100 })
    users.value = data.items
  } catch { /* ignore */ }
}

function editUser(user: UserItem) {
  editingUser.value = user
  form.value = {
    username: user.username,
    email: user.email,
    status: user.status,
  }
  showCreateDialog.value = true
}

function closeDialog() {
  showCreateDialog.value = false
  editingUser.value = null
  form.value = { username: '', email: '', status: 'ACTIVE' }
}

async function handleSave() {
  try {
    if (editingUser.value) {
      await updateUser(editingUser.value.id, form.value)
    } else {
      await createUser(form.value)
    }
    closeDialog()
    await loadUsers()
  } catch (e: any) {
    alert(e.response?.data?.detail || '操作失败')
  }
}

async function handleDelete(id: number) {
  if (!confirm('确定删除该用户？')) return
  try {
    await deleteUser(id)
    await loadUsers()
  } catch (e: any) {
    alert(e.response?.data?.detail || '删除失败')
  }
}

onMounted(loadUsers)
</script>
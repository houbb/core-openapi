<template>
  <div class="page-center">
    <div class="card" style="max-width: 400px; width: 100%;">
      <h2>Login</h2>
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label>Username</label>
          <input v-model="form.username" class="input" placeholder="Enter username" required />
        </div>
        <div class="form-group">
          <label>Password</label>
          <input v-model="form.password" type="password" class="input" placeholder="Enter password" required />
        </div>
        <p v-if="error" class="error">{{ error }}</p>
        <button type="submit" class="btn-primary" style="width:100%;padding:10px;">Login</button>
      </form>
      <p style="margin-top:16px;text-align:center;font-size:13px;">
        Don't have an account? <a href="/#/register">Register</a>
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()
const form = ref({ username: '', password: '' })
const error = ref('')

async function handleLogin() {
  try {
    error.value = ''
    await auth.login(form.value.username, form.value.password)
    const redirect = (route.query.redirect as string) || '/app/dashboard'
    router.push(redirect)
  } catch (e: any) {
    error.value = e.response?.data?.detail || 'Login failed'
  }
}
</script>

<style scoped>
.page-center { display: flex; justify-content: center; align-items: center; min-height: 80vh; padding: 24px; }
.card { background: var(--bg-primary); border: 1px solid var(--border); border-radius: 12px; padding: 32px; }
h2 { font-size: 22px; margin-bottom: 24px; }
.form-group { margin-bottom: 16px; }
.form-group label { display: block; font-size: 13px; font-weight: 500; margin-bottom: 6px; }
.input { width: 100%; padding: 8px 12px; border: 1px solid var(--border); border-radius: 6px; font-size: 14px; box-sizing: border-box; }
.error { color: #e53e3e; font-size: 13px; margin-bottom: 12px; }
.btn-primary { background: var(--accent); color: #fff; border: none; border-radius: 6px; cursor: pointer; }
</style>
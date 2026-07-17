import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import apiClient from '@/api/client'

export interface DeveloperProfile {
  id: number
  username: string
  email: string
  developerType: string
  avatarUrl?: string
  companyName?: string
  phone?: string
  status: string
  createTime: string
  updateTime: string
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string>(localStorage.getItem('portal_token') || '')
  const profile = ref<DeveloperProfile | null>(null)
  const isAuthenticated = computed(() => !!token.value)

  async function login(username: string, password: string) {
    const { data } = await apiClient.post('/account/login', { username, password })
    token.value = data.token
    profile.value = data.profile
    localStorage.setItem('portal_token', data.token)
    return data
  }

  async function register(form: { username: string; email: string; password: string; developerType?: string }) {
    const { data } = await apiClient.post('/account/register', form)
    return data
  }

  async function fetchProfile() {
    if (!token.value) return
    try {
      const { data } = await apiClient.get('/account/profile')
      profile.value = data
    } catch {
      logout()
    }
  }

  function logout() {
    token.value = ''
    profile.value = null
    localStorage.removeItem('portal_token')
  }

  return { token, profile, isAuthenticated, login, register, logout, fetchProfile }
})
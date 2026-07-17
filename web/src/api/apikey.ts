import apiClient from './client'
import type { PageResult } from './services'

// ---- Types ----

export interface UserItem {
  id: number
  username: string
  email: string
  status: string
  createTime: string
  updateTime: string
}

export interface ApplicationItem {
  id: number
  appName: string
  appCode: string
  ownerId: number | null
  description: string
  status: string
  createTime: string
  updateTime: string
}

export interface ApiKeyItem {
  id: number
  applicationId: number
  keyPrefix: string
  name: string
  environment: string
  status: string
  expireTime: string | null
  lastUsedTime: string | null
  createdTime: string
  rawKey?: string | null  // Only present on creation
}

export interface PermissionItem {
  id: number
  name: string
  description: string
}

export interface DashboardStats {
  serviceCount: number
  definitionCount: number
  publishedCount: number
  userCount: number
  applicationCount: number
  activeKeyCount: number
}

// ---- Users ----

export function getUsers(params: { page?: number; size?: number; keyword?: string }) {
  return apiClient.get<PageResult<UserItem>>('/users', { params })
}

export function getUser(id: number) {
  return apiClient.get<UserItem>(`/users/${id}`)
}

export function createUser(data: { username: string; email?: string; status?: string }) {
  return apiClient.post<UserItem>('/users', data)
}

export function updateUser(id: number, data: { username: string; email?: string; status?: string }) {
  return apiClient.put<UserItem>(`/users/${id}`, data)
}

export function deleteUser(id: number) {
  return apiClient.delete(`/users/${id}`)
}

// ---- Applications ----

export function getApplications(params: { page?: number; size?: number; keyword?: string }) {
  return apiClient.get<PageResult<ApplicationItem>>('/applications', { params })
}

export function getApplication(id: number) {
  return apiClient.get<ApplicationItem>(`/applications/${id}`)
}

export function createApplication(data: { appName: string; appCode: string; ownerId?: number; description?: string }) {
  return apiClient.post<ApplicationItem>('/applications', data)
}

export function updateApplication(id: number, data: { appName: string; appCode: string; ownerId?: number; description?: string; status?: string }) {
  return apiClient.put<ApplicationItem>(`/applications/${id}`, data)
}

export function deleteApplication(id: number) {
  return apiClient.delete(`/applications/${id}`)
}

// ---- API Keys (under Application) ----

export function getApiKeys(applicationId: number) {
  return apiClient.get<ApiKeyItem[]>(`/applications/${applicationId}/keys`)
}

export function createApiKey(applicationId: number, data: { environment: string; name?: string; expireTime?: string }) {
  return apiClient.post<ApiKeyItem>(`/applications/${applicationId}/keys`, data)
}

export function disableApiKey(applicationId: number, keyId: number) {
  return apiClient.post<ApiKeyItem>(`/applications/${applicationId}/keys/${keyId}/disable`)
}

export function enableApiKey(applicationId: number, keyId: number) {
  return apiClient.post<ApiKeyItem>(`/applications/${applicationId}/keys/${keyId}/enable`)
}

export function deleteApiKey(applicationId: number, keyId: number) {
  return apiClient.delete(`/applications/${applicationId}/keys/${keyId}`)
}

// ---- Permissions (under Application) ----

export function getApplicationPermissions(applicationId: number) {
  return apiClient.get<PermissionItem[]>(`/applications/${applicationId}/permissions`)
}

export function grantPermission(applicationId: number, permissionId: number) {
  return apiClient.post<PermissionItem[]>(`/applications/${applicationId}/permissions`, { permissionId })
}

export function revokePermission(applicationId: number, permissionId: number) {
  return apiClient.delete<PermissionItem[]>(`/applications/${applicationId}/permissions/${permissionId}`)
}

// ---- Permissions (global) ----

export function getPermissions(params: { page?: number; size?: number; keyword?: string }) {
  return apiClient.get<PageResult<PermissionItem>>('/permissions', { params })
}

export function createPermission(data: { name: string; description?: string }) {
  return apiClient.post<PermissionItem>('/permissions', data)
}

export function updatePermission(id: number, data: { name: string; description?: string }) {
  return apiClient.put<PermissionItem>(`/permissions/${id}`, data)
}

export function deletePermission(id: number) {
  return apiClient.delete(`/permissions/${id}`)
}

// ---- Dashboard ----

export function getDashboardStats() {
  return apiClient.get<DashboardStats>('/dashboard')
}

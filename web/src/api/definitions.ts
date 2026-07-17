import apiClient from './client'
import type { PageResult } from './services'

export interface DefinitionItem {
  id: number
  serviceId: number
  name: string
  path: string
  httpMethod: string
  description: string
  category: string
  status: string
  createTime: string
  updateTime: string
}

export interface ParameterItem {
  id: number
  apiId: number
  name: string
  location: string
  type: string
  required: boolean
  description: string
  example: string
  sortOrder: number
}

export interface ResponseItem {
  id: number
  apiId: number
  statusCode: string
  contentType: string
  schema: string
  example: string
}

export interface VersionItem {
  id: number
  apiId: number
  version: string
  status: string
  changelog: string
  releaseTime: string
  deprecatedTime: string
  createTime: string
  updateTime: string
}

export interface TagItem {
  id: number
  name: string
  color: string
  createTime: string
  updateTime: string
}

// Definitions
export function getDefinitions(params: {
  page?: number
  size?: number
  serviceId?: number
  keyword?: string
  status?: string
}) {
  return apiClient.get<PageResult<DefinitionItem>>('/definitions', { params })
}

export function getDefinitionsByService(serviceId: number, params: { page?: number; size?: number }) {
  return apiClient.get<PageResult<DefinitionItem>>(`/services/${serviceId}/definitions`, { params })
}

export function getDefinition(id: number) {
  return apiClient.get<DefinitionItem>(`/definitions/${id}`)
}

export function createDefinition(data: Partial<DefinitionItem>) {
  return apiClient.post<DefinitionItem>('/definitions', data)
}

export function updateDefinition(id: number, data: Partial<DefinitionItem>) {
  return apiClient.put<DefinitionItem>(`/definitions/${id}`, data)
}

export function deleteDefinition(id: number) {
  return apiClient.delete(`/definitions/${id}`)
}

export function publishDefinition(id: number) {
  return apiClient.post<DefinitionItem>(`/definitions/${id}/publish`)
}

export function deprecateDefinition(id: number) {
  return apiClient.post<DefinitionItem>(`/definitions/${id}/deprecate`)
}

export function offlineDefinition(id: number) {
  return apiClient.post<DefinitionItem>(`/definitions/${id}/offline`)
}

// Parameters
export function getParameters(apiId: number) {
  return apiClient.get<ParameterItem[]>(`/definitions/${apiId}/parameters`)
}

export function createParameter(apiId: number, data: Partial<ParameterItem>) {
  return apiClient.post<ParameterItem>(`/definitions/${apiId}/parameters`, data)
}

export function updateParameter(apiId: number, id: number, data: Partial<ParameterItem>) {
  return apiClient.put<ParameterItem>(`/definitions/${apiId}/parameters/${id}`, data)
}

export function deleteParameter(apiId: number, id: number) {
  return apiClient.delete(`/definitions/${apiId}/parameters/${id}`)
}

// Responses
export function getResponses(apiId: number) {
  return apiClient.get<ResponseItem[]>(`/definitions/${apiId}/responses`)
}

export function createResponse(apiId: number, data: Partial<ResponseItem>) {
  return apiClient.post<ResponseItem>(`/definitions/${apiId}/responses`, data)
}

export function updateResponse(apiId: number, id: number, data: Partial<ResponseItem>) {
  return apiClient.put<ResponseItem>(`/definitions/${apiId}/responses/${id}`, data)
}

export function deleteResponse(apiId: number, id: number) {
  return apiClient.delete(`/definitions/${apiId}/responses/${id}`)
}

// Versions
export function getVersions(apiId: number) {
  return apiClient.get<VersionItem[]>(`/definitions/${apiId}/versions`)
}

export function createVersion(apiId: number, data: Partial<VersionItem>) {
  return apiClient.post<VersionItem>(`/definitions/${apiId}/versions`, data)
}

export function updateVersion(apiId: number, id: number, data: Partial<VersionItem>) {
  return apiClient.put<VersionItem>(`/definitions/${apiId}/versions/${id}`, data)
}

export function activateVersion(apiId: number, id: number) {
  return apiClient.post<VersionItem>(`/definitions/${apiId}/versions/${id}/activate`)
}

export function deprecateVersion(apiId: number, id: number) {
  return apiClient.post<VersionItem>(`/definitions/${apiId}/versions/${id}/deprecate`)
}

export function deleteVersion(apiId: number, id: number) {
  return apiClient.delete(`/definitions/${apiId}/versions/${id}`)
}

// Tags
export function getDefinitionTags(apiId: number) {
  return apiClient.get<TagItem[]>(`/definitions/${apiId}/tags`)
}

export function setDefinitionTags(apiId: number, tagIds: number[]) {
  return apiClient.put(`/definitions/${apiId}/tags`, { tagIds })
}

// Dashboard
export function getDashboard() {
  return apiClient.get('/dashboard')
}

// Tags
export function getTags(params: { page?: number; size?: number }) {
  return apiClient.get<PageResult<TagItem>>('/tags', { params })
}

export function createTag(data: { name: string; color?: string }) {
  return apiClient.post<TagItem>('/tags', data)
}

export function updateTag(id: number, data: { name: string; color?: string }) {
  return apiClient.put<TagItem>(`/tags/${id}`, data)
}

export function deleteTag(id: number) {
  return apiClient.delete(`/tags/${id}`)
}
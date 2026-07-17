import apiClient from './client'

export interface SdkProject {
  id: number
  name: string
  language: string
  version: string
  status: string
  errorMessage?: string
  createTime: string
  updateTime: string
}

export interface SdkGeneration {
  id: number
  sdkProjectId: number
  apiIds: string
  apiVersion?: string
  generatorVersion?: string
  status: string
  downloadUrl?: string
  fileSize?: number
  errorMessage?: string
  createTime: string
  updateTime: string
}

export interface PageResult<T> {
  items: T[]
  page: number
  size: number
  total: number
  hasNext: boolean
}

export interface SdkGenerateRequest {
  name: string
  language: string
  version: string
  apiIds: number[]
}

export function getSdkProjects(page = 1, size = 20): Promise<PageResult<SdkProject>> {
  return apiClient.get('/sdk/projects', { params: { page, size } }).then(r => r.data)
}

export function getSdkProject(id: number): Promise<SdkProject> {
  return apiClient.get(`/sdk/projects/${id}`).then(r => r.data)
}

export function getSdkGenerations(projectId: number, page = 1, size = 20): Promise<PageResult<SdkGeneration>> {
  return apiClient.get(`/sdk/projects/${projectId}/generations`, { params: { page, size } }).then(r => r.data)
}

export function generateSdk(data: SdkGenerateRequest): Promise<SdkGeneration> {
  return apiClient.post('/sdk/generate', data).then(r => r.data)
}

export function getSdkGeneration(id: number): Promise<SdkGeneration> {
  return apiClient.get(`/sdk/generations/${id}`).then(r => r.data)
}

export function getSdkDownloadUrl(generationId: number): string {
  return `/api/v1/openapi/sdk/generations/${generationId}/download`
}

export function deleteSdkProject(id: number): Promise<void> {
  return apiClient.delete(`/sdk/projects/${id}`)
}
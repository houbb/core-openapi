import apiClient from './client'

export interface ServiceItem {
  id: number
  serviceName: string
  serviceCode: string
  description: string
  owner: string
  status: string
  version: string
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

export function getServices(params: { page?: number; size?: number; keyword?: string }) {
  return apiClient.get<PageResult<ServiceItem>>('/services', { params })
}

export function getService(id: number) {
  return apiClient.get<ServiceItem>(`/services/${id}`)
}

export function createService(data: Partial<ServiceItem>) {
  return apiClient.post<ServiceItem>('/services', data)
}

export function updateService(id: number, data: Partial<ServiceItem>) {
  return apiClient.put<ServiceItem>(`/services/${id}`, data)
}

export function deleteService(id: number) {
  return apiClient.delete(`/services/${id}`)
}
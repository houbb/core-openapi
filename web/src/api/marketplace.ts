import apiClient from './client'

// ========== Provider Types ==========
export interface Provider {
  id: number
  name: string
  description: string
  type: string
  ownerId: number
  verified: number
  status: string
  contactEmail: string
  website: string
  logoUrl: string
  createTime: string
}

export interface ProviderRequest {
  name: string
  description?: string
  type?: string
  ownerId?: number
  contactEmail?: string
  website?: string
  logoUrl?: string
}

// ========== Product Types ==========
export interface Product {
  id: number
  name: string
  description: string
  providerId: number
  providerName?: string
  apiId: number
  category: string
  iconUrl: string
  status: string
  createTime: string
  updateTime: string
}

export interface ProductRequest {
  name: string
  description?: string
  providerId?: number
  apiId?: number
  category?: string
  iconUrl?: string
}

// ========== Plan Types ==========
export interface Plan {
  id: number
  productId: number
  name: string
  description: string
  price: number
  billingType: string
  limitConfig: string
  status: string
  sortOrder: number
  createTime: string
}

export interface PlanRequest {
  name: string
  description?: string
  price?: number
  billingType?: string
  limitConfig?: string
  sortOrder?: number
}

// ========== Review Types ==========
export interface Review {
  id: number
  productId: number
  userId: number
  username?: string
  rating: number
  comment: string
  createTime: string
}

export interface ReviewRequest {
  rating: number
  comment?: string
}

// ========== Page Result ==========
export interface PageResult<T> {
  items: T[]
  page: number
  size: number
  total: number
  hasNext: boolean
}

// ========== Dashboard ==========
export interface MarketplaceDashboard {
  totalProducts: number
  publishedProducts: number
  pendingReview: number
  totalProviders: number
  verifiedProviders: number
}

// ========== Listing ==========
export interface ListingRequest {
  featured?: number
  sortOrder?: number
  tags?: string
  highlightText?: string
}

// ========== Provider APIs ==========
export function getProviders(params?: { page?: number; size?: number; keyword?: string; type?: string; status?: string }) {
  return apiClient.get<PageResult<Provider>>('/marketplace/providers', { params })
}

export function getProvider(id: number) {
  return apiClient.get<Provider>(`/marketplace/providers/${id}`)
}

export function createProvider(data: ProviderRequest) {
  return apiClient.post<Provider>('/marketplace/providers', data)
}

export function updateProvider(id: number, data: ProviderRequest) {
  return apiClient.put<Provider>(`/marketplace/providers/${id}`, data)
}

export function verifyProvider(id: number) {
  return apiClient.post<Provider>(`/marketplace/providers/${id}/verify`)
}

export function toggleProviderStatus(id: number, status: string) {
  return apiClient.post<Provider>(`/marketplace/providers/${id}/status?status=${status}`)
}

export function deleteProvider(id: number) {
  return apiClient.delete(`/marketplace/providers/${id}`)
}

// ========== Product APIs ==========
export function getProducts(params?: { page?: number; size?: number; keyword?: string; category?: string; status?: string; providerId?: number }) {
  return apiClient.get<PageResult<Product>>('/marketplace/products', { params })
}

export function getProduct(id: number) {
  return apiClient.get<Product>(`/marketplace/products/${id}`)
}

export function createProduct(data: ProductRequest) {
  return apiClient.post<Product>('/marketplace/products', data)
}

export function updateProduct(id: number, data: ProductRequest) {
  return apiClient.put<Product>(`/marketplace/products/${id}`, data)
}

export function publishProduct(id: number) {
  return apiClient.post<Product>(`/marketplace/products/${id}/publish`)
}

export function deprecateProduct(id: number) {
  return apiClient.post<Product>(`/marketplace/products/${id}/deprecate`)
}

export function deleteProduct(id: number) {
  return apiClient.delete(`/marketplace/products/${id}`)
}

// ========== Plan APIs ==========
export function getPlans(productId: number) {
  return apiClient.get<Plan[]>(`/marketplace/products/${productId}/plans`)
}

export function createPlan(productId: number, data: PlanRequest) {
  return apiClient.post<Plan>(`/marketplace/products/${productId}/plans`, data)
}

export function updatePlan(productId: number, id: number, data: PlanRequest) {
  return apiClient.put<Plan>(`/marketplace/products/${productId}/plans/${id}`, data)
}

export function deletePlan(productId: number, id: number) {
  return apiClient.delete(`/marketplace/products/${productId}/plans/${id}`)
}

// ========== Review APIs ==========
export function getReviews(productId: number, params?: { page?: number; size?: number }) {
  return apiClient.get<PageResult<Review>>(`/marketplace/products/${productId}/reviews`, { params })
}

export function deleteReview(productId: number, id: number) {
  return apiClient.delete(`/marketplace/products/${productId}/reviews/${id}`)
}

export function getReviewStats(productId: number) {
  return apiClient.get<{ avgRating: number; count: number }>(`/marketplace/products/${productId}/reviews/stats`)
}

// ========== Admin APIs ==========
export function getAdminDashboard() {
  return apiClient.get<MarketplaceDashboard>('/marketplace/admin/dashboard')
}

export function saveListing(productId: number, data: ListingRequest) {
  return apiClient.post(`/marketplace/admin/products/${productId}/listing`, data)
}

export function getFeatured() {
  return apiClient.get('/marketplace/admin/featured')
}

// ========== Provider Dashboard ==========
export function getProviderDashboard(providerId: number) {
  return apiClient.get('/marketplace/provider/dashboard', { params: { providerId } })
}

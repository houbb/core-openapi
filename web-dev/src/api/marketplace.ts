import axios from 'axios'

const marketplaceClient = axios.create({
  baseURL: '/api/v1/marketplace/public',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' },
})

export interface MarketplaceItem {
  id: number
  name: string
  description: string
  category: string
  iconUrl: string
  providerName: string
  providerType: string
  verified: boolean
  avgRating: number
  reviewCount: number
}

export interface ProductDetail {
  id: number
  name: string
  description: string
  category: string
  iconUrl: string
  status: string
  provider: { id: number; name: string; type: string; verified: number }
  plans: Array<{ id: number; name: string; description: string; price: number; billingType: string; limitConfig: string }>
  avgRating: number
  reviewCount: number
  createTime: string
}

export function getFeatured() {
  return marketplaceClient.get<MarketplaceItem[]>('/featured')
}

export function getByCategory(category: string, limit = 20) {
  return marketplaceClient.get<MarketplaceItem[]>(`/category/${category}`, { params: { limit } })
}

export function searchProducts(keyword: string) {
  return marketplaceClient.get<MarketplaceItem[]>('/search', { params: { keyword } })
}

export function getProductDetail(id: number) {
  return marketplaceClient.get<ProductDetail>(`/products/${id}`)
}
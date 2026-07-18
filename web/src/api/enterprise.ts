import apiClient from './client'

// ========== Organization Types ==========
export interface Organization {
  id: number
  name: string
  code: string
  type: string
  ownerId: number
  status: string
  description: string
  logoUrl: string
  website: string
  contactEmail: string
  contactPhone: string
  tenantId: string
  createTime: string
  updateTime: string
}

export interface OrganizationRequest {
  name: string
  type?: string
  ownerId?: number
  description?: string
  contactEmail?: string
  contactPhone?: string
  website?: string
  logoUrl?: string
}

// ========== Team Types ==========
export interface Team {
  id: number
  organizationId: number
  parentId: number
  name: string
  description: string
  leaderId: number
  status: string
  sortOrder: number
  createTime: string
  updateTime: string
}

export interface TeamRequest {
  name: string
  description?: string
  parentId?: number
  leaderId?: number
}

// ========== Member Types ==========
export interface Member {
  id: number
  organizationId: number
  teamId: number
  userId: number
  role: string
  status: string
  joinedAt: string
  createTime: string
  updateTime: string
}

export interface MemberRequest {
  userId: number
  teamId?: number
  role?: string
}

// ========== Partner Types ==========
export interface Partner {
  id: number
  organizationId: number
  name: string
  level: string
  status: string
  contactName: string
  contactEmail: string
  contactPhone: string
  description: string
  createTime: string
  updateTime: string
}

export interface PartnerRequest {
  name: string
  level?: string
  contactName?: string
  contactEmail?: string
  contactPhone?: string
  description?: string
  organizationId?: number
}

// ========== Contract Types ==========
export interface Contract {
  id: number
  organizationId: number
  contractNo: string
  name: string
  planName: string
  startDate: string
  endDate: string
  status: string
  maxRequests: number
  maxQps: number
  supportsPhone: number
  supports724: number
  description: string
  createTime: string
  updateTime: string
}

export interface ContractRequest {
  contractNo: string
  name: string
  planName?: string
  startDate?: string
  endDate?: string
  maxRequests?: number
  maxQps?: number
  supportsPhone?: number
  supports724?: number
  description?: string
}

// ========== SLA Types ==========
export interface SlaPolicy {
  id: number
  organizationId: number
  name: string
  availability: number
  responseTimeMs: number
  latencyP99Ms: number
  supportLevel: string
  incidentResponseMin: number
  status: string
  createTime: string
  updateTime: string
}

export interface SlaPolicyRequest {
  name: string
  availability?: number
  responseTimeMs?: number
  latencyP99Ms?: number
  supportLevel?: string
  incidentResponseMin?: number
}

// ========== Compliance Types ==========
export interface CompliancePolicy {
  id: number
  organizationId: number
  name: string
  policyType: string
  configJson: string
  status: string
  createTime: string
  updateTime: string
}

export interface CompliancePolicyRequest {
  name: string
  policyType: string
  configJson?: string
  status?: string
}

// ========== Identity Provider Types ==========
export interface IdentityProvider {
  id: number
  organizationId: number
  providerType: string
  name: string
  configJson: string
  isDefault: number
  status: string
  createTime: string
  updateTime: string
}

export interface IdentityProviderRequest {
  name: string
  providerType: string
  configJson?: string
  isDefault?: number
  status?: string
}

// ========== Billing Types ==========
export interface BillingAccount {
  id: number
  organizationId: number
  accountName: string
  balance: number
  currency: string
  status: string
  createTime: string
  updateTime: string
}

export interface BillingAccountRequest {
  accountName: string
  currency?: string
}

// ========== Dashboard Types ==========
export interface EnterpriseDashboard {
  totalOrganizations: number
  activeOrganizations: number
  totalPartners: number
  strategicPartners: number
  activeContracts: number
  draftContracts: number
  details: Record<string, any>
}

// ========== Page Result ==========
export interface PageResult<T> {
  items: T[]
  page: number
  size: number
  total: number
  hasNext: boolean
}

// ========== Organization APIs ==========
export function getOrganizations(params?: { page?: number; size?: number; keyword?: string; type?: string; status?: string }) {
  return apiClient.get<PageResult<Organization>>('/enterprise/organizations', { params })
}

export function getOrganization(id: number) {
  return apiClient.get<Organization>(`/enterprise/organizations/${id}`)
}

export function createOrganization(data: OrganizationRequest) {
  return apiClient.post<Organization>('/enterprise/organizations', data)
}

export function updateOrganization(id: number, data: OrganizationRequest) {
  return apiClient.put<Organization>(`/enterprise/organizations/${id}`, data)
}

export function updateOrganizationStatus(id: number, status: string) {
  return apiClient.post<Organization>(`/enterprise/organizations/${id}/status?status=${status}`)
}

export function deleteOrganization(id: number) {
  return apiClient.delete(`/enterprise/organizations/${id}`)
}

// ========== Team APIs ==========
export function getTeams(orgId: number) {
  return apiClient.get<Team[]>(`/enterprise/organizations/${orgId}/teams`)
}

export function createTeam(orgId: number, data: TeamRequest) {
  return apiClient.post<Team>(`/enterprise/organizations/${orgId}/teams`, data)
}

export function updateTeam(orgId: number, id: number, data: TeamRequest) {
  return apiClient.put<Team>(`/enterprise/organizations/${orgId}/teams/${id}`, data)
}

export function deleteTeam(orgId: number, id: number) {
  return apiClient.delete(`/enterprise/organizations/${orgId}/teams/${id}`)
}

// ========== Member APIs ==========
export function getMembers(orgId: number, params?: { page?: number; size?: number; teamId?: number; role?: string }) {
  return apiClient.get<PageResult<Member>>(`/enterprise/organizations/${orgId}/members`, { params })
}

export function addMember(orgId: number, data: MemberRequest) {
  return apiClient.post<Member>(`/enterprise/organizations/${orgId}/members`, data)
}

export function updateMemberRole(orgId: number, id: number, role: string) {
  return apiClient.put<Member>(`/enterprise/organizations/${orgId}/members/${id}/role?role=${role}`)
}

export function changeMemberTeam(orgId: number, id: number, teamId: number) {
  return apiClient.put<Member>(`/enterprise/organizations/${orgId}/members/${id}/team?teamId=${teamId}`)
}

export function updateMemberStatus(orgId: number, id: number, status: string) {
  return apiClient.post<Member>(`/enterprise/organizations/${orgId}/members/${id}/status?status=${status}`)
}

export function removeMember(orgId: number, id: number) {
  return apiClient.delete(`/enterprise/organizations/${orgId}/members/${id}`)
}

// ========== Partner APIs ==========
export function getPartners(params?: { page?: number; size?: number; keyword?: string; level?: string; status?: string; organizationId?: number }) {
  return apiClient.get<PageResult<Partner>>('/enterprise/partners', { params })
}

export function getPartner(id: number) {
  return apiClient.get<Partner>(`/enterprise/partners/${id}`)
}

export function createPartner(data: PartnerRequest) {
  return apiClient.post<Partner>('/enterprise/partners', data)
}

export function updatePartner(id: number, data: PartnerRequest) {
  return apiClient.put<Partner>(`/enterprise/partners/${id}`, data)
}

export function updatePartnerStatus(id: number, status: string) {
  return apiClient.post<Partner>(`/enterprise/partners/${id}/status?status=${status}`)
}

export function deletePartner(id: number) {
  return apiClient.delete(`/enterprise/partners/${id}`)
}

// ========== Contract APIs ==========
export function getContracts(params?: { page?: number; size?: number; organizationId?: number; status?: string }) {
  return apiClient.get<PageResult<Contract>>('/enterprise/contracts', { params })
}

export function getContract(id: number) {
  return apiClient.get<Contract>(`/enterprise/contracts/${id}`)
}

export function createContract(data: ContractRequest) {
  return apiClient.post<Contract>('/enterprise/contracts', data)
}

export function updateContract(id: number, data: ContractRequest) {
  return apiClient.put<Contract>(`/enterprise/contracts/${id}`, data)
}

export function activateContract(id: number) {
  return apiClient.post<Contract>(`/enterprise/contracts/${id}/activate`)
}

export function expireContract(id: number) {
  return apiClient.post<Contract>(`/enterprise/contracts/${id}/expire`)
}

export function deleteContract(id: number) {
  return apiClient.delete(`/enterprise/contracts/${id}`)
}

// ========== SLA APIs ==========
export function getSlaByOrg(orgId: number) {
  return apiClient.get<SlaPolicy>(`/enterprise/sla-policies/by-org/${orgId}`)
}

export function createOrUpdateSla(orgId: number, data: SlaPolicyRequest) {
  return apiClient.post<SlaPolicy>(`/enterprise/sla-policies?organizationId=${orgId}`, data)
}

export function deleteSla(id: number) {
  return apiClient.delete(`/enterprise/sla-policies/${id}`)
}

// ========== Compliance APIs ==========
export function getComplianceByOrg(orgId: number) {
  return apiClient.get<CompliancePolicy[]>(`/enterprise/compliance/by-org/${orgId}`)
}

export function createCompliance(orgId: number, data: CompliancePolicyRequest) {
  return apiClient.post<CompliancePolicy>(`/enterprise/compliance?organizationId=${orgId}`, data)
}

export function updateCompliance(id: number, data: CompliancePolicyRequest) {
  return apiClient.put<CompliancePolicy>(`/enterprise/compliance/${id}`, data)
}

export function deleteCompliance(id: number) {
  return apiClient.delete(`/enterprise/compliance/${id}`)
}

// ========== Identity APIs ==========
export function getIdentityByOrg(orgId: number) {
  return apiClient.get<IdentityProvider[]>(`/enterprise/identity/by-org/${orgId}`)
}

export function createIdentity(orgId: number, data: IdentityProviderRequest) {
  return apiClient.post<IdentityProvider>(`/enterprise/identity?organizationId=${orgId}`, data)
}

export function updateIdentity(id: number, data: IdentityProviderRequest) {
  return apiClient.put<IdentityProvider>(`/enterprise/identity/${id}`, data)
}

export function deleteIdentity(id: number) {
  return apiClient.delete(`/enterprise/identity/${id}`)
}

// ========== Billing APIs ==========
export function getBillingByOrg(orgId: number) {
  return apiClient.get<BillingAccount>(`/enterprise/billing/by-org/${orgId}`)
}

export function createOrGetBilling(orgId: number, data: BillingAccountRequest) {
  return apiClient.post<BillingAccount>(`/enterprise/billing?organizationId=${orgId}`, data)
}

export function deleteBilling(id: number) {
  return apiClient.delete(`/enterprise/billing/${id}`)
}

// ========== Dashboard APIs ==========
export function getEnterpriseDashboard() {
  return apiClient.get<EnterpriseDashboard>('/enterprise/dashboard')
}

export function getOrgDashboard(orgId: number) {
  return apiClient.get<Record<string, any>>(`/enterprise/dashboard/organization/${orgId}`)
}
import { createRouter, createWebHashHistory } from 'vue-router'
import AdminLayout from '@/layouts/AdminLayout.vue'

const routes = [
  {
    path: '/',
    component: AdminLayout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/pages/DashboardPage.vue'),
        meta: { title: '仪表盘' },
      },
      {
        path: 'services',
        name: 'ServiceList',
        component: () => import('@/pages/services/ServiceListPage.vue'),
        meta: { title: '服务管理' },
      },
      {
        path: 'services/:id',
        name: 'ServiceDetail',
        component: () => import('@/pages/services/ServiceDetailPage.vue'),
        meta: { title: '服务详情' },
      },
      {
        path: 'definitions',
        name: 'DefinitionList',
        component: () => import('@/pages/definitions/DefinitionListPage.vue'),
        meta: { title: 'API 目录' },
      },
      {
        path: 'definitions/:id',
        name: 'DefinitionDetail',
        component: () => import('@/pages/definitions/DefinitionDetailPage.vue'),
        meta: { title: 'API 详情' },
      },
      {
        path: 'definitions/:id/explorer',
        name: 'ApiExplorer',
        component: () => import('@/pages/definitions/ApiExplorerPage.vue'),
        meta: { title: 'API 详情' },
      },
      {
        path: 'tags',
        name: 'TagManagement',
        component: () => import('@/pages/tags/TagManagementPage.vue'),
        meta: { title: '标签管理' },
      },
      {
        path: 'users',
        name: 'UserList',
        component: () => import('@/pages/users/UserListPage.vue'),
        meta: { title: '用户管理' },
      },
      {
        path: 'applications',
        name: 'ApplicationList',
        component: () => import('@/pages/applications/ApplicationListPage.vue'),
        meta: { title: '应用管理' },
      },
      {
        path: 'applications/:id',
        name: 'ApplicationDetail',
        component: () => import('@/pages/applications/ApplicationDetailPage.vue'),
        meta: { title: '应用详情' },
      },
      {
        path: 'sdk',
        name: 'SdkCenter',
        component: () => import('@/pages/sdk/SdkCenterPage.vue'),
        meta: { title: 'SDK 管理' },
      },
      {
        path: 'marketplace',
        name: 'Marketplace',
        component: () => import('@/pages/marketplace/MarketplacePage.vue'),
        meta: { title: 'Marketplace' },
      },
      {
        path: 'marketplace/products',
        name: 'ProductManage',
        component: () => import('@/pages/marketplace/ProductManagePage.vue'),
        meta: { title: '商品管理' },
      },
      {
        path: 'marketplace/providers',
        name: 'ProviderManage',
        component: () => import('@/pages/marketplace/ProviderManagePage.vue'),
        meta: { title: 'Provider 管理' },
      },
      {
        path: 'marketplace/providers/:id',
        name: 'ProviderDetail',
        component: () => import('@/pages/marketplace/ProviderDetailPage.vue'),
        meta: { title: 'Provider 详情' },
      },
      {
        path: 'enterprise',
        name: 'EnterpriseDashboard',
        component: () => import('@/pages/enterprise/EnterpriseDashboardPage.vue'),
        meta: { title: '企业控制台' },
      },
      {
        path: 'enterprise/organizations',
        name: 'OrganizationList',
        component: () => import('@/pages/enterprise/OrganizationListPage.vue'),
        meta: { title: '组织管理' },
      },
      {
        path: 'enterprise/organizations/:id',
        name: 'OrganizationDetail',
        component: () => import('@/pages/enterprise/OrganizationDetailPage.vue'),
        meta: { title: '组织详情' },
      },
      {
        path: 'enterprise/partners',
        name: 'PartnerList',
        component: () => import('@/pages/enterprise/PartnerListPage.vue'),
        meta: { title: '合作伙伴' },
      },
      {
        path: 'enterprise/contracts',
        name: 'ContractList',
        component: () => import('@/pages/enterprise/ContractListPage.vue'),
        meta: { title: '合同管理' },
      },
      {
        path: 'enterprise/sla',
        name: 'SlaPolicy',
        component: () => import('@/pages/enterprise/SlaPolicyPage.vue'),
        meta: { title: 'SLA 策略' },
      },
      {
        path: 'enterprise/governance',
        name: 'ApiGovernance',
        component: () => import('@/pages/enterprise/ApiGovernancePage.vue'),
        meta: { title: 'API 治理' },
      },
      {
        path: 'enterprise/audit',
        name: 'AuditLog',
        component: () => import('@/pages/enterprise/AuditLogPage.vue'),
        meta: { title: '审计日志' },
      },
      {
        path: 'enterprise/compliance',
        name: 'Compliance',
        component: () => import('@/pages/enterprise/CompliancePage.vue'),
        meta: { title: '合规管理' },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHashHistory(),
  routes,
})

export default router
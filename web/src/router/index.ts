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
    ],
  },
]

const router = createRouter({
  history: createWebHashHistory(),
  routes,
})

export default router
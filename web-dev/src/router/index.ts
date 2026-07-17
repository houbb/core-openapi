import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    component: () => import('@/layouts/PublicLayout.vue'),
    children: [
      { path: '', name: 'Landing', component: () => import('@/pages/LandingPage.vue') },
      { path: 'login', name: 'Login', component: () => import('@/pages/LoginPage.vue') },
      { path: 'register', name: 'Register', component: () => import('@/pages/RegisterPage.vue') },
      { path: 'catalog', name: 'Catalog', component: () => import('@/pages/catalog/CatalogListPage.vue') },
      { path: 'catalog/:id', name: 'ApiDetail', component: () => import('@/pages/catalog/ApiDetailPage.vue') },
      { path: 'docs', name: 'Docs', component: () => import('@/pages/docs/DocsLayoutPage.vue'), children: [
        { path: ':slug', name: 'DocContent', component: () => import('@/pages/docs/DocContentPage.vue') }
      ]},
      { path: 'sdk', name: 'SdkList', component: () => import('@/pages/sdk/SdkListPage.vue') },
    ],
  },
  {
    path: '/app',
    component: () => import('@/layouts/DeveloperLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      { path: '', redirect: '/app/dashboard' },
      { path: 'dashboard', name: 'Dashboard', component: () => import('@/pages/DashboardPage.vue') },
      { path: 'apps', name: 'MyApps', component: () => import('@/pages/apps/MyAppsPage.vue') },
      { path: 'apps/:id', name: 'AppDetail', component: () => import('@/pages/apps/AppDetailPage.vue') },
      { path: 'credentials', name: 'Credentials', component: () => import('@/pages/credentials/CredentialListPage.vue') },
      { path: 'playground', name: 'Playground', component: () => import('@/pages/playground/PlaygroundPage.vue') },
      { path: 'playground/:apiId', name: 'PlaygroundWithApi', component: () => import('@/pages/playground/PlaygroundPage.vue') },
      { path: 'analytics', name: 'Analytics', component: () => import('@/pages/analytics/AnalyticsPage.vue') },
      { path: 'settings', name: 'Settings', component: () => import('@/pages/settings/SettingsPage.vue') },
      { path: 'feedback', name: 'Feedback', component: () => import('@/pages/feedback/FeedbackPage.vue') },
    ],
  },
]

const router = createRouter({
  history: createWebHashHistory(),
  routes,
})

router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('portal_token')
  if (to.meta.requiresAuth && !token) {
    next({ path: '/login', query: { redirect: to.fullPath } })
  } else {
    next()
  }
})

export default router
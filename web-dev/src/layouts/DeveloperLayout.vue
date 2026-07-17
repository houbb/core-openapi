<template>
  <div class="layout">
    <aside class="sidebar">
      <div class="sidebar-brand">Dev Portal</div>
      <nav class="sidebar-nav">
        <router-link to="/app/dashboard" class="nav-item" active-class="active">
          <span class="nav-icon">📊</span> Dashboard
        </router-link>
        <router-link to="/catalog" class="nav-item" active-class="active">
          <span class="nav-icon">🔗</span> API Catalog
        </router-link>
        <router-link to="/app/apps" class="nav-item" active-class="active">
          <span class="nav-icon">📱</span> My Apps
        </router-link>
        <router-link to="/app/credentials" class="nav-item" active-class="active">
          <span class="nav-icon">🔑</span> Credentials
        </router-link>
        <router-link to="/app/playground" class="nav-item" active-class="active">
          <span class="nav-icon">🧪</span> Playground
        </router-link>
        <router-link to="/app/analytics" class="nav-item" active-class="active">
          <span class="nav-icon">📈</span> Analytics
        </router-link>
        <router-link to="/docs" class="nav-item" active-class="active">
          <span class="nav-icon">📖</span> Docs
        </router-link>
        <router-link to="/sdk" class="nav-item" active-class="active">
          <span class="nav-icon">🔧</span> SDK
        </router-link>
        <router-link to="/app/settings" class="nav-item" active-class="active">
          <span class="nav-icon">⚙️</span> Settings
        </router-link>
        <router-link to="/app/feedback" class="nav-item" active-class="active">
          <span class="nav-icon">💬</span> Feedback
        </router-link>
      </nav>
      <div class="sidebar-footer">
        <button class="logout-btn" @click="handleLogout">Logout</button>
      </div>
    </aside>
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

function handleLogout() {
  localStorage.removeItem('portal_token')
  router.push('/login')
}
</script>

<style scoped>
.layout {
  display: flex;
  min-height: 100vh;
}
.sidebar {
  width: 220px;
  background: var(--bg-primary);
  border-right: 1px solid var(--border);
  display: flex;
  flex-direction: column;
  position: fixed;
  top: 0; left: 0; bottom: 0;
}
.sidebar-brand {
  padding: 20px;
  font-size: 17px;
  font-weight: 700;
  color: var(--accent);
  border-bottom: 1px solid var(--border);
}
.sidebar-nav {
  flex: 1;
  padding: 12px 8px;
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  border-radius: 8px;
  color: var(--text-primary);
  font-size: 13px;
  text-decoration: none;
  transition: background 0.15s;
}
.nav-item:hover { background: var(--bg-secondary); }
.nav-item.active { background: var(--accent-bg); color: var(--accent); font-weight: 500; }
.nav-icon { font-size: 16px; width: 20px; text-align: center; }
.sidebar-footer {
  padding: 12px 20px;
  border-top: 1px solid var(--border);
}
.logout-btn {
  width: 100%;
  padding: 8px;
  border: 1px solid var(--border);
  border-radius: 6px;
  background: var(--bg-primary);
  color: var(--text-secondary);
  cursor: pointer;
  font-size: 13px;
}
.logout-btn:hover { background: var(--bg-secondary); }
.main-content {
  margin-left: 220px;
  flex: 1;
  min-height: 100vh;
}
</style>
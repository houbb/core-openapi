<template>
  <div class="layout">
    <aside class="sidebar">
      <div class="sidebar-brand">Open API</div>
      <nav class="sidebar-nav">
        <router-link to="/dashboard" class="nav-item" active-class="active">
          <span class="nav-icon">📊</span>
          <span>仪表盘</span>
        </router-link>
        <router-link to="/services" class="nav-item" active-class="active">
          <span class="nav-icon">⚙️</span>
          <span>服务管理</span>
        </router-link>
        <router-link to="/definitions" class="nav-item" active-class="active">
          <span class="nav-icon">🔗</span>
          <span>API 目录</span>
        </router-link>
        <router-link to="/tags" class="nav-item" active-class="active">
          <span class="nav-icon">🏷️</span>
          <span>标签管理</span>
        </router-link>
        <router-link to="/users" class="nav-item" active-class="active">
          <span class="nav-icon">👤</span>
          <span>用户管理</span>
        </router-link>
        <router-link to="/applications" class="nav-item" active-class="active">
          <span class="nav-icon">📱</span>
          <span>应用管理</span>
        </router-link>
      </nav>
      <div class="sidebar-footer">
        <span class="version">v1.0.0</span>
      </div>
    </aside>
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
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
  top: 0;
  left: 0;
  bottom: 0;
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

.nav-item:hover {
  background: var(--bg-secondary);
}

.nav-item.active {
  background: var(--accent-bg);
  color: var(--accent);
  font-weight: 500;
}

.nav-icon {
  font-size: 16px;
  width: 20px;
  text-align: center;
}

.sidebar-footer {
  padding: 12px 20px;
  border-top: 1px solid var(--border);
}

.version {
  font-size: 11px;
  color: var(--text-tertiary);
}

.main-content {
  margin-left: 220px;
  flex: 1;
  min-height: 100vh;
}
</style>
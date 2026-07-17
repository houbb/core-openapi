<template>
  <div class="public-layout">
    <header class="public-header">
      <div class="header-inner">
        <a href="/#/" class="brand">Core OpenAPI</a>
        <nav class="header-nav">
          <a href="/#/catalog">API Catalog</a>
          <a href="/#/docs">Documentation</a>
          <a href="/#/sdk">SDK</a>
          <a href="/#/login">Login</a>
          <a href="/#/register" class="btn-primary">Register</a>
        </nav>
      </div>
    </header>
    <main class="public-main">
      <router-view />
    </main>
    <footer class="public-footer">
      <p>&copy; 2026 Core Platform. All rights reserved.</p>
    </footer>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
.public-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
.public-header {
  background: var(--bg-primary);
  border-bottom: 1px solid var(--border);
  position: sticky;
  top: 0;
  z-index: 100;
}
.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.brand {
  font-size: 18px;
  font-weight: 700;
  color: var(--accent);
  text-decoration: none;
}
.header-nav {
  display: flex;
  gap: 20px;
  align-items: center;
}
.header-nav a {
  color: var(--text-secondary);
  text-decoration: none;
  font-size: 14px;
}
.header-nav a:hover {
  color: var(--text-primary);
}
.btn-primary {
  background: var(--accent);
  color: #fff !important;
  padding: 6px 16px;
  border-radius: 6px;
}
.public-main {
  flex: 1;
}
.public-footer {
  text-align: center;
  padding: 24px;
  color: var(--text-tertiary);
  font-size: 12px;
  border-top: 1px solid var(--border);
}
</style>
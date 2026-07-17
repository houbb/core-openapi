<template>
  <div class="landing">
    <section class="hero">
      <h1>Build with Core OpenAPI</h1>
      <p class="subtitle">Discover, subscribe, and integrate powerful APIs in minutes.</p>
      <div class="cta">
        <a href="/#/register" class="btn-primary btn-lg">Get Started Free</a>
        <a href="/#/catalog" class="btn-secondary btn-lg">Browse APIs</a>
      </div>
    </section>
    <section class="features">
      <div class="feature-card" v-for="f in features" :key="f.title">
        <div class="feature-icon">{{ f.icon }}</div>
        <h3>{{ f.title }}</h3>
        <p>{{ f.desc }}</p>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
const features = [
  { icon: '📋', title: 'API Catalog', desc: 'Browse and discover APIs across AI, Storage, and more.' },
  { icon: '🔑', title: 'API Keys', desc: 'Generate and manage API keys for your applications.' },
  { icon: '🧪', title: 'Playground', desc: 'Test APIs directly in the browser without writing code.' },
  { icon: '📊', title: 'Analytics', desc: 'Monitor usage, latency, and errors in real-time.' },
  { icon: '🔧', title: 'SDKs', desc: 'Download official SDKs for Java, Python, and more.' },
  { icon: '📖', title: 'Documentation', desc: 'Comprehensive guides and API references.' },
]
</script>

<style scoped>
.landing { max-width: 1200px; margin: 0 auto; padding: 0 24px; }
.hero { text-align: center; padding: 80px 0 60px; }
.hero h1 { font-size: 42px; font-weight: 800; color: var(--text-primary); margin-bottom: 16px; }
.subtitle { font-size: 18px; color: var(--text-secondary); margin-bottom: 32px; }
.cta { display: flex; gap: 16px; justify-content: center; }
.btn-lg { padding: 12px 32px; font-size: 16px; border-radius: 8px; text-decoration: none; }
.btn-primary { background: var(--accent); color: #fff; }
.btn-secondary { background: var(--bg-secondary); color: var(--text-primary); border: 1px solid var(--border); }
.features { display: grid; grid-template-columns: repeat(3, 1fr); gap: 24px; padding: 40px 0 80px; }
.feature-card { padding: 32px 24px; border: 1px solid var(--border); border-radius: 12px; text-align: center; }
.feature-icon { font-size: 32px; margin-bottom: 12px; }
.feature-card h3 { font-size: 16px; font-weight: 600; margin-bottom: 8px; }
.feature-card p { font-size: 13px; color: var(--text-secondary); }
@media (max-width: 768px) { .features { grid-template-columns: 1fr; } }
</style>
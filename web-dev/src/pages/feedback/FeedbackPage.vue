<template>
  <div class="page">
    <h1>Feedback</h1>
    <div class="card" style="max-width: 600px;">
      <form @submit.prevent="submit">
        <div class="form-group">
          <label>Type</label>
          <select v-model="form.type" class="input">
            <option value="SUGGESTION">Suggestion</option>
            <option value="BUG">Bug Report</option>
            <option value="QUESTION">Question</option>
            <option value="OTHER">Other</option>
          </select>
        </div>
        <div class="form-group">
          <label>Title</label>
          <input v-model="form.title" class="input" required />
        </div>
        <div class="form-group">
          <label>Content</label>
          <textarea v-model="form.content" class="input" rows="5"></textarea>
        </div>
        <button type="submit" class="btn-primary">Submit Feedback</button>
      </form>
    </div>

    <h3 style="margin-top: 32px;">My Feedback</h3>
    <div v-if="feedbacks.length">
      <div v-for="fb in feedbacks" :key="fb.id" class="fb-row">
        <div>
          <strong>[{{ fb.type }}] {{ fb.title }}</strong>
          <span class="badge" :class="fb.status">{{ fb.status }}</span>
        </div>
        <p style="font-size:13px;color:var(--text-secondary);">{{ fb.content }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import apiClient from '@/api/client'

const form = ref({ type: 'SUGGESTION', title: '', content: '' })
const feedbacks = ref<any[]>([])

async function submit() {
  await apiClient.post('/feedback', form.value)
  form.value = { type: 'SUGGESTION', title: '', content: '' }
  load()
}

async function load() {
  const { data } = await apiClient.get('/feedback/my')
  feedbacks.value = data.items || []
}

onMounted(load)
</script>

<style scoped>
.page { padding: 32px; }
h1 { font-size: 24px; margin-bottom: 24px; }
.card { background: var(--bg-primary); border: 1px solid var(--border); border-radius: 12px; padding: 32px; }
.form-group { margin-bottom: 16px; }
.form-group label { display: block; font-size: 13px; margin-bottom: 4px; }
.input { width: 100%; padding: 8px 12px; border: 1px solid var(--border); border-radius: 6px; font-size: 14px; box-sizing: border-box; }
.btn-primary { background: var(--accent); color: #fff; border: none; border-radius: 6px; padding: 10px 24px; cursor: pointer; font-size: 14px; }
.fb-row { padding: 16px; background: var(--bg-primary); border: 1px solid var(--border); border-radius: 8px; margin-bottom: 8px; }
.badge { padding: 2px 8px; border-radius: 4px; font-size: 11px; margin-left: 8px; }
.badge.OPEN { background: #bee3f8; color: #2a4365; }
.badge.RESOLVED { background: #c6f6d5; color: #22543d; }
</style>
<script setup>
import { ref, onMounted } from 'vue'
import { api } from '../api'

const items = ref([])
const page = ref(0)
const totalPages = ref(0)
const loading = ref(true)

async function load(p) {
  loading.value = true
  const { data } = await api.get('/posts', { params: { page: p } })
  items.value = data.items
  page.value = data.page
  totalPages.value = data.totalPages
  loading.value = false
}
onMounted(() => load(0))
</script>

<template>
  <p v-if="loading" class="muted">Загрузка…</p>
  <p v-else-if="!items.length" class="muted">Пока нет постов.</p>
  <div class="grid">
    <RouterLink v-for="p in items" :key="p.id" :to="`/post/${p.id}`" class="card">
      <img v-if="p.postType === 'IMAGE'" :src="p.postPath" :alt="p.title" class="thumb" loading="lazy" />
      <video v-else :src="p.postPath + '#t=0.1'" class="thumb" preload="metadata" muted></video>
      <h2>{{ p.title }}</h2>
      <span class="muted">{{ new Date(p.date).toLocaleDateString('ru-RU') }}</span>
    </RouterLink>
  </div>
  <div v-if="totalPages > 1" class="pager">
    <button :disabled="page === 0" @click="load(page - 1)">Назад</button>
    <span class="muted">{{ page + 1 }} из {{ totalPages }}</span>
    <button :disabled="page + 1 >= totalPages" @click="load(page + 1)">Вперёд</button>
  </div>
</template>

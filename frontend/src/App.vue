<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from './stores/auth'

const auth = useAuthStore()
const router = useRouter()

async function logout() {
  await auth.logout()
  router.push('/')
}
</script>

<template>
  <header class="top">
    <RouterLink to="/" class="brand">My artworks</RouterLink>
    <nav>
      <RouterLink to="/about">Профиль</RouterLink>
      <RouterLink v-if="auth.admin" to="/admin">Добавить пост</RouterLink>
      <button v-if="auth.admin" class="link" @click="logout">Выйти из режима автора</button>
    </nav>
  </header>
  <main><RouterView :key="$route.fullPath" /></main>
</template>

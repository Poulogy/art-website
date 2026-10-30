<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { api, errorText } from '../api'

const router = useRouter()
const title = ref('')
const description = ref('')
const file = ref(null)
const busy = ref(false)
const error = ref('')

async function publish() {
  error.value = ''
  busy.value = true
  const fd = new FormData()
  fd.append('title', title.value)
  fd.append('description', description.value)
  fd.append('file', file.value)
  try {
    const { data } = await api.post('/posts', fd)
    router.push(`/post/${data.id}`)
  } catch (e) {
    error.value = errorText(e, 'Не удалось опубликовать.')
  } finally { busy.value = false }
}
</script>

<template>
  <form class="narrow" @submit.prevent="publish">
    <h1>Новый пост</h1>
    <label for="f">Арт или Анимация</label>
    <input id="f" type="file" accept="image/*,video/*" required @change="file = $event.target.files[0]" />
    <label for="t">Title</label>
    <input id="t" v-model="title" maxlength="200" required />
    <label for="d">Описание</label>
    <textarea id="d" v-model="description" rows="5"></textarea>
    <p v-if="error" class="error">{{ error }}</p>
    <p><button type="submit" :disabled="busy">{{ busy ? 'Загружаем…' : 'Опубликовать' }}</button></p>
  </form>
</template>

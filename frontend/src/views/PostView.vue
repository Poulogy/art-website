<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { api, errorText } from '../api'
import { useAuthStore } from '../stores/auth'

const props = defineProps({ id: String })
const auth = useAuthStore()
const router = useRouter()

const post = ref(null)
const comments = ref([])
const name = ref('')
const text = ref('')
const website = ref('')
const error = ref('')
const sending = ref(false)

async function load() {
  post.value = (await api.get(`/posts/${props.id}`)).data
  comments.value = (await api.get(`/posts/${props.id}/comments`)).data
}

async function send() {
  error.value = ''
  sending.value = true
  try {
    const { data } = await api.post(`/posts/${props.id}/comments`,
      { name: name.value, text: text.value, website: website.value })
    comments.value.push(data)
    text.value = ''
    try { localStorage.setItem('commenterName', name.value) } catch {}
  } catch (e) { error.value = errorText(e) }
  finally { sending.value = false }
}

async function removeComment(c) {
  if (!confirm('Удалить комментарий?')) return
  await api.delete(`/comments/${c.id}`)
  comments.value = comments.value.filter((x) => x.id !== c.id)
}

async function removePost() {
  if (!confirm('Удалить пост вместе с файлом?')) return
  await api.delete(`/posts/${props.id}`)
  router.push('/')
}

onMounted(() => {
  try { name.value = localStorage.getItem('commenterName') || '' } catch {}
  load()
})
</script>

<template>
  <article v-if="post">
    <img v-if="post.postType === 'IMAGE'" :src="post.postPath" :alt="post.title" class="media" />
    <video v-else :src="post.postPath" class="media" controls preload="metadata"></video>

    <div class="narrow">
      <h1>{{ post.title }}</h1>
      <p class="muted">{{ new Date(post.date).toLocaleString('ru-RU') }}</p>

      <p class="desc">{{ post.text }}</p>
      <button v-if="auth.admin" class="danger" @click="removePost">Удалить пост</button>

      <h2>Комментарии</h2>
      <p v-if="!comments.length" class="muted">Комментариев пока нет(. Напиши первым 😃.</p>
      <div v-for="c in comments" :key="c.id" class="comment">
        <header>
          <strong>{{ c.aName }}</strong>
          <span class="muted">{{ new Date(c.date).toLocaleString('ru-RU') }}</span>
        </header>
        <p>{{ c.text }}</p>
        <button v-if="auth.admin" class="link" @click="removeComment(c)">Удалить</button>
      </div>

      <form @submit.prevent="send">
        <label for="cn">Имя</label>
        <input id="cn" v-model="name" maxlength="50" autocomplete="nickname" required />
        <label for="ct">Комментарий</label>
        <textarea id="ct" v-model="text" rows="3" maxlength="1000" required></textarea>
        <div class="hp" aria-hidden="true">
          <label for="ws">Не заполняйте это поле >:( </label>
          <input id="ws" v-model="website" tabindex="-1" autocomplete="off" />
        </div>
        <p v-if="error" class="error">{{ error }}</p>
        <p><button type="submit" :disabled="sending">Отправить -></button></p>
      </form>
    </div>
  </article>
  <p v-else class="muted">Загрузка…</p>
</template>

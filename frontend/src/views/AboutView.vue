<script setup>
import { ref, reactive, onMounted } from 'vue'
import { api, errorText } from '../api'
import { useAuthStore } from '../stores/auth'
import PasswordDialog from '../components/PasswordDialog.vue'

const auth = useAuthStore()
const profile = ref(null)
const editing = ref(false)
const dlg = ref(null)
const form = reactive({ name: '', bio: '', iconPath: '', donatePath: '' })
const error = ref('')

onMounted(async () => { profile.value = (await api.get('/profile')).data })

function edit() {
  if (auth.admin) startEdit()
  else dlg.value.open()
}

function startEdit() {
  Object.assign(form, {
    name: profile.value.name, bio: profile.value.bio || '',
    iconPath: profile.value.iconPath || '', donatePath: profile.value.donatePath || ''
  })
  editing.value = true
}

async function save() {
  error.value = ''
  try {
    profile.value = (await api.put('/profile', form)).data
    editing.value = false
  } catch (e) { error.value = errorText(e, 'Ссылки должны начинаться с https://') }
}
</script>

<template>
  <div v-if="profile" class="narrow">
    <template v-if="!editing">
      <img v-if="profile.iconPath" :src="profile.iconPath" alt="" class="avatar" />
      <h1>{{ profile.name }}</h1>
      <p class="desc">{{ profile.bio }}</p>

      <a v-if="profile.donatePath" :href="profile.donatePath" target="_blank" rel="noopener noreferrer" class="btn support">
        Поддержать автора
      </a>
      <p><button class="link" @click="edit">Редактировать профиль</button></p>
    </template>

    <form v-else @submit.prevent="save">
      <label for="n">Имя</label><input id="n" v-model="form.name" required />
      <label for="b">О себе</label><textarea id="b" v-model="form.bio" rows="5"></textarea>
      <label for="a">Ссылка на аватар</label><input id="a" v-model="form.iconPath" />
      <label for="s">Ссылка для поддержки</label><input id="s" v-model="form.donatePath" />
      <p v-if="error" class="error">{{ error }}</p>
      <p><button type="submit">Сохранить</button> <button type="button" class="link" @click="editing = false">Отмена</button></p>
    </form>
  </div>
  <PasswordDialog ref="dlg" @unlocked="startEdit" />
</template>

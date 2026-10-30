<script setup>
import { ref } from 'vue'
import { useAuthStore } from '../stores/auth'

const emit = defineEmits(['unlocked'])
const auth = useAuthStore()
const dlg = ref(null)
const password = ref('')
const error = ref('')
const busy = ref(false)

function open() {
  password.value = ''
  error.value = ''
  dlg.value.showModal()
}

async function submit() {
  busy.value = true
  error.value = ''
  try {
    await auth.login(password.value)
    dlg.value.close()
    emit('unlocked')
  } catch (e) {
    error.value = e?.response?.status === 429
      ? 'Слишком много попыток. Подождите 15 минут.'
      : 'Неверный пароль'
  } finally { busy.value = false }
}

defineExpose({ open })
</script>

<template>
  <dialog ref="dlg" class="dialog">
    <form @submit.prevent="submit">
      <h2>Личный пароль автора 😈</h2>
      <label for="pw">Пароль</label>
      <input id="pw" v-model="password" type="password" autocomplete="current-password" required />
      <p v-if="error" class="error">{{ error }}</p>
      <p>
        <button type="submit" :disabled="busy">Продолжить ⭐</button>
        <button type="button" class="link" @click="dlg.close()">Отмена</button>
      </p>
    </form>
  </dialog>
</template>

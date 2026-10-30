import { defineStore } from 'pinia'
import { api } from '../api'

export const useAuthStore = defineStore('auth', {
  state: () => ({ admin: false }),
  actions: {
    async fetchMe() {
      try { this.admin = (await api.get('/auth/me')).data.admin } catch { this.admin = false }
    },
    async login(password) {
      await api.post('/auth/login', { password })
      this.admin = true
    },
    async logout() {
      await api.post('/auth/logout')
      this.admin = false
    }
  }
})

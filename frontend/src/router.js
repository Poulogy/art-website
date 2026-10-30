import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from './stores/auth'
import HomeView from './views/HomeView.vue'
import PostView from './views/PostView.vue'
import AboutView from './views/AboutView.vue'
import AdminView from './views/AdminView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: HomeView },
    { path: '/post/:id', component: PostView, props: true },
    { path: '/about', component: AboutView },
    { path: '/admin', component: AdminView, meta: { admin: true } }
  ]
})

router.beforeEach((to) => {
  if (to.meta.admin && !useAuthStore().admin) return '/about'
})

export default router

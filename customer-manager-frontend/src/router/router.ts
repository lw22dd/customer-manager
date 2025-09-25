import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '@/views/login.vue'
import WelcomeView from '@/views/welcome.vue'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: LoginView },
  { path: '/welcome', component: WelcomeView },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
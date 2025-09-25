import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '@/views/login.vue'
import WelcomeView from '@/views/welcome.vue'
import CustomerView from '@/views/index.vue'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: LoginView },
  { path: '/welcome', component: WelcomeView },
  { path: '/index', component: CustomerView },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
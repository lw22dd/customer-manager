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

// 添加路由拦截器
router.beforeEach((to, from, next) => {
  // 判断用户是否已登录（从 localStorage 中获取 token）
  const token = localStorage.getItem('token')
  
  // 如果用户未登录，且访问的不是登录页面，则重定向到登录页面
  if (!token && to.path !== '/login') {
    console.log('未登录')
    next('/login')
  } 
  // 如果用户已登录，且访问的是登录页面，则重定向到欢迎页面
  else if (token && to.path === '/login') {
    console.log('已登录')
    next('/index')
  } 
  // 其他情况，允许访问
  else {
    next()
  }
})

export default router
import { createApp } from 'vue'
import './index.css'
import App from './App.vue'
import { createPinia } from 'pinia'
import router from './router/router'
// 导入Element Plus
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
// 导入Element Plus的中文语言包
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'

const pinia = createPinia()
const app = createApp(App)
app.use(router)
app.use(pinia)
app.use(ElementPlus, { locale: zhCn })
app.mount('#app')

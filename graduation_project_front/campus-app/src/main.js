import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import router from './router'
import App from './App.vue'
import './assets/styles/global.scss'
import { setupGlobalErrorHandlers } from '@/utils/errorHandler'
import { formatImageUrl, getAvatarUrl } from '@/utils/image'

const app = createApp(App)
const pinia = createPinia()

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.config.globalProperties.$formatImageUrl = formatImageUrl
app.config.globalProperties.$getAvatarUrl = getAvatarUrl

app.use(pinia)
app.use(router)
app.use(ElementPlus)

setupGlobalErrorHandlers(app)

app.mount('#app')


import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import '@/common/styles/gobal.css'
import '@/music/styles/element/index.scss'
import '@/music/styles/globalScrollbar.css'

import App from './App.vue'
import { createPinia } from 'pinia'
import router from '@/music/router' ; // 导入路由配置


const app: ReturnType<typeof createApp> = createApp(App);
app.use(ElementPlus)
app.use(createPinia())
app.use(router)
app.mount('#app')

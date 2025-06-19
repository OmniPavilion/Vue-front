import '@/application/styles/global.css';
import '@/application/styles/mouse.css';

import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

import App from './App.vue'
import { createPinia } from 'pinia'
import router from '@/application/router' ; // 导入路由配置


const app: ReturnType<typeof createApp> = createApp(App);
app.use(ElementPlus)
app.use(createPinia())
app.use(router)
app.mount('#app')

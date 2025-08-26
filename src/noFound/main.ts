import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import '@/common/styles/gobal.css'

import App from './App.vue'


const app: ReturnType<typeof createApp> = createApp(App);
app.use(ElementPlus)
app.mount('#app')

import {Notebook, Headset, Reading} from '@element-plus/icons-vue'
import type {Component} from "vue";

export interface Feature {
    name: string
    desc: string
    icon: Component
    link: string
    available: boolean
}

export const features: Feature[] = [
    {
        name: '记事本',
        desc: '简单高效的个人记事本，支持Markdown格式和分类管理，随时随地记录灵感与待办事项。',
        icon: Notebook,
        link: '/diary',
        available: true
    },
    {
        name: '音乐播放器',
        desc: '支持本地与在线音乐播放，创建个性化歌单，享受高品质音乐体验。',
        icon: Headset,
        link: '/music',
        available: true
    },
    {
        name: '文集',
        desc: '以MarkDown格式收集管理文章，支持文章阅读、编辑，分类，提供沉浸式阅读体验。',
        icon: Reading,
        link: '/article',
        available: true
    },
]

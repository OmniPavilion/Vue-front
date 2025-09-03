import {defineStore} from 'pinia'
import {ref} from 'vue'

export const useHomeStore = defineStore('homeStore', () => {
    const weatherType = ref<string>('sunny')
    const activeMenuItem = ref<string>('/')
    return {
        weatherType,
        activeMenuItem,
    }
})

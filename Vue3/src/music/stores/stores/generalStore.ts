import {defineStore} from 'pinia';
import {ref} from 'vue';



export const useGeneralStore = defineStore('general', () => {
    const CurrentPage = ref<'music' | 'singer'>('music');


    return {
        CurrentPage
    };
});

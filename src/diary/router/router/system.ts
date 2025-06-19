import type { RouteRecordRaw } from 'vue-router';
import Start from '@/application/views/start/index.vue';

const aboutRoutes: RouteRecordRaw[] = [
    {
        path: '/',
        name: 'start',
        component: Start
    }
];

export default aboutRoutes;

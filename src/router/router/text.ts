import type { RouteRecordRaw } from 'vue-router';
import About from '@/components/HelloWorld.vue';

const aboutRoutes: RouteRecordRaw[] = [
    {
        path: '/',
        name: 'About',
        component: About
    }
];

export default aboutRoutes;

import {createRouter, createWebHistory} from 'vue-router';
import type {RouteRecordRaw} from 'vue-router';
import englishRoutes from './router/system';

const routes: RouteRecordRaw[] = [
    ...englishRoutes
];

const router = createRouter({
    history: createWebHistory(),
    routes
});

export default router;

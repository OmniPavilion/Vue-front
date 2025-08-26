import {createRouter, createWebHistory} from 'vue-router';
import type {RouteRecordRaw} from 'vue-router';
import textRoutes from './router/system';

const routes: RouteRecordRaw[] = [
    ...textRoutes
];

const router = createRouter({
    history: createWebHistory(),
    routes
});

export default router;

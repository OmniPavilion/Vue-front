import type { RouteRecordRaw } from 'vue-router';

const englishRoutes: RouteRecordRaw[] = [
    {
        path: '/english',
        name: 'english',
        redirect: '/english/home',
        component: () => import('@/english/views/container/index.vue'),
        children: [
            {
                path: 'home',
                name: 'home',
                component: () => import('@/english/views/home/index.vue'),
            },
            {
                path: 'net',
                name: 'net',
                component: () => import('@/english/views/net/index.vue'),
            },
            {
                path: 'library',
                name: 'library',
                component: () => import('@/english/views/library/index.vue'),
            },
            {
                path: 'book',
                name: 'book',
                component: () => import('@/english/views/book/index.vue'),
            },
        ]
    },
    {
        path: '/:pathMatch(.*)*',
        name: '404',
        component: () => import('@/common/components/404/index.vue')
    },
];

export default englishRoutes;

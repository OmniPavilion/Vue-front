import type { RouteRecordRaw } from 'vue-router';

const aboutRoutes: RouteRecordRaw[] = [
    {
        path: '/diary',
        name: 'diary',
        redirect: '/diary/log',
        component: () => import('@/diary/views/main/index.vue'),
        children: [
            {
                path: '/diary/log',
                name: 'log',
                component: () => import('@/diary/views/log/index.vue'),
            },
            {
                path: '/diary/stats',
                name: 'stats',
                component: () => import('@/diary/views/stats/index.vue'),
            },
            {
                path: '/diary/history',
                name: 'history',
                component: () => import('@/diary/views/history/index.vue'),
            },
        ]
    },
     {
         path: '/:pathMatch(.*)*',
         name: '404',
         component: () => import('@/common/components/404/index.vue'),
     }
];

export default aboutRoutes;

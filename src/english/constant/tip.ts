import type { Component } from 'vue';
import { HomeFilled, Coin, Notebook, Document } from '@element-plus/icons-vue';

export type Tip = {
    name: string;
    route: string;
    icon?: Component;
    color?: string;
    desc?: string;
}

export const tip: Tip[] = [
    {
        name: '首页',
        route: '/home',
        icon: HomeFilled,
        color: '#FF9AA2',
        desc: '英韵阁系统主页，展示学习概览、个性化数据和快速访问入口'
    },
    {
        name: 'NetWord词网',
        route: '/net',
        icon: Coin,
        color: '#FFB7B2',
        desc: '基于WordNet构建的语义网络，通过同义词集和语义关系(上下位、同义、反义等)深度解析词汇关联，打造立体化词汇学习体验'
    },
    {
        name: '单词库',
        route: '/library',
        icon: Notebook,
        color: '#FFDAC1',
        desc: '系统化分类的英语词汇资源库，涵盖四级、六级等多类型词库，支持按分类和英文筛选'
    },
    {
        name: '单词表',
        route: '/book',
        icon: Document,
        color: '#E2F0CB',
        desc: '个性化单词收藏与管理空间，支持自定义创建单词表、标记掌握程度，助力高效记忆与复习'
    }
];
// stores/logStore.ts
import {defineStore} from 'pinia';
import {ref} from 'vue';
import type {DailyLogVO} from '@/diary/types/vo/DailyLogVO';
import {logApi} from "@/diary/api/logApi";
import type {LogQuery} from "@/diary/types/dto/LogQuery";
import type {Result} from "@/common/types/vo/Result";
import type {PageDTO} from "@/common/types/dto/PageDTO";
import type {AxiosResponse} from "axios";
import type {PageVO} from "@/common/types/vo/PageVO";

export const useLogStore = defineStore('log', () => {
    // 状态
    const logList = ref<DailyLogVO[]>([]);
    const total = ref(0);
    const loading = ref(false);
    const pageQuery = ref<PageDTO<LogQuery>>({
        pageNum: 1,
        pageSize: 100,
        order: 'DESC',
    });

    // 方法
    const createLog = async (log: DailyLogVO) => {
        console.log('创建日志参数', log);
        loading.value = true;
        try {
            const res = await logApi.createLog(log);
            console.log('创建日志结果', res.data);
            return res.data
        } finally {
            loading.value = false;
        }
    };

    const deleteLog = async (id: number) => {
        loading.value = true;
        try {
            const res = await logApi.deleteLog(id);
            // 删除后更新本地列表
            logList.value = logList.value.filter(item => item.id !== id);
            return res.data;
        } finally {
            loading.value = false;
        }
    };

    const updateLog = async (log: DailyLogVO) => {
        loading.value = true;
        try {
            const res = await logApi.updateLog(log);
            // 更新本地数据
            const index = logList.value.findIndex(item => item.id === log.id);
            if (index !== -1) {
                logList.value[index] = log;
            }
            return res.data;
        } finally {
            loading.value = false;
        }
    };

    const fetchLog = async (id: number) => {
        loading.value = true;
        try {
            return await logApi.getLog(id);
        } finally {
            loading.value = false;
        }
    };

    const fetchLogsByPage = async () => {
        console.log('分页查询日志参数', pageQuery.value);
        loading.value = true;
        const res: AxiosResponse<Result<PageVO<DailyLogVO>>> = await logApi.getLogsByPage(pageQuery.value);
        logList.value = res.data.data.rows || [];
        total.value = res.data.data.total || 0;
        console.log('分页查询日志', logList.value);
        console.log('分页查询日志总数', total.value);
        loading.value = false;
        return res.data;
    };

    const isTodayHasLog = async () => {
        const res: AxiosResponse<Result<PageVO<DailyLogVO>>> = await logApi.getLogsByPage({
            pageNum: 1,
            pageSize: 1,
            order: 'DESC',
            query: {
                startDate: new Date().toISOString().split('T')[0],
                endDate: new Date().toISOString().split('T')[0],
            }
        });
        const total = res.data.data.total;
        const count = res.data.data.rows[0]?.logs.length;
        console.log('今天是否创建日志', total)
        console.log('今天日志条数', count)
        return total > 0 && count > 0;
    }

    return {
        // 状态
        logList,
        loading,
        total,
        pageQuery,


        // 方法
        createLog,
        deleteLog,
        updateLog,
        fetchLog,
        fetchLogsByPage,
        isTodayHasLog
    };
});

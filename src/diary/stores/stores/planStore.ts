import {defineStore} from 'pinia'
import {planApi} from '@/diary/api/planApi'
import type {PageDTO} from '@/common/types/dto/PageDTO'
import type {PlanVO} from '@/diary/types/vo/PlanVO'
import {Status} from '@/diary/types/vo/Status'
import {ref} from 'vue'

export const usePlanStore = defineStore('plan', () => {
    // ============ 状态定义 ============
    const plans = ref<PlanVO[]>([])                // 计划列表数据
    const total = ref(0)                         // 计划总数
    const loading = ref(false)                        // 加载状态

    // 分页查询参数
    const pageQuery = ref<PageDTO<Status>>({
        pageNum: 1,      // 当前页码
        pageSize: 10,    // 每页条数
        order: 'DESC',    // 排序方式
    })

    const createPlan = async (plan: PlanVO) => {
        console.log('创建计划', plan)
        loading.value = true
        try {
            const res = await planApi.createPlan(plan)
            console.log('创建计划结果', res)
            return res.data
        } finally {
            loading.value = false
        }
    }

    const updatePlan = async (plan: PlanVO) => {
        console.log('更新计划', plan)
        loading.value = true
        try {
            const res = await planApi.updatePlan(plan)
            console.log('更新计划结果', res)
            return res.data
        } finally {
            loading.value = false
        }
    }

    const deletePlan = async (id: number) => {
        console.log('删除计划', id)
        loading.value = true
        try {
            const res = await planApi.deletePlan(id)
            console.log('删除计划结果', res)
            return res.data
        } finally {
            loading.value = false
        }
    }

    const getPlanById = async (id: number) => {
        console.log('获取计划', id)
        loading.value = true
        try {
            const res = await planApi.getPlanById(id)
            console.log('获取计划结果', res)
            return res.data
        } finally {
            loading.value = false
        }
    }

    const fetchPlansByPage = async () => {
        console.log('分页查询计划', pageQuery.value)
        loading.value = true
        try {
            const res = await planApi.getPlansByPage(pageQuery.value)
            console.log('分页查询计划结果', res.data)
            plans.value = res.data.data.rows
            total.value = res.data.data.total
            return res.data
        } finally {
            loading.value = false
        }
    }

    const getPlanGoingOn = async () => {
        const res = await planApi.getPlansByPage({
            pageNum: 1,
            pageSize: 2147483647,
            order: 'ASC',
        })

        const plans =  res.data.data.rows.filter(plan => {
            return plan.status === Status.IN_PROGRESS;
        })

        console.log("正在进行的计划数：", plans.length)

        return plans
    }

    return {
        loading,
        pageQuery,
        plans,
        total,

        createPlan,
        updatePlan,
        deletePlan,
        getPlanById,
        fetchPlansByPage,
        getPlanGoingOn,
    }
})

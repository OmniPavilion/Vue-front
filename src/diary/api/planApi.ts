import { myAxios } from '@/common/utils/axios';
import type { Result } from '@/common/types/vo/Result';
import type { PageVO } from '@/common/types/vo/PageVO';
import type { PageDTO } from '@/common/types/dto/PageDTO';
import type { PlanVO } from '@/diary/types/vo/PlanVO';
import { Status } from '@/diary/types/vo/Status';
import type { AxiosResponse } from "axios";

export const planApi = {
    // 创建计划
    async createPlan(plan: PlanVO): Promise<AxiosResponse<Result<number>>> {
        return await myAxios.post('/diary/plans', plan);
    },

    // 更新计划
    async updatePlan(plan: PlanVO): Promise<AxiosResponse<Result<void>>> {
        return await myAxios.put('/diary/plans', plan);
    },

    // 删除计划
    async deletePlan(id: number): Promise<AxiosResponse<Result<void>>> {
        return await myAxios.delete(`/diary/plans/${id}`);
    },

    // 获取单个计划
    async getPlanById(id: number): Promise<AxiosResponse<Result<PlanVO>>> {
        return await myAxios.get(`/diary/plans/${id}`);
    },

    // 分页获取计划
    async getPlansByPage(pageDTO: PageDTO<Status>): Promise<AxiosResponse<Result<PageVO<PlanVO>>>> {
        return await myAxios.post('/diary/plans/page', pageDTO);
    }
};

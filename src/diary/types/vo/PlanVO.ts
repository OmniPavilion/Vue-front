import { Status } from "./Status";
import type {PlanTaskVO} from "@/diary/types/vo/PlanTaskVO";

export interface PlanVO {
    id?: number;
    title: string;
    content: string;
    startDate: string; // ISO格式日期字符串 "YYYY-MM-DD"
    endDate: string;   // ISO格式日期字符串 "YYYY-MM-DD"
    status: Status;
    tasks: PlanTaskVO[];
    createdAt?: string;
    updatedAt?: string;
}

// 创建计划的DTO
export interface CreatePlanDto {
    title: string;
    content: string;
    startDate: string;
    endDate: string;
    status: Status;
}

// 更新计划的DTO
export interface UpdatePlanDto extends Partial<CreatePlanDto> {
    id: number;
}

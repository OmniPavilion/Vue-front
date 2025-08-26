import { Status } from "./Status";

export interface PlanTaskVO {
    id?: number;
    title: string;
    description: string;
    status: Status;
    createdAt?: string;
    updatedAt?: string;
}

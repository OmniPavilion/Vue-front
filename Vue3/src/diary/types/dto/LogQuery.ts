import type {Category} from "@/diary/types/vo/Category";

export interface LogQuery {
    category?: Category | undefined;
    startDate?: string |  undefined;
    endDate?: string |  undefined;
}

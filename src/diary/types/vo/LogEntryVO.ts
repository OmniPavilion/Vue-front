import type {Category} from "@/diary/types/vo/Category";

export interface LogEntryVO {
    activity: string;
    category: Category;
    subcategory?: string;
}

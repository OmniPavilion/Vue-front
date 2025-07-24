import type {Status} from "@/diary/types/vo/Status";

export interface NoteVO {
    id?: number;
    content: string;
    dueDate: string;
    dueTime: string | null;
    status: Status;
    notes?: string;
}
